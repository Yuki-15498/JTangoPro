package com.jtangopro;
/**
 * 打卡日历活动
 * 显示范围限制在min和max之间
 * min表示起始日期对应的年月
 * max代表当月
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    //日历的所有项
    private List<String> items = new ArrayList<>();
    //每项状态
    private List<State> item_state = new ArrayList<>();
    enum State{
        HEAD, EMPTY, FINISH, UNFINISH, TOFINISHED, NEVER
    }
    //每月的完成情况表
    private List<Boolean> month_state;
    //传入适配器的数据
    private List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
    //date代表选中的日期，当月默认今天，非当月（往月）默认1号
    private String date, today;
    private int[] dateInts, todayInts, startDateInts;

    DateCheck dc;
    private GridView gv;
    SimpleAdapter adapter;
    TextView state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dc = DateCheck.getDateCheck(CalendarActivity.this);
        state = (TextView) findViewById(R.id.state);

        //获取起始日期和今日日期
        date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        today = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        startDateInts = dc.getStartDateInt();
        dateInts = MyUtil.dateToInts(date);
        todayInts = MyUtil.dateToInts(today);

        //初始化
        setMonthDisplay();

        gv = (GridView) findViewById(R.id.calendar);
        adapter = new SimpleAdapter(CalendarActivity.this, data_list, R.layout.calendar_item, new String[]{"item"},new int[]{R.id.item});
        gv.setAdapter(adapter);

        updateStateDisplay();
        //点击某一日期后显示具体状态
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int tmp = getDay(i);
                if(tmp>0) {
                    dateInts[2] = tmp;
                    date = MyUtil.intsToString(dateInts);
                    onRestart();
                }
            }
        });

        findViewById(R.id.bt_left).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                monthBack();
                onRestart();
            }
        });

        findViewById(R.id.bt_right).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                monthForward();
                onRestart();
            }
        });

        findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setMonthDisplay();
        updateStateDisplay();
        adapter = new SimpleAdapter(CalendarActivity.this, data_list, R.layout.calendar_item, new String[]{"item"},new int[]{R.id.item});
        gv.setAdapter(adapter);
    }

    private void setMonthDisplay(){
        //设置当月显示格式
        //初始化内容
        if(MyUtil.monthCmp(dateInts,startDateInts)>=0&&MyUtil.monthCmp(dateInts,todayInts)<=0) {
            month_state = dc.checkMonth(date);
        } else {
            month_state = new ArrayList<>();
            for(int i=0;i<31;i++)
                month_state.add(false);
        }
        items.clear();
        item_state.clear();
        String[] base = {"日","一","二","三","四","五","六"};
        for(String s:base){
            items.add(s);
            item_state.add(State.HEAD);
        }
        //获取年月
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(5,7));
        int dayOfMonth = MyUtil.getDayOfMonth(year,month);
        //获取1号是星期几
        Calendar cld = Calendar.getInstance();
        cld.set(year,month-1,1);//month是0开始
        int firstDay = cld.get(Calendar.DAY_OF_WEEK);
        for(int i=1;i<firstDay;i++){
            //填入空
            items.add("");
            item_state.add(State.EMPTY);
        }
        for(int i=1;i<=dayOfMonth;i++){
            //填入日期
            int[] datei = new int[3];
            datei[0] = dateInts[0];
            datei[1] = dateInts[1];
            datei[2] = i;
            if(MyUtil.dateCmp(datei,todayInts)==0){
                if(month_state.get(i-1)){
                    items.add(" " + i + "✓");
                    item_state.add(State.FINISH);
                }else{
                    items.add("-" + i + "-");
                    item_state.add(State.TOFINISHED);
                }
            }else if(MyUtil.dateCmp(datei,todayInts)==1){
                items.add("-" + i + "-");
                item_state.add(State.TOFINISHED);
            }else{
                if(MyUtil.dateCmp(datei,startDateInts)>=0){
                    if(month_state.get(i-1)){
                        items.add(" " + i + "✓");
                        item_state.add(State.FINISH);
                    }else{
                        items.add(" " + i + "x");
                        item_state.add(State.UNFINISH);
                    }
                }else{
                    items.add("." + i + ".");
                    item_state.add(State.NEVER);
                }
            }
        }
        //选中日期显示改为圈
        List<String> items_cp = new ArrayList<>(items);
        items_cp.set(dateInts[2]+5+firstDay," 〇 ");
        //初始化插入数据
        data_list.clear();
        for(int i=0,len=items_cp.size();i<len;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item", items_cp.get(i));
            data_list.add(map);
        }
    }

    private void updateStateDisplay(){
        StringBuilder sb = new StringBuilder(date);
        switch (item_state.get(getIndex(dateInts[2]))){
            case HEAD:
                break;
            case FINISH:
                sb.append(" 已完成！");
                break;
            case UNFINISH:
                sb.append(" 未完成！");
                break;
            case TOFINISHED:
                sb.append(" 待完成！");
                break;
            case EMPTY:
                break;
            case NEVER:
                break;
        }
        state.setText(sb.toString());
    }

    private void monthBack(){
        //将日期减一个月
        if(1==dateInts[1]){
            dateInts[1] = 12;
            dateInts[0]--;
        }else{
            dateInts[1]--;
        }
        date = buildDate(dateInts[0],dateInts[1]);
        dateInts = MyUtil.dateToInts(date);
    }

    private void monthForward(){
        //将日期加一个月
        if(12==dateInts[1]){
            dateInts[1] = 1;
            dateInts[0]++;
        }else{
            dateInts[1]++;
        }
        date = buildDate(dateInts[0],dateInts[1]);
        dateInts = MyUtil.dateToInts(date);
    }

    private String buildDate(int year,int month){
        //根据年月数字构建YYYY/MM/DD格式日期字符串
        StringBuilder sb = new StringBuilder();
        sb.append(year+"/");
        if(month<10){
            sb.append("0"+month+"/");
        }else{
            sb.append(month+"/");
        }
        //如果是本月，定位到今天，否则定位到1号
        if(isToMonth(year,month)){
            sb.append(today.substring(8));
        }else{
            sb.append("01");
        }
        return sb.toString();
    }

    private boolean isToMonth(int year, int month) {
        return year==Integer.parseInt(today.substring(0,4))&&
                month==Integer.parseInt(today.substring(5,7));
    }

    private int getIndex(int day){
        //根据item_state表得出日期所在索引
        int i = 7; //去掉头
        for(int len=item_state.size();i<len;i++){
            if(item_state.get(i)!=State.EMPTY){
                break;
            }
        }
        return i+day-1;
    }

    private int getDay(int index){
        //根据item_state得出索引所在的日数
        int i = 7; //去掉头
        for(int len=item_state.size();i<len;i++){
            if(item_state.get(i)!=State.EMPTY){
                break;
            }
        }
        return index-i+1;
    }
}