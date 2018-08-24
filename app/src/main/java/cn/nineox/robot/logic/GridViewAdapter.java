//package cn.nineox.robot.logic;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//import java.util.List;
//
//import cn.nineox.robot.logic.bean.Menu;
//
//public class GridViewAdapter extends BaseAdapter {
//    private Context
//
//    private List<Menu> menus;
//
//    public GridViewAdapter(Context context,List<Menu> menus){
//        this
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageView imageview; // 声明ImageView的对象
//        if (convertView == null) {
//
//        } else {
//            imageview = (ImageView) convertView;
//        }
//        imageview.setImageResource(imageId[position]); // 为ImageView设置要显示的图片
//        return imageview; // 返回ImageView
//    }
//
//    /*
//     * 功能：获得当前选项的ID
//     *
//     * @see android.widget.Adapter#getItemId(int)
//     */
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    /*
//     * 功能：获得当前选项
//     *
//     * @see android.widget.Adapter#getItem(int)
//     */
//    @Override
//    public Menu getItem(int position) {
//        return menus.get(position);
//    }
//
//    /*
//     * 获得数量
//     *
//     * @see android.widget.Adapter#getCount()
//     */
//    @Override
//    public int getCount() {
//        if (menus = null) {
//            return 0;
//        }
//        return menus.size();
//    }
//}