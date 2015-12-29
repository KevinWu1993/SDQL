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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.angmarch.views.NiceSpinner;
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
import sdql.fsyt.sdql.utils.KccjListAdapter;
import sdql.fsyt.sdql.utils.KccjStructure;

/**
 * Created by KevinWu on 2015/10/24.
 */
public class Kccj extends Fragment {
    private static final String URL = "http://jwc.jxnu.edu.cn/MyControl/All_Display.aspx?UserControl=xfz_cj.ascx&Action=Personal";
    private DefaultHttpClient client;//网络连接
    String value;
    String specialInfo, specialCookie;
    private Context context;
    private View view;
    private TextView tvUserInfo;
    private FrameLayout fcj;
    private RecyclerView rvKCCJ;//课程成绩列表
    private List<KccjStructure> cj = new ArrayList<KccjStructure>();//课程成绩列表
    DBHelper mDBHelper = null;//声明DBHelper类
    SQLiteDatabase mSQLiteDatabase = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kccj, container, false);
        context = container.getContext();
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c=mSQLiteDatabase.rawQuery("select * from CourseScore", null);
        SharedPreferences sp = context.getSharedPreferences("UserInfo", 0);
        //当总数小于等于0时才导入数据，这样写不利于数据更新，临时解决方法
        if(c.getCount()<=0) {

            specialCookie = sp.getString("Cookie", "");
            specialInfo = sp.getString("Special", "");
            readNet();
        }
        else{
            setSpinner();//设置下拉菜单方法
        }

        fcj=(FrameLayout)view.findViewById(R.id.kccj);
        //setBGColor();
        String uName= sp.getString("UserName", "");
        String uID=sp.getString("StuNum", "");
        tvUserInfo=(TextView)view.findViewById(R.id.tvxhxm);
        tvUserInfo.setText(uID + " " + uName);
        return view;
    }

    private void setSpinner() {
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();

        NiceSpinner niceSpinner = (NiceSpinner) view.findViewById(R.id.nice_spinner);
        final ArrayList<String> dataset = new ArrayList<>();
        Cursor c=mSQLiteDatabase.rawQuery("select distinct Term from CourseScore", null);//注意要加上distinct
          int count=c.getCount();
        c.moveToFirst();
        for(int i=0;i<count;i++){
            dataset.add(c.getString(c.getColumnIndex("Term")));
            c.moveToNext();
        }
        niceSpinner.attachDataSource(dataset);
        addData(dataset.get(0));//默认显示0号元素那个学期
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //System.out.println(parent.getItemAtPosition(position).toString());
                addData(dataset.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * 往recycleview里面添加数据的方法
     *
     * @param term
     */
    private void addData(String term) {
        cj.clear();//先清空列表
        rvKCCJ = (RecyclerView) view.findViewById(R.id.base_cj_list);//取得recycleview
        //添加测试数据
//        cj.add(new KccjStructure("职业生涯规划与就业指导和野外生存超长标题测试", "86"));
//        cj.add(new KccjStructure("数据结构", "96"));
//        cj.add(new KccjStructure("无线传感器网络", "50"));
        //添加数据
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c=mSQLiteDatabase.rawQuery("select * from CourseScore where Term ="+"'"+term+"'",null);
        int count=c.getCount();
        c.moveToFirst();
        for(int i=0;i<count;i++){
            cj.add(new KccjStructure(c.getString(c.getColumnIndex("CourseName")), c.getString(c.getColumnIndex("CourseScore"))));
            c.moveToNext();
        }

        rvKCCJ.setLayoutManager(new LinearLayoutManager(context));
        // 设置ItemAnimator
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        rvKCCJ.setHasFixedSize(true);
        KccjListAdapter kcadpt = new KccjListAdapter(context, cj);
        rvKCCJ.setAdapter(kcadpt);
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

                String aa[]=value.split("江西师范大学学生成绩表");
                System.out.println("分割出来的数据长度为："+aa.length);
               //System.out.println(aa[0]);
                Document doc = Jsoup.parse(aa[1]);
                Elements myTerm = doc.select("td[valign=middle]");//解析获得学期数据
                //System.out.println("取得的学期数："+myTerm.size());
                /*
                把取过来的数据用学期来遍历，每次一个学期，然后以学期的内容来分割字符串，遍历完了也刚刚好吧字符串都取完
                 */
                for(int i=0;i<myTerm.size();i++){
                    //如果满足条件就证明还没到最后一个学期
                    if(i<myTerm.size()-1){
                        String nowTerm=myTerm.get(i).text().toString();
                        String nextTerm=myTerm.get(i+1).text().toString();
                        String str1[]=value.split(nowTerm);
                        String str2[]=str1[1].split(nextTerm);
                        String jStr=str2[0];//这部分就是当前学期待解析的字符串
                        //System.out.println("解析出来的字符串："+jStr);
                        Document class_doc = Jsoup.parse(jStr);
                        Elements myClass = class_doc.select("font[color=#330099]");//解析获得每个课程的信息
                        //System.out.println("解析出来的课程信息长度为："+myClass.size());
                        //解析出来的课程每七个一次循环
                        for(int j=0;j<myClass.size()-7;j=j+7){
                            String courseID=myClass.get(j).text().toString();//课程号
                            String courseName=myClass.get(j+1).text().toString();//课程名
                            String courseCredit=myClass.get(j+2).text().toString();//课程学分
                            String courseScore=myClass.get(j+3).text().toString();//课程成绩
                            String againScore=myClass.get(j+4).text().toString();//补考成绩
                            String standardScore=myClass.get(j+5).text().toString();//标准分
                            //myClass.get(i+6).text().toString();//这个是备注，这些信息不需要
                            insertData(myTerm.get(i).text().toString(),courseID,courseName,courseCredit,courseScore,againScore,standardScore);
                        }
                    }
                    else if(i==myTerm.size()-1){
                        String nowTerm=myTerm.get(i).text().toString();
                        String str1[]=value.split(nowTerm);
                        String jStr=str1[1];//这部分就是当前学期待解析的字符串
                        //System.out.println("解析出来的字符串："+jStr);
                        Document class_doc = Jsoup.parse(jStr);
                        Elements myClass = class_doc.select("font[color=#330099]");//解析获得每个课程的信息
                        //System.out.println("解析出来的课程信息长度为："+myClass.size());
                        //解析出来的课程每七个一次循环
                        for(int j=0;j<myClass.size()-7;j=j+7){
                            String courseID=myClass.get(j).text().toString();//课程号
                            String courseName=myClass.get(j+1).text().toString();//课程名
                            String courseCredit=myClass.get(j+2).text().toString();//课程学分
                            String courseScore=myClass.get(j+3).text().toString();//课程成绩
                            String againScore=myClass.get(j+4).text().toString();//补考成绩
                            String standardScore=myClass.get(j+5).text().toString();//标准分
                            //myClass.get(i+6).text().toString();//这个是备注，这些信息不需要
                            insertData(myTerm.get(i).text().toString(),courseID,courseName,courseCredit,courseScore,againScore,standardScore);
                        }
                    }
                }
                setSpinner();//设置下拉菜单方法
            }
        }.execute();
    }
    private void insertData(String term,String courseID,String courseName, String courseCredit,String courseScore,String againScore,String standardScore){
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues v=new ContentValues();
        v.put("Term",term);
        v.put("CourseID",courseID);
        v.put("CourseName",courseName);
        v.put("CourseCredit",courseCredit);
        v.put("CourseScore",courseScore);
        v.put("AgainScore",againScore);
        v.put("StandardScore",standardScore);
        mSQLiteDatabase.insert("CourseScore", null, v);//插入CourseScore表
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        view=null;
    }
}
