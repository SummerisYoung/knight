package com.example.sql;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class RoleActivity extends AppCompatActivity {
    private ArrayList<String> mapstring;//接受页面传参

    ArrayList<String> roles;//每一人物数组
    ArrayList<ArrayList> roleLists = new ArrayList<>();//全部人物数组
    private FlexboxLayout flexboxLayout;

    RoleSkill monster = new RoleSkill();//点击的人物
    User mine = new User();//侠客

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        Intent intent = getIntent();
        mapstring = intent.getExtras().getStringArrayList("map");//通过这种方法接受地图活动传递过来的ArrayList<String> map
        Map map = new Map();//然后把mapstring转为Map实例
        map.setObjectId(mapstring.get(0));
        map.setName(mapstring.get(1));
        map.setDescription(mapstring.get(2));

        flexboxLayout = findViewById(R.id.flexbox_role);//获取页面flexbox布局
        TextView textView = findViewById(R.id.map_name);//获取文本组件
        textView.setText(map.getName());//展示地图名字

        returnData();

        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userid = user.getString("userid","");//从缓存获取侠客id

        BmobQuery<User> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("objectId",userid);
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if (e == null) {
                    mine = object.get(0);
                } else {
                    System.out.println("查询失败：" + e);
                }
            }
        });

        Button addbtn = findViewById(R.id.add_role);
        addbtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(RoleActivity.this,AddRole.class);
            intent1.putStringArrayListExtra("map",mapstring);//通过这种方法传递list，即ArrayList<String> map
            startActivityForResult(intent1,1);//用这个方法便于返回
        });
    }

    //获取人物数据
    private void returnData() {
        roleLists.clear();//清空之前的存储数组
        flexboxLayout.removeAllViews();//清空页面内组件

        BmobQuery<Role> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("map",mapstring.get(0));
        bmobQuery.findObjects(new FindListener<Role>() {
            @Override
            public void done(List<Role> list, BmobException e) {
                if (e == null) {
                    for (Role data:list) {
                        roles = new ArrayList<>();//每次创建新的地图数组引用
                        roles.add(data.getObjectId());//填入id
                        roles.add(data.getName());//填入名字
                        roles.add(data.getDescription());//填入描述

                        roleLists.add(roles);//填入地图数组
                    }

                    for(int i = 0;i < roleLists.size();i++) {
                        flexboxLayout.addView(getFlexboxLayoutItemView(roleLists.get(i)));
                    }

                } else {
                    //失败直接打印即可
                    System.out.println("查询失败:" + e.getMessage() + "," + e.getErrorCode());
                }
            }
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
        View view = LayoutInflater.from(RoleActivity.this).inflate(R.layout.role_menu, null);//获取布局view
        Dialog dialog = new Dialog(RoleActivity.this, R.style.bottom_dialog);//创建dialog
        TextView info_text = view.findViewById(R.id.role_info);//获取姓名等级
        TextView description_text = view.findViewById(R.id.role_description);//获取描述文本
        TextView attack_text = view.findViewById(R.id.attack);//获取攻击力文本
        TextView defense_text = view.findViewById(R.id.defense);//获取防御力文本
        TextView speed_text = view.findViewById(R.id.speed);//获取速度文本
        TextView blood_text = view.findViewById(R.id.blood);//获取血量文本
        description_text.setText(list.get(2).toString());


        BmobQuery<RoleSkill> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("role",list.get(0).toString());
        bmobQuery.findObjects(new FindListener<RoleSkill>() {
            @Override
            public void done(List<RoleSkill> skill, BmobException e) {
                if(e == null) {
                    monster = skill.get(0);
                    attack_text.setText("攻:"+monster.getAttack());
                    defense_text.setText("防:"+monster.getDefense());
                    speed_text.setText("速:"+monster.getSpeed());
                    blood_text.setText("血:"+monster.getBlood());
                    switch (monster.getType()){//设置等级颜色
                        case "普普通通":{
                            info_text.setTextColor(Color.BLACK);
                            break;
                        }
                        case "门派弟子":{
                            info_text.setTextColor(Color.GREEN);
                            break;
                        }
                        case "小有名气":{
                            info_text.setTextColor(Color.BLUE);
                            break;
                        }
                        case "一流侠客":{
                            info_text.setTextColor(Color.parseColor("#800080"));
                            break;
                        }
                        case "绝世高手":{
                            info_text.setTextColor(Color.RED);
                            break;
                        }
                        case "主角光环":{
                            info_text.setTextColor(Color.parseColor("#FFD700"));
                            break;
                        }
                    }
                    info_text.setText(list.get(1).toString() + "  " + monster.getLevel() + "级");
                }else{
                    System.out.println("查询失败:" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

        dialog.setContentView(view);//把dialog设置到布局上
        Window dialogWindow = dialog.getWindow();//获取dialog窗体
        dialogWindow.setGravity(Gravity.BOTTOM);//设置显示在底部
        dialogWindow.setWindowAnimations(R.style.dialog_style);//设置动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();//获取窗体属性

        lp.width = getResources().getDisplayMetrics().widthPixels;
        lp.height = (int) (getResources().getDisplayMetrics().heightPixels / 1.5);
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        Button fightBtn = view.findViewById(R.id.role_fight);//获取战斗按钮
        Button delBtn = view.findViewById(R.id.role_delete);//获取删除按钮
        fightBtn.setOnClickListener(v -> {
            fightDialog(view,list,dialog);
        });
        delBtn.setOnClickListener(v -> {
            delDialog(list.get(0).toString(),dialog);
        });
    }

    //战斗过程
    private void fightDialog(View view,ArrayList list, Dialog dialog) {
        LinearLayout role_linearlayout = view.findViewById(R.id.role_linearlayout);//获取角色dialog
        FlexboxLayout flexbox_attr = view.findViewById(R.id.flexbox_attr);//获取属性栏
        FlexboxLayout flexbox_btn = view.findViewById(R.id.flexbox_btn);//按钮layout
        Button fight = view.findViewById(R.id.role_fight);//获取战斗按钮
        Button del = view.findViewById(R.id.role_delete);//获取删除按钮

        role_linearlayout.removeView(flexbox_attr);//移除属性栏
        flexbox_btn.removeView(fight);//移除战斗按钮
        flexbox_btn.removeView(del);//移除删除按钮

        Button confirm = new Button(this);
        confirm.setText("确定");
        confirm.setWidth(120);
        flexbox_btn.addView(confirm);//新增确定按钮

        TextView description = view.findViewById(R.id.role_description);//获取描述文本
        int flag = fightProgress(description,list.get(1).toString());

        confirm.setOnClickListener(v -> {
            if(flag == 1){//你获胜
                delRole(list.get(0).toString(),dialog);//删除对手
                upgrade(monster.getLevel() * 10);
                mine.update(mine.getObjectId(), new UpdateListener() {//更新数据库数据
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            System.out.println("更新成功:"+mine.getUpdatedAt());
                        }else{
                            System.out.println("更新失败：" + e.getMessage());
                        }
                    }
                });
                returnData();//重新刷新界面
            }else{//否则啥也不干

            }
            dialog.dismiss();
        });
    }

    //递归获取经验升级
    private void upgrade(int exp) {
        if(exp + mine.getAccExp() < mine.getReqExp()){
            mine.setAccExp(exp + mine.getAccExp());
        }else{
            int beforeAccExp = mine.getAccExp();//之前积累的经验
            int beforeReqExp = mine.getReqExp();//之前升级需要的经验
            mine.setLevel(mine.getLevel() + 1);//升一级
            mine.setAccExp(0);//升级后积累的经验为0
            mine.setReqExp(mine.getReqExp() + 10);//升级所需的经验上升
            mine.setAttack(mine.getAttack() + new Random().nextInt(2) + 1);//攻击力加1到2
            mine.setDefense(mine.getDefense() + new Random().nextInt(2) + 1);//防御力加1到2
            mine.setSpeed(mine.getSpeed() + new Random().nextInt(2) + 1);//速度加1到2
            mine.setBlood(mine.getBlood() + 10);//血量加10
            switch (mine.getLevel() / 10){//判断是否更改级别
                case 1:{
                    mine.setType("门派弟子");
                    break;
                }
                case 2:{
                    mine.setType("小有名气");
                    break;
                }
                case 3:{
                    mine.setType("一流侠客");
                    break;
                }
                case 4:{
                    mine.setType("绝世高手");
                    break;
                }
                case 5:
                case 6:{
                    mine.setType("主角光环");
                    break;
                }
            }
            upgrade(exp + beforeAccExp - beforeReqExp);//递归升级
        }
    }

    //战斗过程
    private int fightProgress(TextView description,String monster_name) {
        //双方血量
        int monster_blood = monster.getBlood();
        int mine_blood = mine.getBlood();
        String situation = "";//战斗过程描述文本
        //比较双方速度快慢
        if(mine.getSpeed() > monster.getSpeed()){
            while(monster_blood > 0 && mine_blood > 0){
                monster_blood -= mineAttack();

                if(mineAttack() == 1)   situation += "你伤害太低，对" + monster_name + "造成了1点伤害......\n";
                else situation += "你赫然出手，打掉了" + monster_name + mineAttack() + "血量！\n";

                mine_blood -= monsterAttack();

                if(monsterAttack() == 1)   situation += monster_name + "太菜，只打掉了你一滴血......\n";
                else situation += monster_name + "闪电般出手，打掉了你" + monsterAttack() + "血量！\n";
            }
        }else{
            while(monster_blood > 0 && mine_blood > 0){
                mine_blood -= monsterAttack();

                if(monsterAttack() == 1)   situation += monster_name + "太菜，只打掉了你一滴血......\n";
                else situation += monster_name + "闪电般出手，打掉了你" + monsterAttack() + "血量！\n";

                monster_blood -= mineAttack();

                if(mineAttack() == 1)   situation += "你伤害太低，对" + monster_name + "造成了1点伤害......\n";
                else situation += "你赫然出手，打掉了" + monster_name + mineAttack() + "血量！\n";
            }
        }

        if(monster_blood <= 0) {
            situation += "恭喜！你击败了对手！";
            description.setText(situation);
            return 1;
        }else{
            situation += "你不敌对手，落败......";
            description.setText(situation);
            return 0;
        }
    }

    //你出手
    private int monsterAttack() {
        int mineBloodLoss;//你掉的血
        if(monster.getAttack() > mine.getDefense()) {//你的攻击力高于对方的防御力
            mineBloodLoss = monster.getAttack() - mine.getDefense();
        }else{//你的攻击力低于对方的防御力
            mineBloodLoss = 1;//最少减一滴血
        }
        return mineBloodLoss;
    }

    //对方出手
    private int mineAttack() {
        int monsterBloodLoss;//对方掉的血
        if(mine.getAttack() > monster.getDefense()) {//对方的攻击力高于你的防御力
            monsterBloodLoss = mine.getAttack() - monster.getDefense();
        }else{//对方攻击力低于你的防御力
            monsterBloodLoss = 1;//最少减一滴血
        }
        return monsterBloodLoss;
    }

    //删除人物
    private void delRole(String roleid, Dialog dialog) {
        //删除属性
        BmobQuery<RoleSkill> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("role",roleid);
        bmobQuery.findObjects(new FindListener<RoleSkill>() {
            @Override
            public void done(List<RoleSkill> skill, BmobException e) {
                for (RoleSkill data:skill){
                    RoleSkill roleSkill = new RoleSkill();
                    roleSkill.setObjectId(data.getObjectId());
                    roleSkill.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                System.out.println("属性删除成功:"+roleSkill.getUpdatedAt());

                                //删除人物
                                Role role = new Role();
                                role.setObjectId(roleid);
                                role.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            System.out.println("人物删除成功:"+role.getUpdatedAt());
                                            returnData();
                                        }else{
                                            System.out.println("人物删除失败：" + e.getMessage());
                                            returnData();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }else{
                                System.out.println("属性删除失败：" + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    //删除提示框
    private void delDialog(String roleid, Dialog ddialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(RoleActivity.this);
        builder.setMessage("确认删除该人物吗？");
        builder.setPositiveButton("确定", (dialog, which) -> {
            // TODO Auto-generated method stub
            dialog.dismiss();
            delRole(roleid,ddialog);
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            // TODO Auto-generated method stub
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            returnData();
        }
    }

}
