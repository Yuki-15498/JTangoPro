package com.jtangopro;

/**
 * 启动学习活动
 * 加载学习数据
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StudyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        //-----------先从数据库中读取一定数量的未学习生词和复习词汇--------------------
        SharedPreferences ed = getSharedPreferences("setting",MODE_PRIVATE);
        int numOfNew = ed.getInt("number_of_new",0);
        int numOfRev = ed.getInt("number_of_review",0);
        SQLiteDatabase db = DBManager.getDB(this, "JTango.db", null, 1);
        Cursor cursor = db.query("Total_Library",null,"known=?",new String[]{"0"},"word","SUM(prof)<=0",null);
        //Log.d("StudyActivity",""+cursor.getCount());
        Set<Integer> tangoIndex;
        ArrayList<Tango> tangos = new ArrayList<>();
        if(cursor.getCount()>=numOfNew)
            tangoIndex = MyUtil.myRand(cursor.getCount(),numOfNew);
        else
            tangoIndex = MyUtil.myRand(cursor.getCount(),cursor.getCount());
        for(int index:tangoIndex){
            //装载生词
            cursor.moveToPosition(index);
            String[] head = DBManager.head;
            Map<String, String> m = new HashMap<>();
            for(String attr:head){
                String val = cursor.getString(cursor.getColumnIndex(attr));
                m.put(attr,val);
            }
            Tango tmp = new Tango(m);
            tangos.add(tmp);
        }
        cursor = db.query("Total_Library",null,"known=?",new String[]{"0"},"word","SUM(prof)>0","prof");
        if(cursor.getCount()==0) {
            //恭喜！所有单词都已掌握！
            //此处处理暂时先不定
        }
        numOfRev = cursor.getCount()>numOfRev?numOfRev:cursor.getCount();
        cursor.moveToFirst();
        for(int ii=0;ii<numOfRev;ii++){
            //装载复习单词
            String[] head = DBManager.head;
            Map<String, String> m = new HashMap<>();
            for(String attr:head){
                String val = cursor.getString(cursor.getColumnIndex(attr));
                m.put(attr,val);
            }
            Tango tmp = new Tango(m);
            tangos.add(tmp);
            cursor.moveToNext();
        }
        cursor.close();
        Collections.shuffle(tangos);
        //--------------------------数据装载完毕--------------------------------------------
        //---------------------------启动学习卡---------------------------------------------
        //创建并发送PassValues对象
        Intent intent = new Intent(StudyActivity.this, StudyCardActivity.class);
        PassValues pv = new PassValues(tangos,0);
        intent.putExtra("passValues",(Serializable) pv);
        startActivity(intent);
        finish();
    }
}
