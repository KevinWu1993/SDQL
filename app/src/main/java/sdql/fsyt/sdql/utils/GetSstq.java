package sdql.fsyt.sdql.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sdql.fsyt.sdql.database.DBHelper;


/**
 * Created by KevinWu on 2015/12/8.
 * 获取实时天气
 */
public class GetSstq {

    private  Context context = null;
    private DBHelper mDBHelper;
    private  SQLiteDatabase mSQLiteDatabase;

    public GetSstq(Context context) {
        this.context = context;
    }



    public   int setDATA(String value) {
        try {
            //取得result根
            System.out.println("取得的值为："+value);
            JSONObject jsonresult = new JSONObject(value);
            String resultSTR = jsonresult.getString("result");

            //取得DATA根
            JSONObject jsonDATA = new JSONObject(resultSTR);
            String dataSTR = jsonDATA.getString("data");

            //取得realtime根
            JSONObject jsonRT = new JSONObject(dataSTR);
            String rtSTR = jsonRT.getString("realtime");
            JSONObject jsonRealTime = new JSONObject(rtSTR);//这个是realtime的json对象
            String city = jsonRealTime.getString("city_name"); //取得城市名称
            String updatetime = jsonRealTime.getString("date") + " " + jsonRealTime.getString("time");//获得更新时间，即为返回的数据的日期加更新具体时间

            String weatherSTR = jsonRealTime.getString("weather");
            JSONObject jsonWeather = new JSONObject(weatherSTR);//这个是weather的json对象
            String temperature = jsonWeather.getString("temperature");//获得实时温度
            String info = jsonWeather.getString("info");//实时天气状况

            String windSTR = jsonRealTime.getString("wind");
            JSONObject jsonWind = new JSONObject(windSTR);
            String wind_direct = jsonWind.getString("direct");//风向信息
            String wind_power = jsonWind.getString("power");//风力信息
            String wind_offset = jsonWind.getString("offset");//偏移量信息
            String wind_speed = jsonWind.getString("windspeed");//风速信息

            String lifeSTR = jsonRT.getString("life");
            JSONObject jsonLife = new JSONObject(lifeSTR);
            String infoSTR = jsonLife.getString("info");
            JSONObject jsonInfo = new JSONObject(infoSTR);
            JSONArray cyARRAY = jsonInfo.getJSONArray("chuanyi");
            String cy_s = cyARRAY.getString(0);//穿衣建议概述
            String cy_l = cyARRAY.getString(1);//穿衣建议详细说明

            JSONArray ydARRAY = jsonInfo.getJSONArray("yundong");
            String yd_s = ydARRAY.getString(0);
            String yd_l = ydARRAY.getString(1);

            JSONArray gmARRAY = jsonInfo.getJSONArray("ganmao");
            String gm_s = gmARRAY.getString(0);
            String gm_l = gmARRAY.getString(1);

            JSONArray zwxARRAY = jsonInfo.getJSONArray("ziwaixian");
            String zwx_s = zwxARRAY.getString(0);
            String zwx_l = zwxARRAY.getString(1);

            JSONArray wrARRAY = jsonInfo.getJSONArray("wuran");
            String wr_s = wrARRAY.getString(0);
            String wr_l = wrARRAY.getString(1);

            JSONArray weatherARRAY = jsonRT.getJSONArray("weather");
            String WertherSTR = weatherARRAY.getString(0);//取第一天天气，就是当天的天气
            JSONObject jsonTEMP = new JSONObject(WertherSTR);
            String InfoSTR = jsonTEMP.getString("info");//取得早晚天气字符串
            JSONObject jsonT = new JSONObject(InfoSTR);
            JSONArray tempARRAY1 = jsonT.getJSONArray("day");//取得白天数据
            String temp1 = tempARRAY1.getString(2);//取得白天天气

            JSONArray tempARRAY2 = jsonT.getJSONArray("night");//取得夜间天气
            String temp2 = tempARRAY2.getString(2);//获得夜间天气
            mDBHelper = new DBHelper(context);
            mSQLiteDatabase = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("city", city);
            values.put("temperature", temperature);
            values.put("info", info);
            values.put("temp1", temp1);
            values.put("temp2", temp2);
            values.put("updatetime", updatetime);
            values.put("wind_direct", wind_direct);
            values.put("wind_power", wind_power);
            values.put("wind_offset", wind_offset);
            values.put("wind_speed", wind_speed);
            values.put("cy_s", cy_s);
            values.put("cy_l", cy_l);
            values.put("yd_s", yd_s);
            values.put("yd_l", yd_l);
            values.put("gm_s", gm_s);
            values.put("gm_l", gm_l);
            values.put("zwx_s", zwx_s);
            values.put("zwx_l", zwx_l);
            values.put("wr_s", wr_s);
            values.put("wr_l", wr_l);

            //如果数据表中存在记录就更新，如果不存在记录插入
            if (mDBHelper.getCityWeather("南昌县").getCount() <= 0) {
                System.out.println("执行1");
                mSQLiteDatabase.insert("Weather", null, values);
            } else {
                System.out.println("执行2");
                mSQLiteDatabase.update("Weather", values, "city=?", new String[]{"南昌县"});//仅查询一个城市的情况下直接写死
            }
            return 0;//如果成功返回0
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;//如果报异常就返回-1
        }
    }

}
