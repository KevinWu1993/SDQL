package sdql.fsyt.sdql.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import sdql.fsyt.sdql.utils.GetTime;
import sdql.fsyt.sdql.utils.KsapListAdapter;
import sdql.fsyt.sdql.utils.KsapStructure;
import sdql.fsyt.sdql.utils.XwListAdapter;
import sdql.fsyt.sdql.utils.XwStructure;
import sdql.fsyt.sdql.windowThemeActivity.ExpressInfo;
import sdql.fsyt.sdql.windowThemeActivity.ReadNew;

/**
 * Created by KevinWu on 2015/11/15.
 * 师大要闻fragment
 */
public class Sdyw  extends Fragment {
    private static final String URL = "http://news.jxnu.edu.cn/s/271/t/910/p/12/list.htm";
    private DefaultHttpClient client;//网络连接
    String value;//网络返回值
    private Context context;
    private View view;
    private RecyclerView xwRView;//新闻的recycleview列表
    private List<XwStructure> xw = new ArrayList<XwStructure>();//新闻标题内容列表
    DBHelper mDBHelper = null;//声明DBHelper类
    SQLiteDatabase mSQLiteDatabase = null;
    XwListAdapter xwAdapter;//新闻列表适配器
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_xw, container, false);
        context = container.getContext();
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c=mSQLiteDatabase.rawQuery("select * from NewsSDYW", null);
        int count=c.getCount();//取得记录总数
        String nowTime=new GetTime().getDay();
        System.out.println("当前日期"+nowTime);
        c.moveToFirst();
        //当总数小于等于0时或者更新时间字段不等于当前时间时才导入数据
        if(count<=0) {
            readNet();
        }
//        这里写更新时间
        else if((!nowTime.equals(c.getString(c.getColumnIndex("UpdateTime"))))&&nowTime!=null){
//            这种情况就先清空表，避免重复数据
            mSQLiteDatabase.execSQL("delete from NewsSDYW");
            readNet();
        }
        else{
            addData();
            setUI();//设置UI
        }
        return view;
    }

    private void setUI() {
        xwAdapter.setOnItemClickListener(new XwListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, XwStructure xw) {
                System.out.println("数据为：" + xw.getNewsTitle());
                Intent intent = new Intent();
                intent.setClass(context, ReadNew.class);
                intent.putExtra("title", xw.getNewsTitle());
                intent.putExtra("time",xw.getNewsTime());
                intent.putExtra("url",xw.getNewsURL());
                intent.putExtra("Type","师大要闻");
                startActivity(intent);
            }
        });

    }

    /**
     * 往recycleview里面添加数据的方法
     *
     */
    private void addData() {
        xw.clear();//先清空列表
        xwRView = (RecyclerView) view.findViewById(R.id.base_xw_list);//取得recycleview
        //添加数据
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c=mSQLiteDatabase.rawQuery("select * from NewsSDYW",null);//查询师大要闻表
        int count=c.getCount();
        c.moveToFirst();
        for(int i=0;i<count;i++){
            xw.add(new XwStructure(c.getString(c.getColumnIndex("NewsTitle")),
                    c.getString(c.getColumnIndex("NewsTime"))
                    ,c.getString(c.getColumnIndex("NewsURL"))));
            c.moveToNext();
        }

        xwRView.setLayoutManager(new LinearLayoutManager(context));
        // 设置ItemAnimator
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        xwRView.setHasFixedSize(true);
        xwAdapter = new XwListAdapter(context, xw);
        xwRView.setAdapter(xwAdapter);
    }



    public void readNet() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... arg0) {
                client = new DefaultHttpClient();//实例化连接对象
                HttpGet httpget = new HttpGet(URL);
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
                String timeInfo,titleInfo;//时间和标题信息
                String newsURL;//分离出来的URL信息
                Document doc = Jsoup.parse(value);
                Elements myData= doc.select("table[class=columnStyle]");//解析获得每个新闻标题的信息
                System.out.println("解析出来的新闻标题信息长度为：" + myData.size());
                for(int i=0;i<myData.size();i++){
                    String dstStr=myData.get(i).text();//待解析字符串
                    String referenceStr="2015-12-24";//参考字符串
                    timeInfo=dstStr.substring(dstStr.length() - referenceStr.length(), dstStr.length());//分离出来的时间信息
                    titleInfo=dstStr.substring(0,dstStr.length()-referenceStr.length()).trim();//分离出来新闻标题并去掉前后字符串
                    System.out.println(myData.get(i).text());
                    System.out.println("分离出来的时间信息为："+timeInfo);
                    System.out.println("分离出来的标题信息为："+titleInfo);
                    String dstSStr=myData.get(i).toString();//待解析的源字符串
                    String urlTempStr1[]=dstSStr.split("href=\"");
                    if(urlTempStr1.length>=1){
                        String urlTempStr2[]=urlTempStr1[1].split("\" target=\"_blank\"");
                        if(urlTempStr2.length>=1){
                            newsURL=urlTempStr2[0];
                            System.out.println("分离出来的url信息为："+newsURL);
                            //如果成功获取到新闻的地址就写入数据库
                            insertData(titleInfo,timeInfo,newsURL);
                        }
                    }
                }
                addData();
                setUI();//设置UI
            }
        }.execute();
    }
    private void insertData(String newsTitle,String newsTime, String newsURL){
        //取得当前日期
        GetTime gt=new GetTime();
        String nowTime=gt.getDay();
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues v=new ContentValues();
        v.put("NewsTitle",newsTitle);
        v.put("NewsTime",newsTime);
        v.put("NewsURL",newsURL);
        v.put("UpdateTime",nowTime);
        mSQLiteDatabase.insert("NewsSDYW", null, v);//插入新闻缓存表
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        view=null;
    }
}

