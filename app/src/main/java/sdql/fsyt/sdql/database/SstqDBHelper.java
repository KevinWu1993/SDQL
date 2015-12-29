package sdql.fsyt.sdql.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by KevinWu on 2015/12/8.
 */
public class SstqDBHelper  extends SQLiteOpenHelper {
    SQLiteDatabase mSQLiteDatabase = null;
    private static DBHelper mInstance = null;
    private static final int DATABASE_VERSION = 1;//数据库版本
    public static final String DATABASE_NAME = "JSDQX.db";//数据库名称

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
    private static final String WEATHER_TABLE_CREATE = "create table Weather("
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "City TEXT,"
            + "W1day TEXT,"
            + "W1weather TEXT,"
            + "W1temp TEXT,"
            + "Rt TEXT,"
            + "W2day TEXT,"
            + "W2weather TEXT,"
            + "W2temp TEXT,"
            + "W3day TEXT,"
            + "W3weather TEXT,"
            + "W3temp TEXT,"
            + "W4day TEXT,"
            + "W4weather TEXT,"
            + "W4temp TEXT,"
            + "Updatetime TEXT,"
            + "Realid INTEGER"
            + " );";//创建表语句

    private static final String TABLE_CREATE1 = "create table CourseTable("//创建表语句
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "StuNum TEXT,"
            + "Week TEXT,"
            + "OneTwo TEXT,"
            + "Three TEXT,"
            + "Four TEXT,"
            + "Five TEXT,"
            + "SixSeven TEXT,"
            + "EightNine TEXT,"
            + "Night TEXT"
            + " );";
    private static final String TABLE_CREATE2 = "create table CourseInfo(" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "CourseName TEXT," +
            "CourseID TEXT," +
            "CourseTeacher TEXT," +
            "CourseWide TEXT," +
            "CourseNameListLink TEXT," +
            "CourseForumLink TEXT);";

    //构造方法
    public SstqDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SstqDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE1);
        db.execSQL(TABLE_CREATE2);//向数据库中添加表
        db.execSQL(WEATHER_TABLE_CREATE);
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

    //查询指定课程信息
    public Cursor getCityWeather(String key) {
        mSQLiteDatabase = this.getWritableDatabase();
        try {
            Cursor mCursor = mSQLiteDatabase.rawQuery(
                    "select * from Weather where " + "City=?",
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
        System.out.println("前" + key.trim() + "后");
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

    //把数据库统一转化为gbk格式的数据
    public void updateAll() {

    }

    //查询某个字段的全部记录
    public Cursor GetALL() {
        return null;
    }
}