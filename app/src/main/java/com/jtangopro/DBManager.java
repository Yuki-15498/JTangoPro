/**
 * 管理数据库的读写
 * 使用单例模式，使用getDB方法返回一个可读写的SQLiteDatabase对象
 */

package com.jtangopro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class DBManager extends SQLiteOpenHelper {

    private static DBManager dbm = null;
    private static SQLiteDatabase db = null;
    public static String[] head = {"word", "kana", "pronu", "waudio", "trans", "example", "eaudio", "known", "prof"};

    public static final String CREATE_DB = "create table Total_Library ("
            + "id integer primary key autoincrement, "
            + head[0] + " text,"    //词
            + head[1] + " text,"    //假名
            + head[2] + " text,"   //音调（e.g.①②）
            + head[3] + " text,"  //单词读音
            + head[4] + " text,"   //词性+翻译
            + head[5] + " text," //例句
            + head[6] + " text,"  //例句读音
            + head[7] + " text,"   //是否掌握标记“0”，“1”（掌握则移出学习词库）
            + head[8] + " text)"; //熟练度标记，初始为0

    private Context mContext;

    private DBManager(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public static SQLiteDatabase getDB(Context context, String name,
                                       SQLiteDatabase.CursorFactory factory, int version){
        if(null == dbm)
            dbm = new DBManager(context, name, factory, version);
        db = dbm.getWritableDatabase();
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Total_Library");
        onCreate(sqLiteDatabase);
    }

    private static boolean checkDB(Context context){
        File dbFile = context.getDatabasePath("JTango.db");
        return dbFile.exists();
    }

    public static void initDB(Context context){
        //首次安装app时的初始化
        if(checkDB(context)) return;
        SQLiteDatabase db = DBManager.getDB(context, "JTango.db", null, 1);
        ContentValues values = new ContentValues();
        String[] csvContent = null;
        try {
            csvContent = MyUtil.loadCSV(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.beginTransaction();
        int numofWords = csvContent.length;
        for(int i=0;i<numofWords;i++){
            String[] line = csvContent[i].split(",",-1);
            String[] lineEx = new String[head.length];
            System.arraycopy(line,0,lineEx,0,line.length);
            for(int k=line.length;k<head.length;k++)
                lineEx[k] = "0";
            for(int k=0;k<head.length;k++)
                values.put(head[k],lineEx[k]);
            db.insert("Total_Library",null,values);
            values.clear();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public static int allCaseNum(Context context){
        //获取总词条数
        if(null == dbm)
            dbm = new DBManager(context, "JTango.db", null, 1);
        db = dbm.getWritableDatabase();
        String sql = "select count(*) from Total_Library";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}
