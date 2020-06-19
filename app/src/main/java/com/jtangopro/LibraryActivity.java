package com.jtangopro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibraryActivity extends AppCompatActivity {

    private String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Intent intent = getIntent();
        String from_ = intent.getStringExtra("from");
        if(from_.equals("bt_learning")) {
            setTitle("待学习单词");
            flag = "0";
        }
        else {
            setTitle("已掌握单词");
            flag = "1";
        }

        printScreen(flag);
    }

    private void printScreen(String flag){
        final ArrayList<String> data = new ArrayList();
        SQLiteDatabase db = DBManager.getDB(this, "JTango.db", null, 1);
        final Cursor cursor = db.query("Total_Library",null,"known=?",new String[]{flag},null,null,"prof desc");
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
        printScreen(flag);
    }
}
