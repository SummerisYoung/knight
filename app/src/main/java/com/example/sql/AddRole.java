package com.example.sql;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Random;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddRole extends AppCompatActivity {
    private ArrayList<String> mapstring;//接受页面传参

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_role);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        mapstring = intent.getExtras().getStringArrayList("map");//通过这种方法接受地图活动传递过来的ArrayList<String> map
        System.out.println(mapstring);

        Map map = new Map();//把mapstring转为Map实例
        map.setObjectId(mapstring.get(0));//保留mapid

        EditText name = findViewById(R.id.input_name);
        EditText description = findViewById(R.id.input_description);
        Button submit = findViewById(R.id.submit);
        Spinner spinner = findViewById(R.id.spinner);

        Role newRole = new Role();
        newRole.setMap(map);

        submit.setOnClickListener(v -> {
                newRole.setName(name.getText().toString());
                newRole.setDescription(description.getText().toString());
                newRole.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if(e==null){
                            RoleSkill newRoleSkill1 = new RoleSkill();
                            setRoleSkill(spinner.getSelectedItem().toString(), newRoleSkill1,objectId);
                            //增加属性表
                            newRoleSkill1.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if(e == null) {
                                        dialog("增加成功!",intent);
                                    }else{
                                        dialog("增加失败!",intent);
                                    }
                                }
                            });
                        }else{
                            System.out.println("创建数据失败：" + e.getMessage());
                        }
                    }
                });
        });
    }

    private void setRoleSkill(String type,RoleSkill newRoleSkill,String objectId) {
        //设置属性
        switch (type){
            case "普普通通":{
                setAttrs(newRoleSkill, "普普通通", 0);
                break;
            }
            case "门派弟子":{
                setAttrs(newRoleSkill, "门派弟子", 10);
                break;
            }
            case "小有名气":{
                setAttrs(newRoleSkill, "小有名气", 20);
                break;
            }
            case "一流侠客":{
                setAttrs(newRoleSkill, "一流侠客", 30);
                break;
            }
            case "绝世高手":{
                setAttrs(newRoleSkill, "绝世高手", 40);
                break;
            }
            case "主角光环":{
                setAttrs(newRoleSkill, "主角光环", 50);
                break;
            }
        }
        Role newRole = new Role();
        newRole.setObjectId(objectId);
        newRoleSkill.setRole(newRole);
    }

    private void setAttrs(RoleSkill roleSkill, String type, int num) {
        roleSkill.setLevel(new Random().nextInt(10) + 1 + num);
        roleSkill.setAttack(new Random().nextInt(10) + 1 + num);
        roleSkill.setDefense(new Random().nextInt(10) + 1 + num);
        roleSkill.setSpeed(new Random().nextInt(10) + 1 + num);
        roleSkill.setBlood((new Random().nextInt(10) + 1 + num) * 10);
        roleSkill.setType(type);
    }

    private void dialog(String msg,Intent intent){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddRole.this);
        builder.setMessage(msg);
        builder.setCancelable(false);//去掉取消按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            // TODO Auto-generated method stub
            dialog.dismiss();
            setResult(1,intent);
            finish();
        });
        builder.show();
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
