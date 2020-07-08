package com.jtangopro;

/**
 * 结束活动
 * 显示结束界面
 * 将学习数据(prof)写回数据库
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FinishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        DateCheck dc = DateCheck.getDateCheck(FinishActivity.this);

        Intent intent = getIntent();
        PassValues pv = (PassValues) intent.getSerializableExtra("passValues");

        TextView congrat = (TextView) findViewById(R.id.congrat);
        TextView cnt = (TextView) findViewById(R.id.cnt);
        TextView cnt_ = (TextView) findViewById(R.id.cnt_);
        Button bt_finish = (Button) findViewById(R.id.bt_finish);

        String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

        //基于日期和是否完成任务的逻辑修改显示内容
        if(pv.continueStudy(false)) {
            if(!dc.checkToday()) {
                congrat.setText("\n今日进度还差一点哦\n明日继续加油！");
            }else{
                congrat.setText("\n追加进度没完成哦\n下次请量力而行！");
                cnt_.setText("本轮新增       已掌握单词");
            }
        }else {
            pv.increaseAllProf();
            if(!dc.checkToday()) {
                dc.setToday();
                bt_finish.setText(dateStr+"   打卡");
            }else{
                congrat.setText("\n恭喜\n追加进度已完成！");
                cnt_.setText("本轮新增       已掌握单词");
            }
        }

        bt_finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        cnt.setText(""+pv.getCntKnown());

        //熟练度写回数据库
        ArrayList<Tango> tgs = pv.getTangos();
        SQLiteDatabase db = DBManager.getDB(FinishActivity.this, "JTango.db", null, 1);
        for(Tango t:tgs){
            ContentValues values = new ContentValues();
            values.put("prof", t.get("prof"));
            db.update("Total_Library", values, "word = ?", new String[]{t.get("word")});
        }

        //pv.printProfInfo();
    }
}
