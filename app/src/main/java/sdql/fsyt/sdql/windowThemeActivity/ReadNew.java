package sdql.fsyt.sdql.windowThemeActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;

import sdql.fsyt.sdql.R;
import sdql.fsyt.sdql.timeline.DateText;

/**
 * Created by KevinWu on 2015/12/25.
 * 新闻阅读Activity
 * 从外部传入新闻标题和url，在这里解析并展示新闻内容
 */
public class ReadNew extends AppCompatActivity {
    private String newsURL;//新闻地址
    private String headURL="http://news.jxnu.edu.cn";
    private String newsTitle;//新闻标题
    private DefaultHttpClient client;//网络连接
    private String value;//网络返回值
    Toolbar toolbar;
    ImageView[] imageViews;//图片数组
    ArrayList imageUrl=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.windows_readnews);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        toolbar.setTitle(extras.getString("Type"));
        String nTitle = extras.getString("title");
        String nTime=extras.getString("time");
        newsURL = "http://news.jxnu.edu.cn"+extras.getString("url");
        TextView tvTitle=(TextView)findViewById(R.id.title);
        TextView tvInfo=(TextView)findViewById(R.id.newsinfo);
        tvTitle.setText(nTitle);
        tvInfo.setText("来源：江西师范大学    更新时间："+nTime);
        getDataFromNet();
    }

    public void getDataFromNet() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... arg0) {
                //拼接url
                client = new DefaultHttpClient();//实例化连接对象
                HttpGet httpget = new HttpGet(newsURL);
                try {
                    HttpResponse response = client.execute(httpget);
                    //判断状态码符不符合规范先
                    if (response.getStatusLine().getStatusCode() == 200) {
                        System.out.println("成功返回目标网页");
                        value = EntityUtils.toString(response.getEntity());//取得返回的内容
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                System.out.println("数据的长度为：" + value.length());
                System.out.println("下面是取得的网页数据" + value + "  wang");
                System.out.println(value);
                Document ap_doc = Jsoup.parse(value);
                Elements myInfo = ap_doc.select("font[size=4]");//解析获得每个信息
                System.out.println("解析出来的新闻信息长度为：" + myInfo.size());
                int picSize=0;
                String contentText = "";//新闻文本内容
                for (int i = 0; i < myInfo.size(); i++) {
                    System.out.println(myInfo.get(i).text());
                    String myURLinfo[]=myInfo.get(i).toString().split("src=\"");
                    if(myURLinfo.length>1){
                        String durl[]=myURLinfo[1].split("\"></font>");
                        System.out.println(durl[0]);
                        imageUrl.add(durl[0]);
                        picSize++;
                    }else{
                       contentText=contentText+myInfo.get(i).text().replace("     ","\n\n     ");
                    }
                }
                initImage(picSize);//初始化图片资源
                setContentText(contentText);//设置文本
                getHttpPic();//从网络获取图片
            }
        }.execute();
    }


    private void setContentText(String contentText) {
        TextView tv=(TextView)findViewById(R.id.contenttext);
        TextView tvLink=(TextView)findViewById(R.id.link);
        tv.setText(contentText);
        tvLink.setText(Html.fromHtml("<a href='"+newsURL+"'>查看原文</a>") );
        tvLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 参数为图片的个数
     *
     * @param imageSize
     */
    private void initImage(int imageSize) {
        ViewGroup group = (ViewGroup) findViewById(R.id.content);
         imageViews = new ImageView[imageSize];
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        for (int i = 0; i < imageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            ViewGroup.LayoutParams iLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(screenWidth/1.9));
            imageView.setLayoutParams(iLayout);
            imageView.setPadding(0, 15, 0, 15);
            imageViews[i] = imageView;
            imageView.setImageResource(R.drawable.loading);
            group.addView(imageView);
        }
    }

    public void getHttpPic() {
        new AsyncTask<String, Void, String>() {
            Bitmap bitmap[]=new Bitmap[imageViews.length];
            @Override
            protected String doInBackground(String... arg0) {
                URL myFileURL;
                try{
                    for(int i=0;i<imageViews.length;i++){
                    myFileURL = new URL(headURL+imageUrl.get(i));
                    //获得连接
                    HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
                    //设置超时时间为6000毫秒
                    conn.setConnectTimeout(6000);
                    //连接设置获得数据流
                    conn.setDoInput(true);
                    //不使用缓存
                    conn.setUseCaches(false);
                    //这句可有可无，没有影响
                    //conn.connect();
                    //得到数据流
                    InputStream is = conn.getInputStream();
                    //解析得到图片
                    bitmap[i] = BitmapFactory.decodeStream(is);
                    //关闭数据流
                   is.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                for(int i=0;i<imageViews.length;i++){
                    imageViews[i].setImageBitmap(bitmap[i]);
                }
            }
        }.execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
