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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.angmarch.views.NiceSpinner;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sdql.fsyt.sdql.MainActivity;
import sdql.fsyt.sdql.R;
import sdql.fsyt.sdql.database.DBHelper;
import sdql.fsyt.sdql.utils.Course;
import sdql.fsyt.sdql.utils.CourseListAdapter;
import sdql.fsyt.sdql.utils.GetTime;


/**
 * Created by KevinWu on 2015/10/17.
 */

public class Wdkb extends Fragment {
    DBHelper mDBHelper = null;//声明DBHelper类
    SQLiteDatabase mSQLiteDatabase = null;
    String specialInfo, specialCookie, stuName;
    String value;
    String term;
    DefaultHttpClient client;
    private List<Course> c = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    int weeka = 100;
    GetTime gt;
    View view;
    LinearLayout ll;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wdkb, container, false);
        context = container.getContext();
        NiceSpinner niceSpinner = (NiceSpinner) view.findViewById(R.id.nice_spinner);
        ArrayList<String> dataset = new ArrayList<>(Arrays.asList("星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"));
        gt = new GetTime();
        gt.set();
        weeka=gt.getDayOfWeek();
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setSelectedIndex(gt.getDayOfWeek());//选中当前日期
        ll = (LinearLayout) view.findViewById(R.id.kb);


        SharedPreferences sp = context.getSharedPreferences("UserInfo", 0);
        boolean isFirst = sp.getBoolean("isFirst", true);
        if (isFirst ) {
            SharedPreferences sp2 =context.getSharedPreferences("TermInfo", 0);
            term=sp2.getString("Term","2015/9/1+0:00:00");
            client = new DefaultHttpClient();
            specialCookie = sp.getString("Cookie", "");
            specialInfo = sp.getString("Special", "");
            stuName = sp.getString("StuNum", "");
            readNet("http://jwc.jxnu.edu.cn/User/default.aspx?&&code=111&uctl=MyControl%5cxfz_kcb.ascx&MyAction=Personal");
        }else
            addData(weeka);

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    public void readNet(String url) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... arg0) {
                String urlString = arg0[0];
                HttpPost post = new HttpPost(urlString);
                post.setHeader("Cookie", "ASP.NET_SessionId=" + specialCookie + ";" +
                        "JwOAUserSettingNew=" + specialInfo);
                try {

                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("__EVENTTARGET", ""));
                    params.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                    params.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUJNzIzMTk0NzYzD2QWAgIBD2QWCgIBDw8WAh4EVGV4dAUhMjAxNeW5tDEy5pyIMzDml6Ug5pif5pyf5LiJJm5ic3A7ZGQCBQ8PFgIfAAUY5b2T5YmN5L2N572u77ya6K++56iL6KGoZGQCBw8PFgIfAAUtICAg5qyi6L+O5oKo77yMKDEzMDgwOTUwNzgsU3R1ZGVudCkg5ZC05ZCv5LicZGQCCg9kFgQCAQ8PFgIeCEltYWdlVXJsBUMuLi9NeUNvbnRyb2wvQWxsX1Bob3RvU2hvdy5hc3B4P1VzZXJOdW09MTMwODA5NTA3OCZVc2VyVHlwZT1TdHVkZW50ZGQCAw8WAh8ABYgiPGRpdiBpZD0nbWVudVBhcmVudF8wJyBjbGFzcz0nbWVudVBhcmVudCcgb25jbGljaz0nbWVudUdyb3VwU3dpdGNoKDApOyc+5oiR55qE5L+h5oGvPC9kaXY+PGRpdiBpZD0nbWVudUdyb3VwMCcgY2xhc3M9J21lbnVHcm91cCc+PERpdiBjbGFzcz0nbWVudUl0ZW1PbicgdGl0bGU9J+ivvueoi+ihqCc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTExMSYmdWN0bD1NeUNvbnRyb2xceGZ6X2tjYi5hc2N4Jk15QWN0aW9uPVBlcnNvbmFsIiB0YXJnZXQ9J3BhcmVudCc+6K++56iL6KGoPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5Z+65pys5L+h5oGvJz48YSBocmVmPSIuLlxNeUNvbnRyb2xcU3R1ZGVudF9JbmZvckNoZWNrLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7ln7rmnKzkv6Hmga88L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfkv67mlLnlr4bnoIEnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMTAmJnVjdGw9TXlDb250cm9sXHBlcnNvbmFsX2NoYW5nZXB3ZC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5L+u5pS55a+G56CBPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5a2m57GN6aKE6K2mJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ3hmel9ieXNoLmFzY3gmQWN0aW9uPVBlcnNvbmFsJyk7IiB0YXJnZXQ9Jyc+5a2m57GN6aKE6K2mPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5paw55Sf5a+85biIJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MjE0JiZ1Y3RsPU15Q29udHJvbFxzdHVkZW50X215dGVhY2hlci5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5paw55Sf5a+85biIPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n6K++56iL5oiQ57upJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ3hmel9jai5hc2N4JkFjdGlvbj1QZXJzb25hbCcpOyIgdGFyZ2V0PScnPuivvueoi+aIkOe7qTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+aJi+acuuWPt+eggSc+PGEgaHJlZj0iLi5cTXlDb250cm9sXFBob25lLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7miYvmnLrlj7fnoIE8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflrrbplb/nmbvlvZUnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0yMDMmJnVjdGw9TXlDb250cm9sXEp6X3N0dWRlbnRzZXR0aW5nLmFzY3giIHRhcmdldD0ncGFyZW50Jz7lrrbplb/nmbvlvZU8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflj4zkuJPkuJrlj4zlrabkvY3or77nqIvlronmjpLooagnPjxhIGhyZWY9Ii4uXE15Q29udHJvbFxEZXp5X2tiLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7lj4zkuJPkuJrlj4zlrabkvY3or77nqIvlronmjpLooag8L2E+PC9kaXY+PC9kaXY+PGRpdiBpZD0nbWVudVBhcmVudF8xJyBjbGFzcz0nbWVudVBhcmVudCcgb25jbGljaz0nbWVudUdyb3VwU3dpdGNoKDEpOyc+5YWs5YWx5pyN5YqhPC9kaXY+PGRpdiBpZD0nbWVudUdyb3VwMScgY2xhc3M9J21lbnVHcm91cCc+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfln7nlhbvmlrnmoYgnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMDQmJnVjdGw9TXlDb250cm9sXGFsbF9qeGpoLmFzY3giIHRhcmdldD0ncGFyZW50Jz7ln7nlhbvmlrnmoYg8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfor77nqIvkv6Hmga8nPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMTYmJnVjdGw9TXlDb250cm9sXGFsbF9jb3Vyc2VzZWFyY2guYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuivvueoi+S/oeaBrzwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+W8gOivvuWuieaOkic+PGEgaHJlZj0iLi5cTXlDb250cm9sXFB1YmxpY19La2FwLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7lvIDor77lronmjpI8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflrabnlJ/kv6Hmga8nPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMTkmJnVjdGw9TXlDb250cm9sXGFsbF9zZWFyY2hzdHVkZW50LmFzY3giIHRhcmdldD0ncGFyZW50Jz7lrabnlJ/kv6Hmga88L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmlZnlt6Xkv6Hmga8nPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMjAmJnVjdGw9TXlDb250cm9sXGFsbF90ZWFjaGVyLmFzY3giIHRhcmdldD0ncGFyZW50Jz7mlZnlt6Xkv6Hmga88L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfnn63kv6HlubPlj7AnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMjImJnVjdGw9TXlDb250cm9sXG1haWxfbGlzdC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+55+t5L+h5bmz5Y+wPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5pWZ5a6k5pWZ5a2m5a6J5o6SJz48YSBocmVmPSIuLlxNeUNvbnRyb2xccHVibGljX2NsYXNzcm9vbS5hc3B4IiB0YXJnZXQ9J19ibGFuayc+5pWZ5a6k5pWZ5a2m5a6J5o6SPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5Y+M5a2m5L2N6K++56iL5oiQ57upJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ2RlenlfY2ouYXNjeCZBY3Rpb249UGVyc29uYWwnKTsiIHRhcmdldD0nJz7lj4zlrabkvY3or77nqIvmiJDnu6k8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmr5XkuJrnlJ/lm77lg4/ph4fpm4bkv6Hmga/moKHlr7knPjxhIGhyZWY9Ii4uXE15Q29udHJvbFxUWENKX0luZm9yQ2hlY2suYXNweCIgdGFyZ2V0PSdfYmxhbmsnPuavleS4mueUn+WbvuWDj+mHh+mbhuS/oeaBr+agoeWvuTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+acn+acq+aIkOe7qeafpeivoic+PGEgaHJlZj0iamF2YXNjcmlwdDpPcGVuV2luZG93KCd4ZnpfVGVzdF9jai5hc2N4Jyk7IiB0YXJnZXQ9Jyc+5pyf5pyr5oiQ57up5p+l6K+iPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5pyf5pyr5oiQ57up5p+l5YiG55Sz6K+3Jz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ0Nmc3FfU3R1ZGVudC5hc2N4Jyk7IiB0YXJnZXQ9Jyc+5pyf5pyr5oiQ57up5p+l5YiG55Sz6K+3PC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n6KGl57yT6ICD5a6J5o6SJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ3hmel9UZXN0X0JISy5hc2N4Jyk7IiB0YXJnZXQ9Jyc+6KGl57yT6ICD5a6J5o6SPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5a2m5Lmg6Zeu562UJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTU5JiZ1Y3RsPU15Q29udHJvbFxBbGxfU3R1ZHlfTGlzdC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5a2m5Lmg6Zeu562UPC9hPjwvZGl2PjwvZGl2PjxkaXYgaWQ9J21lbnVQYXJlbnRfMicgY2xhc3M9J21lbnVQYXJlbnQnIG9uY2xpY2s9J21lbnVHcm91cFN3aXRjaCgyKTsnPuaVmeWtpuS/oeaBrzwvZGl2PjxkaXYgaWQ9J21lbnVHcm91cDInIGNsYXNzPSdtZW51R3JvdXAnPjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n572R5LiK6K+E5pWZJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ3BqX3N0dWRlbnRfaW5kZXguYXNjeCcpOyIgdGFyZ2V0PScnPue9keS4iuivhOaVmTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+aVmeWKoeaEj+ingeeusSc+PGEgaHJlZj0iLi4vRGVmYXVsdC5hc3B4P0FjdGlvbj1BZHZpc2UiIHRhcmdldD0nX2JsYW5rJz7mlZnliqHmhI/op4HnrrE8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmnJ/mnKvogIPor5XlronmjpInPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMjkmJnVjdGw9TXlDb250cm9sXHhmel90ZXN0X3NjaGVkdWxlLmFzY3giIHRhcmdldD0ncGFyZW50Jz7mnJ/mnKvogIPor5XlronmjpI8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfovoXkv67lj4zkuJPkuJrlj4zlrabkvY3miqXlkI0nPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygnRGV6eV9ibS5hc2N4Jyk7IiB0YXJnZXQ9Jyc+6L6F5L+u5Y+M5LiT5Lia5Y+M5a2m5L2N5oql5ZCNPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0nMjAxNOe6p+acrOenkeWtpueUn+i9rOS4k+S4muaKpeWQjSc+PGEgaHJlZj0iLi5cTXlDb250cm9sXHp6eV9zdHVkZW50X3NxLmFzcHgiIHRhcmdldD0nX2JsYW5rJz4yMDE057qn5pys56eR5a2m55Sf6L2s5LiT5Lia5oql5ZCNPC9hPjwvZGl2PjwvZGl2PmQCDA9kFgJmD2QWDAIBDw8WAh8ABR7msZ/opb/luIjojIPlpKflrablrabnlJ/or77ooahkZAIDDw8WAh8ABWfnj63nuqflkI3np7DvvJo8VT4xM+e6p+eJqeiBlOe9kTLnj608L1U+44CA44CA5a2m5Y+377yaPFU+MTMwODA5NTA3ODwvdT7jgIDjgIDlp5PlkI3vvJo8dT7lkLTlkK/kuJw8L3U+ZGQCBQ8QDxYGHg1EYXRhVGV4dEZpZWxkBQzlrabmnJ/lkI3np7AeDkRhdGFWYWx1ZUZpZWxkBQzlvIDlrabml6XmnJ8eC18hRGF0YUJvdW5kZ2QQFQgPMTUtMTbnrKwy5a2m5pyfDzE1LTE256ysMeWtpuacnw8xNC0xNeesrDLlrabmnJ8PMTQtMTXnrKwx5a2m5pyfDzEzLTE056ysMuWtpuacnw8xMy0xNOesrDHlrabmnJ8PMTItMTPnrKwy5a2m5pyfDzEyLTEz56ysMeWtpuacnxUIEDIwMTYvMy8xIDA6MDA6MDAQMjAxNS85LzEgMDowMDowMBAyMDE1LzMvMSAwOjAwOjAwEDIwMTQvOS8xIDA6MDA6MDAQMjAxNC8zLzEgMDowMDowMBAyMDEzLzkvMSAwOjAwOjAwEDIwMTMvMy8xIDA6MDA6MDAQMjAxMi85LzEgMDowMDowMBQrAwhnZ2dnZ2dnZ2RkAgkPDxYCHgdWaXNpYmxlaGRkAgoPPCsACwEADxYIHghEYXRhS2V5cxYAHgtfIUl0ZW1Db3VudAL/////Dx4VXyFEYXRhU291cmNlSXRlbUNvdW50Av////8PHglQYWdlQ291bnRmZGQCCw88KwALAQAPFggfBhYAHwcC/////w8fCAL/////Dx8JZmRkZCWPWDW7haR3T8v0xU8qJMsbZaYhbv5Cp4vFJQEfCz+q"));
                    params.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWCwKawe6LBQKKhuW9AQL9g+OSAgLeg4+HCQL9g/eyBwLItunkDwLvttGQDQLItv0EAu+25bAOAoaZ/bIBAubhijNaX+epYcu/tWVjRlecuuYSkZV/ebZBr521n10tYIO+kg=="));
                    params.add(new BasicNameValuePair("_ctl1:ddlSterm",term));
                    //params.add(new BasicNameValuePair("_ctl1:btnSearch", "确定"));
                    post.setEntity(new UrlEncodedFormEntity(params));

                    Log.d("请求头的长度为：",""+post.getAllHeaders().length);
                } catch (UnsupportedEncodingException e1) {

                }
                try {
                    HttpResponse response = client.execute(post);
                    value = EntityUtils.toString(response.getEntity());
                    //System.out.println(value);
                    return value;
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    mDBHelper = new DBHelper(context);
                    mSQLiteDatabase = mDBHelper.getWritableDatabase();
                    ContentValues value[][] = new ContentValues[7][9];//第一维是周几，第二维当天的第几节课
                    for (int iii = 0; iii < value.length; iii++)
                        for (int jjj = 0; jjj < value[iii].length; jjj++)
                            value[iii][jjj] = new ContentValues();
                    value[0][0].put("Week", "1");
                    value[1][0].put("Week", "2");
                    value[2][0].put("Week", "3");
                    value[3][0].put("Week", "4");
                    value[4][0].put("Week", "5");
                    value[5][0].put("Week", "6");
                    value[6][0].put("Week", "7");
                    for (int i2 = 0; i2 <= 6; i2++)
                        mSQLiteDatabase.insert("CourseTable", null, value[i2][0]);
                    value[0][1].put("StuNum", stuName);
                    value[1][1].put("StuNum", stuName);
                    value[2][1].put("StuNum", stuName);
                    value[3][1].put("StuNum", stuName);
                    value[4][1].put("StuNum", stuName);
                    value[5][1].put("StuNum", stuName);
                    value[6][1].put("StuNum", stuName);

                    System.out.println(result);

                    String a[];
                    a = result.split("星期日");
                    String score[];
                    int i = 0, j = 0;
                    if(a.length>1) {
                        String html = a[1];//这部分是解析课程表的内容，包括了后面的课程信息
                        String htmlInfo[] = a[1].split("课表说明：底色为深色部分表示的是有冲突的课程！");
                        Document doc = Jsoup.parse(html);   //把HTML代码加载到doc中，这部分是课程表
                        Document docInfo = Jsoup.parse(htmlInfo[1]);//这部分是课程信息
                        System.out.println(htmlInfo[1]);
                        Elements classInfo = docInfo.select("font[color=#330099]");
                        System.out.println(classInfo.toString());
                        int count = 0;
                        String keyValue[] = new String[]{
                                "CourseID", "CourseName", "CourseWide", "CourseTeacher", "CourseNameListLink", "CourseForumLink"
                        };
                        ContentValues cv = new ContentValues();
                        for (Element link_classInfo : classInfo) {
                            //System.out.println(link_classInfo.toString());
                            //System.out.println("每次循环"+link_classInfo.text());
                            if ((count + 2) % 6 == 0 && count != 0) {
                                //System.out.println("转化前"+link_classInfo.toString());
                                //System.out.println(link_classInfo.text());
                                String tempa[] = link_classInfo.toString().split("OpenWindow\\('");
                                //System.out.println(tempa[0]);
                                // System.out.println(link_classInfo.toString());
                                String tempb[] = tempa[1].split("'\\);");
                                cv.put(keyValue[4], tempb[0]);
                            } else if ((count + 1) % 6 == 0 && count != 0) {
                                // System.out.println("hou转化前"+link_classInfo.toString());
                                String tempa[] = link_classInfo.toString().split("href=\"");
                                String tempb[] = tempa[1].split("\">课程讨论区");
                                cv.put(keyValue[5], tempb[0]);
                                mSQLiteDatabase.insert("CourseInfo", null, cv);
                                cv = new ContentValues();
                            } else {
                                cv.put(keyValue[count % 6], link_classInfo.text());
                            }
                            count++;
                            //if(count>3)break;
                        }

                        Elements myClass = doc.select("DIV[align=center]");
                        score = new String[myClass.size()];
                        int pos = 1;
                        String dayValue[] = new String[]{
                                "OneTwo", "Three", "Four", "Five", "SixSeven", "EightNine", "Night"
                        };
                        for (Element link_class : myClass) {
                            //如果当前解析到的数据无关，直接跳入下一次循环
                            if (link_class.text().equals("1 2")
                                    || link_class.text().equals("3")
                                    || link_class.text().equals("3")
                                    || link_class.text().equals("4")
                                    || link_class.text().equals("5")
                                    || link_class.text().equals("6 7")
                                    || link_class.text().equals("8 9")
                                    || link_class.text().equals("中 午")
                                    || link_class.text().equals("下午")
                                    || link_class.text().equals("晚上")
                                    || link_class.text().equals("晚 上")) continue;
                            score[i] = link_class.text();
                            //System.out.println(score[i]);
                            String temp[] = score[i].split("\\(");//面向对象程序设计
                            if (temp.length > 1) {
                                String temp2[] = temp[1].split("\\)");//W7202 ) 13级物联网2班
                                value[pos - 1][(j / 7) + 2].put(dayValue[i / 7], temp[0] + "@" + temp2[0]);
                            }
                            pos++;
                            if (pos == 8) {
                                pos = 1;
                            }
                            // if(i==7)break;
                            i++;
                            j++;

                        }
                        for (int ii = 0; ii < value.length; ii++) {
                            int aa = ii + 1;
                            for (int jj = 1; jj < value[ii].length; jj++) {
                                if (value[ii][jj].size() > 0) {
                                    System.out.println("执行" + aa);
                                    mSQLiteDatabase.update("CourseTable", value[ii][jj], "Week=?", new String[]{"" + aa});
                                }
                            }

                        }
                        SharedPreferences sp = context.getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putBoolean("isFirst", false);
                        ed.commit();
                        // addData(weeka);
                    }
                    else{
                        Log.d("网页返回值中解析失败，返回值的长度为",""+result.length());
                    }

                }
            }
        }.execute(url);
    }

    private void addData(int week) {
        c.clear();//先清空表
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        int weekday = week;
        if (weekday == 0) weekday = 7;
        Cursor cursor = mDBHelper.getSpecial("CourseTable", "Week", "" + weekday);
        String dayValue[] = new String[]{
                "OneTwo", "Three", "Four", "Five", "SixSeven", "EightNine", "Night"
        };

        for (int i = 0; i < dayValue.length; i++) {
            String temp = cursor.getString(cursor
                    .getColumnIndex(dayValue[i]));//取数据库对应字段
            if (temp != null) {
                String time = "08:00";
                String courseName[] = temp.split("@");
                System.out.println(courseName[0]);
                Cursor cursor2 = mDBHelper.getSpecial("CourseInfo", "CourseName", courseName[0].trim());
                String teacher = cursor2.getString(cursor2
                        .getColumnIndex("CourseTeacher"));//教师
                switch (dayValue[i]) {
                    case "OneTwo":
                        time = "08:00";
                        break;
                    case "Three":
                        time = "09:40";
                        break;
                    case "Four":
                        time = "10:30";
                        break;
                    case "Five":
                        time = "11:20";
                        break;
                    case "SixSeven":
                        time = "14:00";
                        break;
                    case "EightNine":
                        time = "15:40";
                        break;
                    case "Night":
                        time = "19:00";
                        break;
                }

                c.add(new Course(time, courseName[0], courseName[1], teacher));
            }
                // 拿到RecyclerView
                recyclerView = (RecyclerView) view.findViewById(R.id.base_class_list);
                //final MainActivity ma = (MainActivity) getActivity();
                // 设置LinearLayoutManager
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                // 设置ItemAnimator
                //recyclerView.setItemAnimator(new DefaultItemAnimator());
                // 设置固定大小
                recyclerView.setHasFixedSize(true);
                // 初始化自定义的适配器
                CourseListAdapter myAdapter = new CourseListAdapter(context, c);
                // 为mRecyclerView设置适配器
                recyclerView.setAdapter(myAdapter);
        }
    }
}