package sdql.fsyt.sdql.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import sdql.fsyt.sdql.R;
import sdql.fsyt.sdql.database.DBHelper;
import sdql.fsyt.sdql.utils.GetSstq;
import sdql.fsyt.sdql.utils.GetTime;


/**
 * Created by KevinWu on 2015/11/15.
 */
public class Sstq extends Fragment {
    public static final String TAG = "实时天气";
    private static final String URL = "http://op.juhe.cn/onebox/weather/query?cityname=%E5%8D%97%E6%98%8C%E5%8E%BF&dtype=&key=1294c8bcef6d7a05ac4fb25f43eaa242";
    private DefaultHttpClient client;//网络连接
    private String value;//网络返回的字符串
    private DBHelper mDBHelper = null;//声明DBHelper类
    private SQLiteDatabase mSQLiteDatabase = null;
    private Context context;
    private FrameLayout ftq;
    private View view;
    private SweetSheet mSweetSheet;
    private SweetSheet mSweetSheetShare;//分享菜单
    private TextView tvW;//一个字天气
    private ImageView btShare;//分享按钮
    private ImageView btRefresh;//更新按钮
    private ImageView btMore;//更多天气信息按钮
    private TextView tvDetail;//天气详情
    private TextView tvWendu;//天气温度信息（拼音命名。。）
    private TextView tvTGwendu;//体感温度信息
    private TextView tvUpdateTime;//更新时间信息
    private String city,
            temperature,
            info,
            temp1,
            temp2,
            updatetime,
            wind_direct,
            wind_power,
            wind_offset,
            wind_speed,
            cy_s,
            cy_l,
            yd_s,
            yd_l,
            gm_s,
            gm_l,
            zwx_s,
            zwx_l,
            wr_s,
            wr_l;//各个字段的信息

    GetTime gt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setWaveColor();//设置颜色的方法
        view = inflater.inflate(R.layout.fragment_sstq, container, false);
        context = container.getContext();
        ftq = (FrameLayout) view.findViewById(R.id.tq);
       // setBGColor();
        init();
        if (getDataFromDB() > 0) {
            setView();
        } else {
            readNet();
        }
        setSweetSheetView();
        setSweetSheetView_Share();
        return view;
    }



    /**
     * 从数据库中取得之前保存的数据出来存在变量中
     * 如果没有数据就返回-1
     */
    private int getDataFromDB() {
        int i = -1;
        //先从数据库中取得数据出来
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor cursor = mDBHelper.getCityWeather("南昌县");
        if (cursor.getCount() > 0) {
            i = 1;
            city = cursor.getString(cursor.getColumnIndex("city"));
            temperature = cursor.getString(cursor.getColumnIndex("temperature"));
            info = cursor.getString(cursor.getColumnIndex("info"));
            temp1 = cursor.getString(cursor.getColumnIndex("temp1"));
            temp2 = cursor.getString(cursor.getColumnIndex("temp2"));
            updatetime = cursor.getString(cursor.getColumnIndex("updatetime"));
            wind_direct = cursor.getString(cursor.getColumnIndex("wind_direct"));
            wind_power = cursor.getString(cursor.getColumnIndex("wind_power"));
            wind_offset = cursor.getString(cursor.getColumnIndex("wind_offset"));
            wind_speed = cursor.getString(cursor.getColumnIndex("wind_speed"));
            cy_s = cursor.getString(cursor.getColumnIndex("cy_s"));
            cy_l = cursor.getString(cursor.getColumnIndex("cy_l"));
            yd_s = cursor.getString(cursor.getColumnIndex("yd_s"));
            yd_l = cursor.getString(cursor.getColumnIndex("yd_l"));
            gm_s = cursor.getString(cursor.getColumnIndex("gm_s"));
            gm_l = cursor.getString(cursor.getColumnIndex("gm_l"));
            zwx_s = cursor.getString(cursor.getColumnIndex("zwx_s"));
            zwx_l = cursor.getString(cursor.getColumnIndex("zwx_l"));
            wr_s = cursor.getString(cursor.getColumnIndex("wr_s"));
            wr_l = cursor.getString(cursor.getColumnIndex("wr_l"));
        }
        return i;
    }

    /**
     * 设置ui方法
     */
    public void setView() {
        //设置天气主界面textview
        Log.d("天气描述","--"+info);
        tvW.setText(info.substring(info.length() - 1, info.length()));//取最后一个字符设置为天气概述
        //tvDetail.setText(status1);//设置天气详情
        tvWendu.setText(info + "   " + temp1 + "° / " + temp2 + "°");
        tvTGwendu.setText(temperature + "°");
        tvUpdateTime.setText(updatetime + " 更新");
    }

    private void init() {
        tvW = (TextView) view.findViewById(R.id.wgaisu);
        btRefresh = (ImageView) view.findViewById(R.id.btrefresh);
        btShare = (ImageView) view.findViewById(R.id.btshare);
        btMore = (ImageView) view.findViewById(R.id.btmore);
        //tvDetail = (TextView) view.findViewById(R.id.wdetail);
        tvWendu = (TextView) view.findViewById(R.id.wwendu);
        tvTGwendu = (TextView) view.findViewById(R.id.wtiganwendu);
        tvUpdateTime = (TextView) view.findViewById(R.id.wrefreshtime);


        //设置一个字天气概述的字体
        AssetManager asm = context.getAssets();
        Typeface tf = Typeface.createFromAsset(asm, "fonts/wfont.ttf");
        tvW.setTypeface(tf);


        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSweetSheetShare.toggle();
            }
        });

        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "从天气服务器更新的数据中，请稍后。。。", Toast.LENGTH_SHORT).show();
              Snackbar.make(view.getRootView(), "从天气服务器更新的数据中，请稍后~", Snackbar.LENGTH_LONG).show();
                GetSstq g = new GetSstq(context);
                readNet();
