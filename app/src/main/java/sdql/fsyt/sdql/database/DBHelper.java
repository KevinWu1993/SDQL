package sdql.fsyt.sdql.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by KevinWu on 2015/10/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    SQLiteDatabase mSQLiteDatabase = null;
    private static DBHelper mInstance = null;
    private static final int DATABASE_VERSION = 1;//数据库版本
    public static final String DATABASE_NAME = "SDQL.db";//数据库名称

    /*
    * CourseTable表格字段
    * */
    public static final String KEY_ID = "_ID";
    public static final String KEY_StuNum = "StuNum";//学号
    public static final String KEY_OneTwo = "OneTwo";//当天第一节二课
    public static final String KEY_Three = "Three";//当天第三节课
    public static final String KEY_Four = "Four";//当天第四节课
    public static final String KEY_Five = "Five";//当天第五节课
    public static final String KEY_SixSeven = "SixSeven";//当天第六七节课
    public static final String KEY_EightNine = "EightNine";//当天第八九节课
    public static final String KEY_Night = "Night";//当天第晚上节课
    public static final String KEY_Week = "Week";//星期
    public static final String KEY_Term = "Term";//学期

    /*
    * CourseInfo课程信息表字段
    * */
    public static final String KEY_CourseName = "CourseName";
    public static final String KEY_CourseID = "CourseID";
    public static final String KEY_CourseTeacher = "CourseTeacher";
    public static final String KEY_CourseWide = "CourseWide";


    //构造方法
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable.COURSETABLE_TABLE_CREATE);
        db.execSQL(CreateTable.COURSEINFO_TABLE_CREATE);//向数据库中添加表
        db.execSQL(CreateTable.WEATHER_TABLE_CREATE);
        db.execSQL(CreateTable.COURSESCORE_TABLE_CREAT);//添加课程成绩表
        db.execSQL(CreateTable.EXAMTIMETABLE_TABLE_CREAT);//添加考试安排表
        db.execSQL(CreateTable.RECENTEXPRESS_TABLE_CREAT);//添加最近快递表
        db.execSQL(CreateTable.NEWSSDYW_TABLE_CREATE);//添加师大要闻表
        db.execSQL(CreateTable.NEWSXYDT_TABLE_CREATE);//添加校园动态表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 单例模式
     * 实例中没有使用这个模式，暂时注释
     **/
    public static synchronized DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    //查询指定城市天气
    public Cursor getCityWeather(String key) {
        mSQLiteDatabase = this.getWritableDatabase();
        try {
            Cursor mCursor = mSQLiteDatabase.rawQuery(
                    "select * from Weather where " + "city=?",
                    new String[]{key});
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (SQLException e) {
            return null;
        }

    }

    //仅用于测试
    public Cursor exSearch(String key) {
        SQLiteDatabase sql = getReadableDatabase();
       // System.out.println("前" + key.trim() + "后");
        Cursor cursor = sql.rawQuery("select * from CourseInfo where CourseName='" + key.trim() + "';", null);
        System.out.println("select * from CourseInfo where CourseName='" + key + "';");
        return cursor;
    }

    public Cursor getSpecial(String table, String whichKey, String key) {
        mSQLiteDatabase = this.getWritableDatabase();
        try {
            Cursor mCursor = mSQLiteDatabase.rawQuery(
                    "select * from " + table + " where " + whichKey + "=?",
                    new String[]{key});
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (SQLException e) {
            return null;
        }

    }

    //查询一个Realid对应的值
    public Cursor getSpecial2(String table, String whichKey, String key) {
        mSQLiteDatabase = this.getWritableDatabase();
        try {
            Cursor mCursor = mSQLiteDatabase.query(table, new String[]{whichKey}, whichKey + "=?", new String[]{key}, null, null, null);
            if (null != mCursor) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (SQLException e) {
            return null;
        }

    }
}