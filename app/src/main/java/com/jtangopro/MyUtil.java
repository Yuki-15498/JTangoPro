package com.jtangopro;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class MyUtil {

    public static String[] loadCSV(Context context) throws Exception {
        //加载csv文档，只在创建数据库时使用
        List<String> csvContent = new ArrayList<>();
        //获取字节流，并转为字符流
        InputStream in = context.getResources().openRawResource(R.raw.n1);
        String line = "";
        CSVFileUtil cfu = new CSVFileUtil(in);
        while ((line = cfu.readLine()) != null) {
            csvContent.add(line);
            //Log.d("lineMessage","line"+i+":"+csvContent[i]);
        }
        String[] csvContentArray = new String[csvContent.size()];
        csvContent.toArray(csvContentArray);
        return csvContentArray;
    }

    public static boolean judgeInput(String s){
        //用于判断输入是否合法
        if(s.isEmpty())
            return false;
        for(int i=0;i<s.length();i++)
            if (s.charAt(i) > '9' || s.charAt(i) < '0')
                return false;
        int number = Integer.parseInt(s);
        if(number<2||number>100)
            return false;
        return true;
    }

    public static Set<Integer> myRand(int range, int num){
        //生成[0,range)num个不重复随机整数
        Set<Integer> ans = new TreeSet<>();
        Random random = new Random();
        while(ans.size()!=num)
            ans.add(random.nextInt(range));
        return ans;
    }

    public static boolean isKana(char c){
        //判断输入的字符是否为假名
        final char[] table = {'あ','い','う','え','お','ア','イ','ウ','エ','オ',
                'か','き','く','け','こ','カ','キ','ク','ケ','コ',
                'さ','し','す','せ','そ','サ','シ','ス','セ','ソ',
                'た','ち','つ','て','と','タ','チ','ツ','テ','ト',
                'な','に','ぬ','ね','の','ナ','ニ','ヌ','ネ','ノ',
                'は','ひ','ふ','へ','ほ','ハ','ヒ','フ','ヘ','ホ',
                'ま','み','む','め','も','マ','ミ','ム','メ','モ',
                'や','ゆ','よ','ゃ','ゅ','ょ','ヤ','ユ','ヨ','ャ','ュ','ョ',
                'ら','り','る','れ','ろ','ラ','リ','ル','レ','ロ',
                'わ','を','ん','ワ','ヲ','ン',
                'が','ぎ','ぐ','げ','ご','ガ','ギ','グ','ゲ','ゴ',
                'ざ','じ','ず','ぜ','ぞ','ザ','ジ','ズ','ゼ','ゾ',
                'だ','ぢ','づ','で','ど','ダ','ヂ','ヅ','デ','ド',
                'ば','び','ぶ','べ','ぼ','バ','ビ','ブ','ベ','ボ',
                'ぱ','ぴ','ぷ','ぺ','ぽ','パ','ピ','プ','ペ','ポ',
                'っ','ー','～','∼'};
        for(int ch:table)
            if(ch==c)
                return true;
        return false;
    }

    public static boolean isFullKana(String s){
        //判断输入的词汇是否只包含假名
        for(int i=0,len=s.length();i<len;i++)
            if(!isKana(s.charAt(i)))
                return false;
        return true;
    }

    public static boolean containsKanji(String s){
        //判断输入的词汇是否含有汉字
        for(int i=0;i<s.length();i++)
            if(!isKana(s.charAt(i))&&!Character.isDigit(s.charAt(i)))
                return true;
        return false;
    }

    public static float getSize(String s){
        //根据输入的字符串长度决定字体大小
        float len = s.length();
        if(len<=4.0) len = (float)1.0;
        return (float) (15.0 + 35.0 / len);
    }

    public static float getSize2(String s){
        //根据输入的字符串长度决定字体大小2,适用于意思显示
        float len = s.length();
        if(len<=4.0) len = (float)1.0;
        return (float) (13.0 + 15.0 / len);
    }

    public static String myTrim(String s){
        //返回去掉首尾后的字符串
        return s.substring(1,s.length()-1);
    }

    public static String myTrim2(String s){
        //返回意思的主干（‘1.’后的意思）
        return s.substring(2);
    }

    public static int getDayOfMonth(int year,int month){
        //获取某年某月的天数
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int[] dateToInts(String date){
        //将字符串日期转为int数组
        int[] rst = new int[3];
        rst[0] = Integer.parseInt(date.substring(0,4));
        rst[1] = Integer.parseInt(date.substring(5,7));
        rst[2] = Integer.parseInt(date.substring(8));
        return rst;
    }

    public static boolean isSameMonth(String date1, String date2){
        //判断两个日期是否在同一个月
        int[] d1 = dateToInts(date1);
        int[] d2 = dateToInts(date2);
        return d1[0]==d2[0]&&d1[1]==d2[1];
    }

    public static boolean isSameMonth(int[] date1, int[] date2){
        //判断两个日期是否在同一个月
        return date1[0]==date2[0]&&date1[1]==date2[1];
    }

    public static int dateCmp(int[] date1, int[] date2){
        //判断两个日期的先后
        if(date1[0]==date2[0]&&date1[1]==date2[1]&&date1[2]==date2[2]){
            return 0;
        }
        int sum1 = date1[0]*10000 + date1[1]*100 + date1[2];
        int sum2 = date2[0]*10000 + date2[1]*100 + date2[2];
        return sum1>sum2?1:-1;
    }

    public static int monthCmp(int[] date1, int[] date2){
        //判断两个日期月份上的先后
        if(date1[0]==date2[0]&&date1[1]==date2[1]){
            return 0;
        }
        int sum1 = date1[0]*10000 + date1[1]*100;
        int sum2 = date2[0]*10000 + date2[1]*100;
        return sum1>sum2?1:-1;
    }

    public static String intsToString(int[] date){
        //int[]日期转String日期
        StringBuilder sb = new StringBuilder();
        sb.append(date[0]+"/");
        if(date[1]<10){
            sb.append("0"+date[1]+"/");
        }else{
            sb.append(date[1]+"/");
        }
        if(date[2]<10){
            sb.append("0"+date[2]);
        }else{
            sb.append(date[2]);
        }
        return sb.toString();
    }

}
