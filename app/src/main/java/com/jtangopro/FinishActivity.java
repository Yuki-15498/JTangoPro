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

        Intent intent = getIntent();
        PassValues pv = (PassValues) intent.getSerializableExtra("passValues");

        TextView congrat = (TextView) findViewById(R.id.congrat);
        TextView cnt = (TextView) findViewById(R.id.cnt);
        TextView date = (TextView) findViewById(R.id.date);

        if(pv.continueStudy(false))
            congrat.setText("\n今日进度还差一点哦\n明日继续加油！");
        else
            pv.increaseAllProf();

        Button bt_finish = (Button) findViewById(R.id.bt_finish);
        bt_finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        cnt.setText(""+pv.getCntKnown());

        String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        date.setText(dateStr+"   打卡\n");

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
