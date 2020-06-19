package com.jtangopro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //显示已设置的值
        SharedPreferences ed = getSharedPreferences("setting",MODE_PRIVATE);
        int num = ed.getInt("number_of_new",0);
        int num2 = ed.getInt("number_of_review",0);
        if(num!=0){
            EditText et = (EditText) findViewById(R.id.et_setting);
            et.setText(""+num);
        }
        if(num2!=0){
            EditText et = (EditText) findViewById(R.id.et_setting_2);
            et.setText(""+num2);
        }

        Button bt_save = (Button) findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText et = (EditText) findViewById(R.id.et_setting);
                EditText et2 = (EditText) findViewById(R.id.et_setting_2);
                String s = et.getText().toString();
                String s2 = et2.getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences("setting",MODE_PRIVATE).edit();
                if(!MyUtil.judgeInput(s)) {
                    Toast.makeText(SettingActivity.this, "请输入2到100之间的整数！", Toast.LENGTH_SHORT).show();
                    et.setText("");
                }
                if(!MyUtil.judgeInput(s2)){
                    Toast.makeText(SettingActivity.this, "请输入2到100之间的整数！", Toast.LENGTH_SHORT).show();
                    et2.setText("");
                }
                if(MyUtil.judgeInput(s)&&MyUtil.judgeInput(s2)){
                    editor.putInt("number_of_new",Integer.parseInt(s));
                    editor.putInt("number_of_review",Integer.parseInt(s2));
                    editor.apply();
                    Toast.makeText(SettingActivity.this, "设置已保存！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

}
