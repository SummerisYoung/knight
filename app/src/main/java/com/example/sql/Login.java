package com.example.sql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //连接bomb后端云
        Bmob.initialize(this, "83450349cbe9abda1da42a10b683a282");

        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);

        Button loginBtn = findViewById(R.id.login);
        Button signupBtn = findViewById(R.id.signup);

        loginBtn.setOnClickListener(v -> {
            toLogin(user.getText().toString(),pass.getText().toString());
        });
        signupBtn.setOnClickListener(v -> {
            toSignUp(user.getText().toString(),pass.getText().toString());
        });
    }

    //登录
    private void toLogin(String username, String password) {
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User bmobUser, BmobException e) {
                if(e==null){//登录成功
                    Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                    SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);//保存信息到缓存
                    user.edit().putString("userid",bmobUser.getObjectId()).commit();//保存登录的侠客
                    goMainActivity();
                }else {//密码输入错误
                    if(e.getErrorCode() == 101){
                        Toast.makeText(Login.this, "密码输入错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //注册
    private void toSignUp(String username, String password) {
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User bmobUser, BmobException e) {
                if(e==null){
                    System.out.println("注册成功"+bmobUser.getUsername());
                    initAttrs();//设置基础属性
                    goMainActivity();//跳转主页
                }else{
                    System.out.println("注册失败"+e);
                    if(e.getErrorCode() == 202) {
                        Toast.makeText(Login.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //跳转到主页
    private void goMainActivity() {
        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
    }

    //设置属性
    private void initAttrs() {
        User user = BmobUser.getCurrentUser(User.class);
        user.setLevel(1);
        user.setAttack(new Random().nextInt(10) + 1 + 5);
        user.setDefense(new Random().nextInt(10) + 1 + 5);
        user.setSpeed(new Random().nextInt(10) + 1 + 5);
        user.setBlood((new Random().nextInt(10) + 1 + 5) * 10);
        user.setAccExp(0);
        user.setReqExp(10);
        user.setType("普普通通");
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    System.out.println("更新用户信息成功：");
                } else {
                    System.out.println("error"+e.getMessage());
                }
            }
        });
    }
}
