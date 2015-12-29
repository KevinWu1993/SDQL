package sdql.fsyt.sdql.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sdql.fsyt.sdql.R;
import sdql.fsyt.sdql.database.DBHelper;
import sdql.fsyt.sdql.utils.KsapListAdapter;
import sdql.fsyt.sdql.utils.KsapStructure;


/**
 * Created by KevinWu on 2015/11/15.
 */
public class Ksap extends Fragment {
    private static final String URL = "http://jwc.jxnu.edu.cn/User/default.aspx?&code=129&&uctl=MyControl%5cxfz_test_schedule.ascx";
    private DefaultHttpClient client;//网络连接
    String value;
    String specialInfo, specialCookie;
    private Context context;
    private View view;
    private FrameLayout fap;
    private RecyclerView rvKSAP;//考试安排列表
    private List<KsapStructure> ap = new ArrayList<KsapStructure>();//考试安排列表
    DBHelper mDBHelper = null;//声明DBHelper类
    SQLiteDatabase mSQLiteDatabase = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ksap, container,false);
        context = container.getContext();
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c=mSQLiteDatabase.rawQuery("select * from ExamTimeTable", null);
        SharedPreferences sp = context.getSharedPreferences("UserInfo", 0);
        //当总数小于等于0时才导入数据，这样写不利于数据更新，临时解决方法
        if(c.getCount()<=0) {

            specialCookie = sp.getString("Cookie", "");
            specialInfo = sp.getString("Special", "");
            readNet();
        }
        else{
             addData();
        }

        fap=(FrameLayout)view.findViewById(R.id.ksap);
        //setBGColor();
        return view;
    }

    /**
     * 往recycleview里面添加数据的方法
     *
     */
    private void addData() {
        ap.clear();//先清空列表
        rvKSAP = (RecyclerView) view.findViewById(R.id.base_ap_list);//取得recycleview
        //添加数据
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c=mSQLiteDatabase.rawQuery("select * from ExamTimeTable",null);
        int count=c.getCount();
        c.moveToFirst();
        for(int i=0;i<count;i++){
            ap.add(new KsapStructure("课程："+c.getString(c.getColumnIndex("CourseName")),
                    "时间："+c.getString(c.getColumnIndex("ExamTime"))
            ,"考场："+c.getString(c.getColumnIndex("ExamRoom"))
            ,"座位："+c.getString(c.getColumnIndex("ExamSeat"))
            ,"备注："+c.getString(c.getColumnIndex("Remark"))));
            c.moveToNext();
        }

        rvKSAP.setLayoutManager(new LinearLayoutManager(context));
        // 设置ItemAnimator
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        rvKSAP.setHasFixedSize(true);
        KsapListAdapter ksadpt = new KsapListAdapter(context, ap);
        rvKSAP.setAdapter(ksadpt);
    }



    public void readNet() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... arg0) {
                client = new DefaultHttpClient();//实例化连接对象
                HttpGet httpget = new HttpGet(URL);
                httpget.setHeader("Cookie", "ASP.NET_SessionId=" + specialCookie + ";" +
                        "JwOAUserSettingNew=" + specialInfo);
                try {
                    HttpResponse response = client.execute(httpget);

                    //判断状态码符不符合规范先
                    if (response.getStatusLine().getStatusCode() == 200) {
                        value = EntityUtils.toString(response.getEntity());//取得返回的内容
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {


                        //System.out.println("解析出来的字符串："+jStr);
                        Document ap_doc = Jsoup.parse(value);
                        Elements myAp= ap_doc.select("font[color=#330099]");//解析获得每个课程的信息
                        System.out.println("解析出来的课程信息长度为："+myAp.size());
                        //解析出来的课程每七个一次循环
                        for(int j=0;j<myAp.size();j=j+7){
                            String courseID=myAp.get(j).text().toString();//课程号
                            String courseName=myAp.get(j+1).text().toString();//课程名
                            //j+2为学号信息，不需要
                            String examTime=myAp.get(j+3).text().toString();//考试时间
                            String examRoom=myAp.get(j + 4).text().toString();//考场
                            String examSeat=myAp.get(j+5).text().toString();//座位号
                            String remark=myAp.get(j+6).text().toString();//这个是备注
                            insertData(courseID,courseName,examTime,examRoom,examSeat,remark);
                        }
                addData();
            }
        }.execute();
    }
    private void insertData(String courseID,String courseName, String examTime,String examRoom,String examSeat,String remark){
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues v=new ContentValues();
        v.put("CourseID",courseID);
        v.put("CourseName",courseName);
        v.put("ExamTime",examTime);
        v.put("ExamRoom",examRoom);
        v.put("ExamSeat",examSeat);
        v.put("Remark",remark);
        mSQLiteDatabase.insert("ExamTimeTable", null, v);//插入CourseScore表
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        view=null;
    }
}
