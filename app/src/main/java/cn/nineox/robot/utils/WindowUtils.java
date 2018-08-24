package cn.nineox.robot.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import cn.nineox.robot.PluginAPP;
import cn.nineox.robot.R;

import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * Created by Zed on 2017/5/24 20:16.
 * dec:
 */

public class WindowUtils {

	//设置状态栏颜色  是否覆盖状态栏
	public static void coverMode(Activity activity, int color, boolean isCover) {
		Window window = activity.getWindow();

		if (isCover) {
			//设置透明状态栏,这样才能让 ContentView 向上
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		} else {
			//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		//设置状态栏颜色
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			window.setStatusBarColor(color);
		}

		ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
		View mChildView = mContentView.getChildAt(0);
		if (mChildView != null) {
			//注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 是否预留出系统 View 的空间.
			ViewCompat.setFitsSystemWindows(mChildView, !isCover);
		}
	}

	//沉浸式  activity 全屏显示 在onWindowFocusChanged调用
	public static void fullWindow(Activity activity, boolean hasFocus) {
		if (hasFocus && Build.VERSION.SDK_INT >= 19) {
			View decorView = activity.getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	//status height
	public static int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = activity.getResources().getDimensionPixelSize(i5);
			} catch (ClassNotFoundException | IllegalAccessException
					| InstantiationException | IllegalArgumentException
					| SecurityException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	//action bar height
	public static int getActionBarHeight(Activity activity) {

		int actionBarHeight = 0;

		final TypedValue tv = new TypedValue();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
				actionBarHeight = TypedValue.complexToDimensionPixelSize(
						tv.data, activity.getResources().getDisplayMetrics());
			}
		} else {
			// 使用android.support.v7.appcompat包做actionbar兼容的情况
			if (activity.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true)) {
				actionBarHeight = TypedValue.complexToDimensionPixelSize(
						tv.data, activity.getResources().getDisplayMetrics());
			}

		}
		return actionBarHeight;
	}

	public static int getDefaultDisplayHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	public static int getDefaultDisplayWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	public static int setHeightByDisplay(Context context, View view, int weight) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = getDefaultDisplayHeight(context) * weight;
		view.setLayoutParams(layoutParams);
		return layoutParams.height;
	}

	public static int setWidthByDisplay(Context context, View view, int weight) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.width = getDefaultDisplayWidth(context) * weight;
		view.setLayoutParams(layoutParams);
		return layoutParams.width;
	}

	/**
	 * dp-->px
	 */
	public static int dp2Px(Context context, int dp) {
		// 1.px/dp = density ==> px和dp倍数关系
		// 2.px/(ppi/160) = dp ==>ppi
		float density = context.getResources().getDisplayMetrics().density;
		// int ppi = getResources().getDisplayMetrics().densityDpi;//160 240 320
		int px = (int) (dp * density + 0.5f);
		return px;
	}

	/**
	 * px-->dp
	 */
	public static int px2Dp(Context context, int px) {
		// 1.px/dp = density ==> px和dp倍数关系
		float density = context.getResources().getDisplayMetrics().density;
		int dp = (int) (px / density + 0.5f);
		return dp;
	}

	/**
	 * 获取app顶层页面
	 */
	public static Activity getTopActivity() {
		PluginAPP application = getMediaSDKApplication();
		if (application != null) {
			return application.getTopActivity();
		}
		return null;
	}

	/**
	 * 拿到手机顶层页面 如果不是app的页面则返回null
	 */
	public static Activity getActivity() {
		Class activityThreadClass = null;
		try {
			activityThreadClass = Class.forName("android.app.ActivityThread");
			Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
			Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
			activitiesField.setAccessible(true);
			Map activities = (Map) activitiesField.get(activityThread);
			for (Object activityRecord : activities.values()) {
				Class activityRecordClass = activityRecord.getClass();
				Field pausedField = activityRecordClass.getDeclaredField("paused");
				pausedField.setAccessible(true);
				if (!pausedField.getBoolean(activityRecord)) {
					Field activityField = activityRecordClass.getDeclaredField("activity");
					activityField.setAccessible(true);
					return (Activity) activityField.get(activityRecord);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 拿到app的application
	 *
	 * @return
	 */
	public static PluginAPP getMediaSDKApplication() {
		Application application = null;
		try {
			Class clazz = Class.forName("android.app.ActivityThread");
			final Method method = clazz.getMethod("currentActivityThread", new Class[0]); // get target activity thread
			Object object = method.invoke(null, (Object[]) null);
			final Method method1 = clazz.getMethod("getApplication");
			return (PluginAPP) method1.invoke(object, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setImage(Context context, String url, ImageView imageView, int height, int with, RequestListener listener) {

		if (url == null) {
			return;
		}

		WeakReference<Context> reference = new WeakReference<>(context);

		Glide.with(context).load(url)
				.listener(listener)
				.override(WindowUtils.dp2Px(reference.get(), height), WindowUtils.dp2Px(reference.get(), with))
				.dontAnimate()
				.placeholder(R.drawable.ic_portrait)
				.into(imageView);
	}

	public static void openSystemImage(Context context, int request) {

		if (Build.VERSION.SDK_INT < KITKAT) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			((Activity) context).startActivityForResult(intent, request);
		} else {
			Intent intent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			((Activity) context).startActivityForResult(intent, request);
		}

	}

	public static String getSystemImagePath(Context context, Uri uri) {
		String[] imgPath = {MediaStore.Images.Media.DATA};
		Cursor cursor = ((Activity) context).managedQuery(uri, imgPath, null, null, null);
		int indexOrThrow = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(indexOrThrow);
		Log.i("getSystemImagePath", "picturePath = " + path);
		return path;
	}

}
