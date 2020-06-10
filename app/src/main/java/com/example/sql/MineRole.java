package com.example.sql;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MineRole extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_role);

        ActionBar actionBar = getSupportActionBar();//左上角返回
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userid = user.getString("userid","");//从缓存获取侠客id

        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objectId",userid);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    System.out.println("查询成功" + object.size());
                    initView(object.get(0));
                } else {
                    if(e.getErrorCode() == 9010){
                        Toast.makeText(MineRole.this, "网络请求超时，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                    System.out.println("查询失败：" + e);
                }
            }
        });
    }

    //设置界面
    private void initView(User user) {
        System.out.println("名"+user.getUsername()+"级"+user.getLevel()+"类型"+user.getType()+"共"+user.getAttack());
        TextView username = findViewById(R.id.info_name);
        username.setText(user.getUsername());

        TextView level = findViewById(R.id.info_level);
        level.setText(user.getLevel() + "");

        TextView exp = findViewById(R.id.info_exp);
        exp.setText(user.getAccExp() + "/" + user.getReqExp() + "");

        TextView type = findViewById(R.id.info_type);
        type.setText(user.getType() + "");
        setColor(type);

        TextView attack = findViewById(R.id.info_attack);
        attack.setText(user.getAttack() + "");
        TextView defense = findViewById(R.id.info_defense);
        defense.setText(user.getDefense() + "");
        TextView speed = findViewById(R.id.info_speed);
        speed.setText(user.getSpeed() + "");
        TextView blood = findViewById(R.id.info_blood);
        blood.setText(user.getBlood() + "");
    }

    //设置名声颜色
    private void setColor(TextView type) {
        switch (type.getText().toString()){//设置等级颜色
            case "普普通通":{
                type.setTextColor(Color.BLACK);
                break;
            }
            case "门派弟子":{
                type.setTextColor(Color.GREEN);
                break;
            }
            case "小有名气":{
                type.setTextColor(Color.BLUE);
                break;
            }
            case "一流侠客":{
                type.setTextColor(Color.parseColor("#800080"));
                break;
            }
            case "绝世高手":{
                type.setTextColor(Color.RED);
                break;
            }
            case "主角光环":{
                type.setTextColor(Color.parseColor("#FFD700"));
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
