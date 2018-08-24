package cn.nineox.robot.monitor.common.tutk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.nineox.robot.monitor.R;


public class CustomAlertDialog extends Dialog implements View.OnClickListener {

	private TextView tv_title, tv_content;
	private Button btn_single, btn_left, btn_right;

	private OnDialogSingleClickLister singleClickLister;
	private OnDialogClickLister       clickLister;


	public CustomAlertDialog(Context context, String content, String btnSingle) {
		this(context, R.style.CustomProgressDialog, null, content, btnSingle);
	}

	public CustomAlertDialog(Context context, String title, String content, String btnSingle) {
		this(context, R.style.CustomProgressDialog, title, content, btnSingle);
	}

	public CustomAlertDialog(Context context, String title, String content, String btnLeft, String btnRight) {
		this(context, R.style.CustomProgressDialog, title, content, btnLeft, btnRight);
	}

	private CustomAlertDialog(Context context, int theme, String title, String content, String btnSingle) {
		super(context, theme);

		setContentView(R.layout.layout_dialog_single);

		getWindow().getAttributes().gravity = Gravity.CENTER;
//        getWindow().setLayout(WindowUtils.getDefaultDisplayWidth(context) * 6 / 10, WindowManager.LayoutParams.WRAP_CONTENT);
		getWindow().setWindowAnimations(R.style.window_anim_style);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_content = (TextView) findViewById(R.id.tv_content);
		btn_single = (Button) findViewById(R.id.btn_single);

		if (title != null && tv_title != null) {
			tv_title.setVisibility(View.VISIBLE);
			tv_title.setText(title);
		} else {
			tv_title.setVisibility(View.GONE);
		}
		if (content != null && tv_content != null) {
			tv_content.setText(content);
		}
		if (btnSingle != null && btn_single != null) {
			btn_single.setText(btnSingle);
		}

		btn_single.setOnClickListener(this);

		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	private CustomAlertDialog(Context context, int theme, String title, String content, String btnLeft, String btnRight) {
		super(context, theme);

		setContentView(R.layout.layout_dialog_ok_cancle);
		getWindow().getAttributes().gravity = Gravity.CENTER;
//		getWindow().setLayout(WindowUtils.getDefaultDisplayWidth(context) * 6 / 10, WindowManager.LayoutParams.WRAP_CONTENT);
		getWindow().setWindowAnimations(R.style.window_anim_style);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_content = (TextView) findViewById(R.id.tv_content);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);

		if (title != null && tv_title != null) {
			tv_title.setVisibility(View.VISIBLE);
			tv_title.setText(title);
		} else {
			tv_title.setVisibility(View.GONE);
		}
		if (content != null && tv_content != null) {
			tv_content.setText(content);
		}
		if (btnLeft != null && btn_left != null) {
			btn_left.setText(btnLeft);
			btn_left.setOnClickListener(this);
		}
		if (btnRight != null && btn_right != null) {
			btn_right.setText(btnRight);
			btn_right.setOnClickListener(this);
		}

		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	public void setMessage(String content) {
		if (content != null && tv_content != null) {
			tv_content.setText(content);
		}
	}

	@Override
	public void onClick(View view) {

		int id = view.getId();
		if (btn_single != null && id == btn_single.getId()) {
			single_Click(this);
		} else if (btn_left != null && id == btn_left.getId()) {
			left_Click(this);
		} else if (btn_right != null && id == btn_right.getId()) {
			right_Click(this);
		}
	}

	private void single_Click(DialogInterface dialog) {
		dialog.dismiss();
		if (singleClickLister != null) {
			singleClickLister.okClick(dialog);
		}
	}

	private void right_Click(DialogInterface dialog) {
		dialog.dismiss();
		if (clickLister != null) {
			clickLister.rightClick(dialog);
		}
	}

	private void left_Click(DialogInterface dialog) {
		dialog.dismiss();
		if (clickLister != null) {
			clickLister.leftClick(dialog);
		}
	}

	public void setOnDialogSingleClickListener(OnDialogSingleClickLister clickListener) {
		this.singleClickLister = clickListener;
	}

	public void setOnDialogClickListener(OnDialogClickLister clickListener) {
		this.clickLister = clickListener;
	}

	public interface OnDialogSingleClickLister {
		public void okClick(DialogInterface dialog);
	}

	public interface OnDialogClickLister {
		public void leftClick(DialogInterface dialog);

		public void rightClick(DialogInterface dialog);
	}

}
