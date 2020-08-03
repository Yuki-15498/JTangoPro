package com.jtangopro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibraryActivity extends AppCompatActivity {

    private RadioGroup rg;
    private RadioButton[] rbs = new RadioButton[3];
    private enum Lib {LIB1, LIB2, LIB3}
    //顶栏三控件
    private Button bt_num;
    private Spinner sp;
    private Button bt_sort;
    //查询用信息
    private String[] qry = {"kana","asc"};
    //默认是lib1
    private Lib lib = Lib.LIB1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        bt_num = findViewById(R.id.bt_num);
        sp = findViewById(R.id.sp1);
        bt_sort = findViewById(R.id.bt_sort);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    qry[0] = "kana";
                }else if(position==1){
                    qry[0] = "prof";
                }
                printScreen();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        bt_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt_sort.getText().equals("升序")){
                    bt_sort.setText("降序");
                    qry[1] = "desc";
                }else{
                    bt_sort.setText("升序");
                    qry[1] = "asc";
                }
                printScreen();
            }
        });

        setTitle("待学习单词");
        printScreen();
        //底部栏,根据不同的选择打印不同的内容
        rg = findViewById(R.id.rg);
        rbs[0] = findViewById(R.id.lib1);
        rbs[1] = findViewById(R.id.lib2);
        rbs[2] = findViewById(R.id.lib3);
        monitoringRadioGrop();
    }

    private void printScreen(){
        final ArrayList<String> data = new ArrayList();
        SQLiteDatabase db = DBManager.getDB(this, "JTango.db", null, 1);
        final Cursor cursor;
        if(lib == Lib.LIB1){
            cursor = db.query("Total_Library",null,"prof=? AND known!=?",new String[]{"0","1"},null,null,qry[0]+" "+qry[1]);
        }else if(lib == Lib.LIB2){
            cursor = db.query("Total_Library",null,"prof!=? AND known=?",new String[]{"0","0"},null,null,qry[0]+" "+qry[1]);
        }else{
            cursor = db.query("Total_Library",null,"known=?",new String[]{"1"},null,null,qry[0]+" "+qry[1]);
        }
        bt_num.setText("总数："+cursor.getCount());
        int i=0;
        if(cursor.moveToFirst()){
            do{
                data.add(cursor.getString(cursor.getColumnIndex("word")));
            }while (cursor.moveToNext());
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(LibraryActivity.this, android.R.layout.simple_list_item_1, data);
        final ListView listView = (ListView)findViewById(R.id.list_view);
        //将构建好的适配器对象传进去
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                if(cursor.moveToFirst()) {
                    do {
                        if (cursor.getString((cursor.getColumnIndex("word"))).equals(data.get(position))) {
                            break;
                        }
                    } while (cursor.moveToNext());
                }
                String[] head = DBManager.head;
                Map<String, String> m = new HashMap<>();
                for(String attr:head){
                    String val = cursor.getString(cursor.getColumnIndex(attr));
                    m.put(attr,val);
                }
                cursor.close();
                Intent intent = new Intent(LibraryActivity.this, TangoCardActivity.class);
                intent.putExtra("tango", (Serializable) new Tango(m));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        printScreen();
    }

    private void monitoringRadioGrop(){
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.lib1:
                        rbs[0].setBackgroundColor(Color.parseColor("#D1D1D1"));
                        rbs[1].setBackgroundResource(R.drawable.border1);
                        rbs[2].setBackgroundResource(R.drawable.border1);
                        lib = Lib.LIB1;
                        setTitle("待学习单词");
                        printScreen();
                        break;
                    case R.id.lib2:
                        rbs[1].setBackgroundColor(Color.parseColor("#D1D1D1"));
                        rbs[0].setBackgroundResource(R.drawable.border1);
                        rbs[2].setBackgroundResource(R.drawable.border1);
                        lib = Lib.LIB2;
                        setTitle("在学习单词");
                        printScreen();
                        break;
                    case R.id.lib3:
                        rbs[2].setBackgroundColor(Color.parseColor("#D1D1D1"));
                        rbs[0].setBackgroundResource(R.drawable.border1);
                        rbs[1].setBackgroundResource(R.drawable.border1);
                        lib = Lib.LIB3;
                        setTitle("已掌握单词");
                        printScreen();
                        break;
                }
            }
        });
    }
}
