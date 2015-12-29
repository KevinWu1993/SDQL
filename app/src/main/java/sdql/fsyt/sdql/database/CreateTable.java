package sdql.fsyt.sdql.database;

/**
 * Created by KevinWu on 2015/12/17.
 * 由于要创建的表太多，所以创建一个类来保存需要创建的表的创建语句
 */
public class CreateTable {
    public static final String WEATHER_TABLE_CREATE = "create table Weather("
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "city TEXT,"//天气对应的城市
            + "temperature TEXT,"//温度信息
            + "info TEXT,"//天气信息
            + "temp1 TEXT,"//高温
            + "temp2 TEXT,"//低温
            + "updatetime TEXT,"//天气在服务器的更新时间
            + "wind_direct TEXT,"//风向
            + "wind_power TEXT,"//风力
            + "wind_offset TEXT,"//风向偏移量
            + "wind_speed TEXT,"//风速
            + "cy_s TEXT,"//穿衣建议
            + "cy_l TEXT,"//穿衣建议详情
            + "yd_s TEXT,"//运动建议
            + "yd_l TEXT,"//运动建议详情
            + "gm_s TEXT,"//感冒指数
            + "gm_l TEXT,"//感冒指数详情
            + "zwx_s TEXT,"//紫外线说明
            + "zwx_l TEXT,"//紫外线详情
            + "wr_s TEXT,"//污染说明
            + "wr_l TEXT"//污染详情
            + " );";//创建表语句

    public static final String COURSETABLE_TABLE_CREATE = "create table CourseTable("//创建表语句
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"//自动增长ID
            + "StuNum TEXT,"//学号
            + "Week TEXT,"//周几的课
            + "OneTwo TEXT,"//第一第二节课
            + "Three TEXT,"//第三节课
            + "Four TEXT,"//第四节课
            + "Five TEXT,"//第五节课
            + "SixSeven TEXT,"//第六第七节课
            + "EightNine TEXT,"//第八第九节课
            + "Night TEXT"//晚上的课
            + " );";
    public static final String COURSEINFO_TABLE_CREATE = "create table CourseInfo(" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "CourseName TEXT," +//课程名
            "CourseID TEXT," +//课程ID
            "CourseTeacher TEXT," +//课程老师
            "CourseWide TEXT," +//课程班别
            "CourseNameListLink TEXT," +//课程信息连接
            "CourseForumLink TEXT);";//课程讨论区连接
    //课程成绩表
    public static final String COURSESCORE_TABLE_CREAT = "create table CourseScore(" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Term TEXT," +//学期
            "CourseID TEXT," +//课程号
            "CourseName TEXT," +//课程名
            "CourseCredit TEXT," +//课程学分
            "CourseScore TEXT," +//课程成绩
            "AgainScore TEXT," +//补考成绩
            "StandardScore TEXT);";//标准分
    //课程成绩表
    public static final String EXAMTIMETABLE_TABLE_CREAT = "create table ExamTimeTable(" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "CourseID TEXT," +//课程id
            "CourseName TEXT," +//课程名
            "ExamTime TEXT," +//考试时间
            "ExamRoom TEXT," +//考试考场
            "ExamSeat TEXT," +//考试座位
            "Remark TEXT);";//备注信息
    //最近快递列表
    public static final String RECENTEXPRESS_TABLE_CREAT = "create table RecentExpress(" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ExCompany TEXT," +//快递公司
            "ExNum TEXT," +//快递号
            "ExRemark TEXT);";//快递备注
    //        新闻——师大要闻表
    public static final String NEWSSDYW_TABLE_CREATE = "create table NewsSDYW("//创建表语句
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"//自动增长ID
            + "NewsTitle TEXT,"//新闻标题
            + "NewsTime TEXTT,"//新闻时间
            + "NewsURL TEXT,"//新闻URL
            + "NewsPicURL TEXT,"//新闻图片URL（预留）
            + "UpdateTime TEXT"//新闻更新时间
            + " );";
    //        新闻——校园动态表
    public static final String NEWSXYDT_TABLE_CREATE = "create table NewsXYDT("//创建表语句
            + "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"//自动增长ID
            + "NewsTitle TEXT,"//新闻标题
            + "NewsTime TEXTT,"//新闻时间
            + "NewsURL TEXT,"//新闻URL
            + "NewsPicURL TEXT,"//新闻图片URL（预留）
            + "UpdateTime TEXT"//新闻更新时间
            + " );";
}
