package com.jtangopro;

import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 学习过程中各个活动传递的数据
 * 根据学习过程反馈数据
 */

public class PassValues implements Serializable {

    private ArrayList<Tango> tangos;
    private int cntKnown;
    private String tangeCardType;

    //维护队列和队列容器，用于控制流程
    private List<Map.Entry<String,Integer>> initList = new LinkedList<>();
    private List<List<Map.Entry<String,Integer>>> lists = new LinkedList<>();

    //键值对，代表当前学习
    //键代表studyCard类型，值代表tango索引
    //type1:给词识义，type2：给义识词。type3：识汉字
    //type3中纯假名的tango不加入
    private Map.Entry<String,Integer> curStudy;

    public PassValues(){}

    public PassValues(ArrayList<Tango> t, int c){
        this.tangos = t;
        this.cntKnown = c;
        this.listInit();
    }

    private void listInit(){
        // 初始化List
        for(int i=0,len=tangos.size();i<len;i++)
            initList.add(new AbstractMap.SimpleEntry<String, Integer>("type1",i));
        for(int i=0;i<3;i++)
            lists.add(new LinkedList<Map.Entry<String, Integer>>());
        curStudy = initList.get(0);
    }

    private void pushData(int ind){
        //将初始list中的项推到list容器中
        lists.get(0).add(new AbstractMap.SimpleEntry<String, Integer>("type2", ind));
        //lists.get(1).add(new AbstractMap.SimpleEntry<String, Integer>("type1", ind));
        if(MyUtil.containsKanji(tangos.get(ind).get("word"))) lists.get(1).add(new AbstractMap.SimpleEntry<String, Integer>("type3", ind));
        lists.get(2).add(new AbstractMap.SimpleEntry<String, Integer>("type1", ind));
        for(List<Map.Entry<String,Integer>> tmp:lists)
            Collections.shuffle(tmp);
    }

    private boolean setCurStudy(){
        //返回值为false时，表示流程已结束
        if(!initList.isEmpty())
            curStudy = initList.get(0);
        else{
            List<Map.Entry<String,Integer>> curList = null;
            //找到当前第一个不为空的队列为执行队列
            for(List<Map.Entry<String,Integer>> tmp:lists)
                if(!tmp.isEmpty()){
                    curList = tmp;
                    break;
                }
            if(null==curList) return false;
            curStudy = curList.get(0);
        }
        return true;
    }

    public boolean continueStudy(boolean known){
        /* 核心，流程控制算法
         * 学习流程推进，初始队列不为空则执行初始队列，并将内容填入队列容器
         * 学习流程大致：type1->type2->(re1)-type1(shuffle)->type3->re2-type1(shuffle)
         * 返回值为false时，表示流程已结束 */
        if(!initList.isEmpty()){
            //初始队列执行
            initList.remove(0);
            if(!known) {
                int ind = curStudy.getValue();
                pushData(ind);
            }else this.cntKnown++;
        }else{
            //队列容器执行
            List<Map.Entry<String,Integer>> curList = null;
            //找到当前第一个不为空的队列为执行队列
            for(List<Map.Entry<String,Integer>> tmp:lists)
                if(!tmp.isEmpty()){
                    curList = tmp;
                    break;
                }
            if(null==curList) return false;
            curList.remove(0);
        }
        return setCurStudy();
    }

    public int getCurProf(){
        //取出当前tango的prof，这里将String转为int
        String prof = this.tangos.get(this.curStudy.getValue()).get("prof");
        return Integer.parseInt(prof);
    }

    public void setCurProf(int prof){
        //设置当前tange的prof，这里将int转为String，且补零到8位
        StringBuilder p = new StringBuilder(""+prof);
        while(p.length()<8)
            p.insert(0,"0");
        this.tangos.get(this.curStudy.getValue()).set("prof",p.toString());
    }

    public Tango getCurTango(){
        return this.tangos.get(this.curStudy.getValue());
    }

    public String getCurStudyType(){ return this.curStudy.getKey(); }

    public void setTangeCardType(String type){this.tangeCardType = type;}

    public String getTangeCardType(){return this.tangeCardType;}

    public int getCntKnown(){ return this.cntKnown; }

    public boolean isInit(){
        //是否处于初始队列
        if(initList.isEmpty()) return false;
        else return true;
    }

    public Set<String> getWords(){
        //获取所有tango的假名
        //要去重
        Set<String> s = new HashSet<>();
        for(Tango t:tangos)
            s.add(t.get("kana"));
        return s;
    }

    public ArrayList<Tango> getTangos(){ return tangos; }

    public void printProfInfo(){
        //调试用，打印所有词以及熟练度
        StringBuilder info = new StringBuilder();
        for(int i=0;i<tangos.size();i++){
            info.append(tangos.get(i).get("word"));
            info.append(": ");
            info.append(tangos.get(i).get("prof"));
            info.append("; ");
        }
        Log.d("tangos",info.toString());
    }

    public void increaseAllProf(){
        //所有词熟练度+1
        for(Tango t:tangos){
            int prof_ = Integer.parseInt(t.get("prof")) + 1;
            StringBuilder p = new StringBuilder(""+prof_);
            while(p.length()<8)
                p.insert(0,"0");
            t.set("prof",p.toString());
        }
    }

}
