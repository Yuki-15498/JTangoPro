package com.jtangopro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * 保存每日打卡信息
 * 对外提供方法：
 * 1、checkToday(): 查看今日是否已经完成任务(用于控制开始按钮文本（开始学习或继续学习），以及结束活动显示)
 * 2、setToday(boolean done):设置今日的任务完成情况
 * 3、checkMonth(int year, int month):获取某年某月的当月所有信息
 * 4、refreshDC(): 初始化DC，在点击开始学习按钮后运行
 * 5、getDaysFinished(): 获取已完成任务的所有天数
 * 6、getStartDate():获取首次日期
 */

public class DateCheck {

    private static DateCheck dc = null;

    private Context mContext;

    //首次日期
    private String startDate;
    //累计月数，方便每日信息的读取
    private int numMonth;
    //每日信息,其中每个32位数代表32天的信息，1表示当天已完成进度,即一个数表示一个月的信息
    private List<Integer> dailyInfo = new ArrayList<>();
    //实际月数，根据今日日期和首次日期推算出来

    private DateCheck(Context context){
        mContext = context;
        refreshDC();
    }

    private int getNumMonthTill(String date){
        //获取从开始日期到某一日期间的月数
        int startY = Integer.parseInt(startDate.substring(0,4));
        int startM = Integer.parseInt(startDate.substring(5,7));
        int dateY = Integer.parseInt(date.substring(0,4));
        int dateM = Integer.parseInt(date.substring(5,7));
        if(startY>dateY||(startY==dateY&&startM>dateM)){
            //输入日期小于起始日期
            return 0;
        }
        if(startY==dateY){
            return dateM - startM + 1;
        }else{
            return 13 - startM + 12*(dateY-startY-1) + dateM;
        }
    }

    private int getTrueNumMonth() {
        //获取开始到今天的真实月数
        String today = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        return getNumMonthTill(today);
    }

    public void refreshDC(){
        //刷新dc状态，加载信息，每次点击开始学习或需要日期信息的活动后运行
        SharedPreferences dt = mContext.getSharedPreferences("dateInfo",MODE_PRIVATE);
        startDate = dt.getString("startDate","");
        if("".equals(startDate)){
            //首次处理
            startDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            SharedPreferences.Editor editor = dt.edit();
            editor.putString("startDate",startDate);
            editor.apply();
        }
        numMonth = dt.getInt("numMonth",1);
        for(int i=0;i<numMonth;i++){
            //装载所有月份int
            dailyInfo.add(dt.getInt("month"+i,0));
        }
        int trueNumMonth = getTrueNumMonth();
        SharedPreferences.Editor editor = dt.edit();
        editor.putInt("numMonth",trueNumMonth);
        editor.apply();
        //填0
        for(int i=0,len=trueNumMonth-numMonth;i<len;i++){
            dailyInfo.add(0);
        }
    }

    public static DateCheck getDateCheck(Context context){
        if(null==dc){
            dc = new DateCheck(context);
        }
        return dc;
    }

    private boolean isDone(int m, int day){
        //返回某月某天是否完成任务
        int dayMask = 0b1000_0000_0000_0000_0000_0000_0000_0000;
        for(int i=1;i<day;i++){
            dayMask = dayMask>>>1;
        }
        return !(0==(dayMask & m));
    }

    public void setToday(){
        //设置今日信息，并保存
        int toMonth = dailyInfo.get(dailyInfo.size()-1);
        int dayMask = 0b1000_0000_0000_0000_0000_0000_0000_0000;
        for(int i=1,day=getTodayNo();i<day;i++){
            dayMask = dayMask>>>1;
        }
        dailyInfo.set(dailyInfo.size()-1,dayMask|toMonth);
        SharedPreferences dt = mContext.getSharedPreferences("dateInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = dt.edit();
        for(int i=0,len=dailyInfo.size();i<len;i++){
            editor.putInt("month"+i,dailyInfo.get(i));
        }
        editor.apply();
    }

    private int getTodayNo(){
        //获取今天是当月的第几号
        String today = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        return Integer.parseInt(today.substring(8));
    }

    public boolean checkToday(){
        //判断今天是否完成任务
        //今日为几号，就取第几位
        return isDone(dailyInfo.get(dailyInfo.size()-1),getTodayNo());
    }

    public List<Boolean> checkMonth(String date){
        int monthInfo = dailyInfo.get(getNumMonthTill(date)-1);
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(5,7));
        //获取当月天数
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        //添加
        List<Boolean> rst = new ArrayList<>(dayOfMonth);
        for(int i=1;i<dayOfMonth;i++){
            rst.add(isDone(monthInfo,i));
        }
        return rst;
    }

    public int getDaysFinished(){
        //获取一共完成了任务的天数
        int rst = 0;
        for(int month:dailyInfo){
            for(int i=1;i<32;i++){
                if(isDone(month,i)){
                    rst++;
                }
            }
        }
        return rst;
    }

    public String getStartDate(){
        return startDate;
    }

    public int[] getStartDateInt(){
        //获取起始日期，返回一个数组，包含年、月、日
        int[] date = new int[3];
        date[0] = Integer.parseInt(startDate.substring(0,4));
        date[1] = Integer.parseInt(startDate.substring(5,7));
        date[2] = Integer.parseInt(startDate.substring(8));
        return date;
    }

}
