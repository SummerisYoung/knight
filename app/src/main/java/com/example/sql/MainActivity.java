package com.example.sql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> maps;//每一地图数组
    ArrayList<ArrayList> arrayLists = new ArrayList<>();//全部地图数组
    private FlexboxLayout flexboxLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flexboxLayout = findViewById(R.id.flexbox_map);

        //获取地图数据
        BmobQuery<Map> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<Map>() {
            @Override
            public void done(List<Map> list, BmobException e) {
                if (e == null) {
                    //list相当于一个CartoonsListEntity集合可以直接取对象使用
//                    System.out.println("查询成功:" + list.size());
                    for (Map data:list) {
                        maps = new ArrayList<>();//每次创建新的地图数组引用
                        maps.add(data.getObjectId());//填入id
                        maps.add(data.getName());//填入名字
                        maps.add(data.getDescription());//填入描述

                        arrayLists.add(maps);//填入地图数组
                    }

                    for(int i = 0;i < arrayLists.size();i++) {
                        flexboxLayout.addView(getFlexboxLayoutItemView(arrayLists.get(i)));
                    }
                } else {
                    if(e.getErrorCode() == 9010){
                        Toast.makeText(MainActivity.this, "网络请求超时，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                    //失败直接打印即可
                    System.out.println("查询失败:" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

        //进入侠客页面
        Button mine = findViewById(R.id.mine);
        mine.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MineRole.class);
            startActivity(intent);
        });
    }

    //获取FlexboxLayout的子View
    private View getFlexboxLayoutItemView(ArrayList list) {
        LinearLayout container = new LinearLayout(this);
        Button item = new Button(this);
        item.setText(list.get(1).toString());
        item.setWidth(120);
        //按钮点击事件
        item.setOnClickListener(v -> diaLogShow(list));
        container.addView(item);
        return container;
    }

    //底部菜单
    private void diaLogShow(ArrayList list) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_menu, null);//获取布局view
        Dialog dialog = new Dialog(MainActivity.this, R.style.bottom_dialog);//创建dialog
        TextView textView = view.findViewById(R.id.map_description);//获取文本组件
        Button button = view.findViewById(R.id.map_in);//获取进入按钮
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RoleActivity.class);
            intent.putStringArrayListExtra("map",list);//通过这种方法传递list，即ArrayList<String> map
            startActivity(intent);
        });
        textView.setText(list.get(2).toString());
        dialog.setContentView(view);//把dialog设置到布局上
        Window dialogWindow = dialog.getWindow();//获取dialog窗体
        dialogWindow.setGravity(Gravity.BOTTOM);//设置显示在底部
        dialogWindow.setWindowAnimations(R.style.dialog_style);//设置动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();//获取窗体属性

        lp.width = getResources().getDisplayMetrics().widthPixels;
        lp.height = (int) (getResources().getDisplayMetrics().heightPixels / 2);
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
