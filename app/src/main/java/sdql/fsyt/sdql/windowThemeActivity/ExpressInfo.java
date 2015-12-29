package sdql.fsyt.sdql.windowThemeActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import sdql.fsyt.sdql.R;
import sdql.fsyt.sdql.timeline.DateAdapter;
import sdql.fsyt.sdql.timeline.DateText;

/**
 * Created by KevinWu on 2015/12/22.
 * 快递的信息展示类，作为弹窗的activity出现
 */
public class ExpressInfo extends AppCompatActivity{
    Toolbar toolbar;
    private static final String sURL = "https://route.showapi.com/64-19?";//请求url
    private String URL;
    private String COM="com=";//快递公司英文
    private String Nu="nu=";//快递单号
    private String SHOW_API="showapi_appid=14035";
    private String TIMESTAMP="";//时间戳
    private String SECRECT="showapi_sign=02229ddc59744d28af818143b1183bf6";
    private DefaultHttpClient client;//网络连接
    private String value;//网络返回值
    private String remark,num;//备注和单号
    ListView lvList;
    List<DateText> list =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.windows_expressinfo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("单号跟踪");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long d = new Timestamp(System.currentTimeMillis()).getTime();
        TIMESTAMP="showapi_timestamp="+d;
        Bundle extras = getIntent().getExtras();
        String com=getCom(extras.getString("co"));
        num=extras.getString("num");
        remark=extras.getString("rm");
        URL=sURL+COM+com+"&"+Nu+num+"&"+SHOW_API+"&"+TIMESTAMP+"&"+SECRECT;
        System.out.println(URL);
       getFromNet();
        }

    private String getCom(String str) {
        String backSTR=null;
        switch (str) {
            case "顺丰速运":
                backSTR="shunfeng";
                break;
            case "圆通快递":
                backSTR="yuantong";
                break;
            case "天天快递":
                backSTR="tiantian";
                break;
            case "百世汇通":
                backSTR="huitong";
                break;
            case "全峰快递":
                backSTR="quanfeng";
                break;
            case "申通快递":
                backSTR="shentong";
                break;
            case "韵达快递":
                backSTR="yunda";
                break;
            case "中通快递":
                backSTR="zhongtong";
                break;
            case "EMS":
                backSTR="ems";
                break;
            case "宅急送快递":
                backSTR="zjs";
                break;
            case "飞远物流":
                backSTR="feiyuan";
                break;
        }
        return backSTR;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
           finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void getFromNet() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... arg0) {
                long t= new Timestamp(System.currentTimeMillis()).getTime();//取得当前时间戳
                //拼接url
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
                System.out.println(value);
                try {
                    JSONObject jsonresult = new JSONObject(value);
                    //返回的状态码为0时表示查询成功
                    if( jsonresult.getInt("showapi_res_code")==0&&value!=null){
                        String jresult=jsonresult.getString("showapi_res_body");
                        JSONObject jsondata = new JSONObject(jresult);
                        JSONArray dataARRAY = jsondata.getJSONArray("data");
                       // System.out.println("解析出来的长度为："+dataARRAY.length());
                        for(int i=0;i<dataARRAY.length();i++){
                            String tempdata=dataARRAY.get(i).toString();
                            JSONObject jsontempdata = new JSONObject(tempdata);
                            String tempContext=jsontempdata.getString("context");
                            String tempTime=jsontempdata.getString("time");
                            DateText dt = new DateText(tempTime,tempContext);
                            list.add(dt);
                        }
                    }
                    else{
                        AlertDialog.Builder ad = new AlertDialog.Builder(ExpressInfo.this);
                        ad.setTitle("抱歉，查无此单！");
                        ad.setNegativeButton("返回",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        ad.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertDialog.Builder ad = new AlertDialog.Builder(ExpressInfo.this);
                    ad.setTitle("抱歉，查无此单！");
                    ad.setNegativeButton("返回",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    ad.show();
                }
                setView();
            }
        }.execute();
    }

    private void setView() {
        TextView tvRE=(TextView)findViewById(R.id.exRemark);
        TextView tvNum=(TextView)findViewById(R.id.exNum);
        lvList=(ListView)findViewById(R.id.lv_list);
        lvList.setAdapter(new DateAdapter(this,list));
        tvNum.setText(num);
        tvRE.setText(remark);
    }
}
