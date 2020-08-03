package com.jtangopro;

/**
 * 单词卡活动
 */

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class TangoCardActivity extends AppCompatActivity {

    private String audioName;
    private PassValues pv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tango_card);

        final Intent intent = getIntent();
        pv = (PassValues) intent.getSerializableExtra("passValues");
        String from_ = null==pv?"lib":"study";
        final Tango tango;
        String tangoCardType = null==pv?"":pv.getTangeCardType();

        if("lib"==from_)
            tango = (Tango) intent.getSerializableExtra("tango");
        else
            tango = pv.getCurTango();

        final TextView word_ = (TextView) findViewById(R.id.word_);
        TextView mark_ = (TextView) findViewById(R.id.mark_);
        TextView kana_ = (TextView) findViewById(R.id.kana_);
        TextView pronu_ = (TextView) findViewById(R.id.pronu_);
        TextView trans_ = (TextView) findViewById(R.id.trans_);
        TextView example_ = (TextView) findViewById(R.id.example_);

        trans_.setMovementMethod(ScrollingMovementMethod.getInstance());

        String w = tango.get("word");
        if(Character.isDigit(w.charAt(w.length()-1))){
            word_.setText(w.substring(0,w.length()-1));
            mark_.setText("  "+w.charAt(w.length()-1));
        }else
            word_.setText(w);
        kana_.setText(" "+tango.get("kana"));
        pronu_.setText(tango.get("pronu"));
        if(tango.get("trans").length()!=0) trans_.setText(tango.get("trans").substring(1,tango.get("trans").length()-1));
        if(tango.get("example").length()!=0) example_.setText(tango.get("example").substring(1,tango.get("example").length()-1));

        //更具文本长度动态修改字号
        word_.setTextSize(MyUtil.getSize((String)word_.getText()));
        example_.setTextSize(MyUtil.getSize((String)example_.getText()));

        //音频播放小喇叭
        final String wa = tango.get("waudio");
        final String ea = tango.get("eaudio");
        audioName = wa;
        Button bt_waudio = (Button) findViewById(R.id.bt_waudio);
        Button bt_eaudio = (Button) findViewById(R.id.bt_eaudio);
        if(ea.length()==0) bt_eaudio.setVisibility(View.INVISIBLE);
        bt_waudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playSound(TangoCardActivity.this, wa);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bt_eaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playSound(TangoCardActivity.this, ea);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //显示熟练度，测试用
        TextView prof_ = (TextView) findViewById(R.id.prof_);
        prof_.setText("熟练度: "+tango.get("prof"));

        final Button bt_1 = (Button) findViewById(R.id.bt_1);
        final Button bt_2 = (Button) findViewById(R.id.bt_2);
        final Button bt_3 = (Button) findViewById(R.id.bt_3);

        //记错按钮
        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_t;
                pv.setCurProf(pv.getCurProf()-1);
                if(!pv.continueStudy(false)) intent_t = new Intent(TangoCardActivity.this,FinishActivity.class);
                else intent_t = new Intent(TangoCardActivity.this,StudyCardActivity.class);
                intent_t.putExtra("passValues",(Serializable) pv);
                startActivity(intent_t);
                finish();
            }
        });

        //转往下一流程
        bt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_t;
                if(!pv.continueStudy(false)) intent_t = new Intent(TangoCardActivity.this,FinishActivity.class);
                else intent_t = new Intent(TangoCardActivity.this,StudyCardActivity.class);
                intent_t.putExtra("passValues",(Serializable) pv);
                startActivity(intent_t);
                finish();
            }
        });

        //已掌握，移除
        bt_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = DBManager.getDB(TangoCardActivity.this, "JTango.db", null, 1);
                ContentValues values = new ContentValues();
                values.put("known", "1");
                db.update("Total_Library", values, "word = ?", new String[]{tango.get("word")});
                if(null!=pv) {
                    Intent intent_t;
                    if(!pv.continueStudy(true)) intent_t = new Intent(TangoCardActivity.this,FinishActivity.class);
                    else intent_t = new Intent(TangoCardActivity.this,StudyCardActivity.class);
                    intent_t.putExtra("passValues",(Serializable) pv);
                    startActivity(intent_t);
                    finish();
                }
                finish();
            }
        });
        if(from_.equals("lib")) {
            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            if (tango.get("known").equals("1")) {
                bt_3.setText("已移出学习词库");
                bt_3.setTextColor(Color.BLACK);
                bt_3.setBackgroundColor(Color.GRAY);
                bt_3.setOnClickListener(null);
            }
        }else if(tangoCardType.equals("tangocard1")){
            //不做处理
        }
        else if(tangoCardType.equals("tangocard2")){
            bt_1.setText("记错了");
            bt_3.setVisibility(View.INVISIBLE);
        }else if(tangoCardType.equals("tangocard3")){
            bt_1.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            playSound(TangoCardActivity.this, audioName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(pv!=null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示：");
            builder.setMessage("您确定退出？未完成进度不会被保存");
            //设置确定按钮
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent_t = new Intent(TangoCardActivity.this,FinishActivity.class);
                    intent_t.putExtra("passValues",(Serializable) pv);
                    startActivity(intent_t);
                    finish();
                }
            });
            //设置取消按钮
            builder.setPositiveButton("取消", null);
            //显示提示框
            builder.show();
        }else{
            finish();
        }
    }

    private void playSound(Context context, String audioName) throws IOException {
        String[] name = audioName.split("\\.");
        String filename = "a_" + name[0] + "_" + name[1] + ".mp3";
        try {
            AssetFileDescriptor fd = getAssets().openFd(filename);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
