package com.jtangopro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //显示掌握词数和总词数
        int[] nums = initRun(this);
        String showNum = nums[0] + "/" + nums[1];
        TextView textNum = (TextView) findViewById(R.id.num);
        textNum.setText(showNum);

        Button bt_learning = (Button) findViewById(R.id.bt_learning);
        bt_learning.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra("from","bt_learning");
                startActivity(intent);
            }
        });

        Button bt_known = (Button) findViewById(R.id.bt_known);
        bt_known.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra("from","bt_known");
                startActivity(intent);
            }
        });

        Button bt_start = (Button) findViewById(R.id.bt_start);
        bt_start.setTextColor(Color.WHITE);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences ed = getSharedPreferences("setting",MODE_PRIVATE);
                int num = ed.getInt("number_of_new",0);
                if(num==0){
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "请先设置！", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(MainActivity.this, StudyActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        int[] nums = initRun(this);
        String showNum = nums[0] + "/" + nums[1];
        TextView textNum = (TextView) findViewById(R.id.num);
        textNum.setText(showNum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static int getKnownNum(Context context){
        //计算已掌握的单词数
        SQLiteDatabase db = DBManager.getDB(context, "JTango.db", null, 1);
        Cursor cursor = db.query("Total_Library",null,null,null,null,null,null);
        int cnt = 0;
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString((cursor.getColumnIndex("known"))).equals("1")){
                    cnt++;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return cnt;
    }

    private int[] initRun(Context context){
        DBManager.initDB(context);
        int numOfWords = DBManager.allCaseNum(context);
        int[] rst = new int[2];
        rst[0] = getKnownNum(context);
        rst[1] = numOfWords;
        return rst;
    }
}
