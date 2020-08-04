package com.jtangopro;
/**
 *
 * 学习卡活动，分n种学习模式
 *
 */

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StudyCardActivity extends AppCompatActivity {

    private PassValues pv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_card);

        final TextView word_ = (TextView) findViewById(R.id.word_);
        TextView mark_ = (TextView) findViewById(R.id.mark_);
        final Button bt_1 = (Button) findViewById(R.id.bt_1);
        final Button bt_2 = (Button) findViewById(R.id.bt_2);
        final Button bt_3 = (Button) findViewById(R.id.bt_3);
        final Button bt_4 = (Button) findViewById(R.id.bt_4);
        final Button bt_5 = (Button) findViewById(R.id.bt_5);
        //自适应宽度
        Display display = this.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int btWidth = (int) (point.x*0.92);
        bt_1.getLayoutParams().width = btWidth;
        bt_2.getLayoutParams().width = btWidth;
        bt_3.getLayoutParams().width = btWidth;
        bt_4.getLayoutParams().width = btWidth;
        bt_5.getLayoutParams().width = btWidth;

        word_.setMovementMethod(ScrollingMovementMethod.getInstance());

        final Intent intent = getIntent();
        pv = (PassValues) intent.getSerializableExtra("passValues");

        Tango t = pv.getCurTango();
        String studyType = pv.getCurStudyType();
        String w = t.get("word");
        if(Character.isDigit(w.charAt(w.length()-1))){
            mark_.setText(""+w.charAt(w.length()-1));
            w = w.substring(0,w.length()-1);
            word_.setText(w);
        }else
            word_.setText(w);

        //选择题
        Set<String> words = pv.getWords();
        String trueKana = t.get("kana");
        int numOfBts = words.size()>=4?4:words.size();
        words.remove(trueKana);
        List<String> falseWords = new LinkedList<>(words);
        Collections.shuffle(falseWords);
        Button[] bts = new Button[4];
        bts[0] = bt_1;
        bts[1] = bt_2;
        bts[2] = bt_3;
        bts[3] = bt_4;
        int truePos = new Random().nextInt(numOfBts);

        //取代表意思
        String mean = t.get("trans").split("\n")[1];
        String meanTrim = MyUtil.myTrim2(mean);

        final Intent intent_ = new Intent(StudyCardActivity.this,TangoCardActivity.class);
        if(studyType.equals("type1")){
            //学习类型1----------------------------------------------------------------------------------
            bt_1.setText("会读，意思也知道");
            setButtonKnown(bt_1,intent_);
            bt_2.setText("只会意思");
            setButtonHalf(bt_2,intent_);
            bt_3.setText("只会读法");
            setButtonHalf(bt_3,intent_);
            bt_4.setText("都不会");
            setButtonUnknown(bt_4,intent_);
            bt_5.setVisibility(View.INVISIBLE);
            //------------------------------------------------------------------------------------------
        }else if(studyType.equals("type2")){
            //学习类型2----------------------------------------------------------------------------------
            mark_.setVisibility(View.INVISIBLE);
            word_.setText(meanTrim);
            word_.setTextSize(MyUtil.getSize2(w));
            int i=0;
            for(;i<numOfBts;i++){
                if(truePos==i) {
                    setButtonTrue(bts[i], intent_);
                    bts[i].setText(MyUtil.myTrim(trueKana));
                }
                else{
                    setButtonFalse(bts[i], intent_);
                    bts[i].setText(MyUtil.myTrim(falseWords.remove(0)));
                }
            }
            for(;i<4;i++)
                bts[i].setVisibility(View.INVISIBLE);
            bt_5.setText("不会");
            setButtonUnknown(bt_5,intent_);
            //------------------------------------------------------------------------------------------
        }else if(studyType.equals(("type3"))){
            //学习类型3----------------------------------------------------------------------------------
            mark_.setVisibility(View.INVISIBLE);
            StringBuilder sb = new StringBuilder();
            for(int i=0,len=w.length();i<len;i++){
                if(MyUtil.isKana(w.charAt(i)))
                    sb.append(w.charAt(i));
                else
                    sb.append('□');
            }
            word_.setText(sb.toString()+"\n"+MyUtil.myTrim(t.get("kana")));
            bt_1.setText("知道是什么汉字");
            setButtonKnown(bt_1,intent_);
            bt_2.setText("不会");
            setButtonUnknown(bt_2,intent_);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            //------------------------------------------------------------------------------------------
        }

    }

    private void setButtonUnknown(Button bt, final Intent intent){
        //将当前按钮设置为“不会”状态
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pv.setTangeCardType("tangocard3");
                intent.putExtra("passValues",(Serializable) pv);
                startActivity(intent);
                finish();
            }
        });
        bt.setBackground(getResources().getDrawable(R.drawable.myrec3));
    }

    private void setButtonKnown(Button bt, final Intent intent){
        //将当前按钮设置为“知道”状态
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(pv.isInit()) {
                    pv.setTangeCardType("tangocard1");
                    pv.setCurProf(pv.getCurProf()+3);
                } else {
                    pv.setTangeCardType("tangocard2");
                    pv.setCurProf(pv.getCurProf()+2);
                }
                intent.putExtra("passValues",(Serializable) pv);
                startActivity(intent);
                finish();
            }
        });
        bt.setBackground(getResources().getDrawable(R.drawable.myrec2));
    }

    private void setButtonHalf(Button bt,final Intent intent){
        //将当前按钮设置为“半会”状态
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pv.setTangeCardType("tangocard2");
                pv.setCurProf(pv.getCurProf()+1);
                intent.putExtra("passValues",(Serializable) pv);
                startActivity(intent);
                finish();
            }
        });
        bt.setBackground(getResources().getDrawable(R.drawable.myrec4));
    }

    private void setButtonTrue(final Button bt, final Intent intent){
        //将当前按钮设置为“正确”状态
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bt.setBackground(getResources().getDrawable(R.drawable.myrec5));
                pv.setTangeCardType("tangocard3");
                pv.setCurProf(pv.getCurProf()+1);
                intent.putExtra("passValues",(Serializable) pv);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setButtonFalse(final Button bt, final Intent intent){
        //将当前按钮设置为“错误”状态
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                bt.setBackground(getResources().getDrawable(R.drawable.myrec3));
                pv.setTangeCardType("tangocard3");
                intent.putExtra("passValues",(Serializable) pv);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("您确定退出？未完成进度不会被保存");
        //设置确定按钮
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent_t = new Intent(StudyCardActivity.this,FinishActivity.class);
                intent_t.putExtra("passValues",(Serializable) pv);
                startActivity(intent_t);
                finish();
            }
        });
        //设置取消按钮
        builder.setPositiveButton("取消", null);
        //显示提示框
        builder.show();
    }
}