//                getDataFromDB();
//                setView();

            }
        });
        btMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSweetSheet.toggle();
            }
        });
    }

    //更多菜单
    private void setSweetSheetView() {
        mSweetSheet = new SweetSheet(ftq);
        //从menu 中设置数据源
        mSweetSheet.setMenuList(R.menu.weather_menu);
        mSweetSheet.setDelegate(new ViewPagerDelegate());
        //mSweetSheet.setBackgroundEffect(new DimEffect(0.5f));
        mSweetSheet.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                switch (position) {
                    case 0:
                        moreInfo("风力风向", "风向：" + wind_direct + "\n风力：" + wind_power);
                        break;
                    case 1:
                        moreInfo("穿衣建议", "概况：" + cy_s + "\n建议：" + cy_l);
                        break;
                    case 2:
                        moreInfo("运动建议", "概况：" + yd_s + "\n建议：" + yd_l);
                        break;
                    case 3:
                        moreInfo("感冒指数", "概况：" + gm_s + "\n提醒：" + gm_l);
                        break;
                    case 4:
                        moreInfo("紫外线强度", "强度：" + zwx_s + "\n建议：" + zwx_l);
                        break;
                    case 5:
                        moreInfo("污染程度", "情况：" + wr_s + "\n说明：" + wr_l);
                        break;
                }
                return false;
            }
        });
    }

    //分享菜单
    private void setSweetSheetView_Share() {
        mSweetSheetShare = new SweetSheet(ftq);
        //从menu 中设置数据源
        mSweetSheetShare.setMenuList(R.menu.share_menu);
        mSweetSheetShare.setDelegate(new RecyclerViewDelegate(false, 600));
        mSweetSheetShare.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {
                switch (position) {
                    case 0:

                        break;
                    case 1:
                        break;

                }
                return true;
            }
        });
    }

    private void moreInfo(String title, String content) {
        AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
        ad1.setTitle(title);
        ad1.setCancelable(false);
        ad1.setMessage(content);
        ad1.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int i) {
            }
        });
        ad1.show();
    }
    public void readNet() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... arg0) {
                //String urlString = arg0[0];
                client = new DefaultHttpClient();//实例化连接对象
                HttpGet httpget = new HttpGet(URL);

                try {
                    HttpResponse response = client.execute(httpget);
                    System.out.println("请求返回的代码为："+response.getStatusLine().getStatusCode());
                    //判断状态码符不符合规范先
                    if (response.getStatusLine().getStatusCode() == 200) {
                        value = EntityUtils.toString(response.getEntity(), "UTF-8");//取得返回的内容
                        System.out.println(value);

                    }

                } catch (IOException e) {
                    System.out.println("异常");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
               new GetSstq(context).setDATA(value);
                getDataFromDB();
                setView();
            }


        }.execute();
    }
}
