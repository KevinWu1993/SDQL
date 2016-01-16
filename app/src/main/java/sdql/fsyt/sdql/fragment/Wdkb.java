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
import org.apache.http.client.methods.HttpGet;
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

//        addData(weeka);
        SharedPreferences sp = context.getSharedPreferences("UserInfo", 0);
        boolean isFirst = sp.getBoolean("isFirst", true);
        if (isFirst ) {
            SharedPreferences sp2 =context.getSharedPreferences("TermInfo", 0);
            term=sp2.getString("Term","2015/9/1+0:00:00");
            client = new DefaultHttpClient();
            specialCookie = sp.getString("Cookie", "");
            specialInfo = sp.getString("Special", "");
            stuName = sp.getString("StuNum", "");
            readNet("http://jwc.jxnu.edu.cn/User/default.aspx?&code=111&&uctl=MyControl%5cxfz_kcb.ascx&MyAction=Personal");
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
                System.out.println("访问的URL为："+urlString);
                HttpGet get = new HttpGet(urlString);
                get.setHeader("Cookie", "_ga=GA1.3.609810117.1451115712;ASP.NET_SessionId=" + specialCookie + ";" +
                        "JwOAUserSettingNew=" + specialInfo);
                System.out.println("_ga=GA1.3.609810117.1451115712;ASP.NET_SessionId=" + specialCookie + ";" +
                        "JwOAUserSettingNew=" + specialInfo);
                try {

                    List<NameValuePair> params = new ArrayList<>();
//                    params.add(new BasicNameValuePair("__EVENTTARGET", ""));
//                    params.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
//                    params.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUJNzIzMTk0NzYzD2QWAgIBD2QWCgIBDw8WAh4EVGV4dAUgMjAxNuW5tDHmnIgxNuaXpSDmmJ/mnJ/lha0mbmJzcDtkZAIFDw8WAh8ABRjlvZPliY3kvY3nva7vvJror77nqIvooahkZAIHDw8WAh8ABS0gICDmrKLov47mgqjvvIwoMTMwODA5NTA3OCxTdHVkZW50KSDlkLTlkK/kuJxkZAIKD2QWBAIBDw8WAh4ISW1hZ2VVcmwFQy4uL015Q29udHJvbC9BbGxfUGhvdG9TaG93LmFzcHg/VXNlck51bT0xMzA4MDk1MDc4JlVzZXJUeXBlPVN0dWRlbnRkZAIDDxYCHwAFiCI8ZGl2IGlkPSdtZW51UGFyZW50XzAnIGNsYXNzPSdtZW51UGFyZW50JyBvbmNsaWNrPSdtZW51R3JvdXBTd2l0Y2goMCk7Jz7miJHnmoTkv6Hmga88L2Rpdj48ZGl2IGlkPSdtZW51R3JvdXAwJyBjbGFzcz0nbWVudUdyb3VwJz48RGl2IGNsYXNzPSdtZW51SXRlbU9uJyB0aXRsZT0n6K++56iL6KGoJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTExJiZ1Y3RsPU15Q29udHJvbFx4Znpfa2NiLmFzY3gmTXlBY3Rpb249UGVyc29uYWwiIHRhcmdldD0ncGFyZW50Jz7or77nqIvooag8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfln7rmnKzkv6Hmga8nPjxhIGhyZWY9Ii4uXE15Q29udHJvbFxTdHVkZW50X0luZm9yQ2hlY2suYXNweCIgdGFyZ2V0PSdfYmxhbmsnPuWfuuacrOS/oeaBrzwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+S/ruaUueWvhueggSc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTExMCYmdWN0bD1NeUNvbnRyb2xccGVyc29uYWxfY2hhbmdlcHdkLmFzY3giIHRhcmdldD0ncGFyZW50Jz7kv67mlLnlr4bnoIE8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflrabnsY3pooToraYnPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygneGZ6X2J5c2guYXNjeCZBY3Rpb249UGVyc29uYWwnKTsiIHRhcmdldD0nJz7lrabnsY3pooToraY8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmlrDnlJ/lr7zluIgnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0yMTQmJnVjdGw9TXlDb250cm9sXHN0dWRlbnRfbXl0ZWFjaGVyLmFzY3giIHRhcmdldD0ncGFyZW50Jz7mlrDnlJ/lr7zluIg8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfor77nqIvmiJDnu6knPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygneGZ6X2NqLmFzY3gmQWN0aW9uPVBlcnNvbmFsJyk7IiB0YXJnZXQ9Jyc+6K++56iL5oiQ57upPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5omL5py65Y+356CBJz48YSBocmVmPSIuLlxNeUNvbnRyb2xcUGhvbmUuYXNweCIgdGFyZ2V0PSdfYmxhbmsnPuaJi+acuuWPt+eggTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+WutumVv+eZu+W9lSc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTIwMyYmdWN0bD1NeUNvbnRyb2xcSnpfc3R1ZGVudHNldHRpbmcuYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuWutumVv+eZu+W9lTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+WPjOS4k+S4muWPjOWtpuS9jeivvueoi+WuieaOkuihqCc+PGEgaHJlZj0iLi5cTXlDb250cm9sXERlenlfa2IuYXNweCIgdGFyZ2V0PSdfYmxhbmsnPuWPjOS4k+S4muWPjOWtpuS9jeivvueoi+WuieaOkuihqDwvYT48L2Rpdj48L2Rpdj48ZGl2IGlkPSdtZW51UGFyZW50XzEnIGNsYXNzPSdtZW51UGFyZW50JyBvbmNsaWNrPSdtZW51R3JvdXBTd2l0Y2goMSk7Jz7lhazlhbHmnI3liqE8L2Rpdj48ZGl2IGlkPSdtZW51R3JvdXAxJyBjbGFzcz0nbWVudUdyb3VwJz48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+WfueWFu+aWueahiCc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTEwNCYmdWN0bD1NeUNvbnRyb2xcYWxsX2p4amguYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuWfueWFu+aWueahiDwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+ivvueoi+S/oeaBryc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTExNiYmdWN0bD1NeUNvbnRyb2xcYWxsX2NvdXJzZXNlYXJjaC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+6K++56iL5L+h5oGvPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5byA6K++5a6J5o6SJz48YSBocmVmPSIuLlxNeUNvbnRyb2xcUHVibGljX0trYXAuYXNweCIgdGFyZ2V0PSdfYmxhbmsnPuW8gOivvuWuieaOkjwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+WtpueUn+S/oeaBryc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTExOSYmdWN0bD1NeUNvbnRyb2xcYWxsX3NlYXJjaHN0dWRlbnQuYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuWtpueUn+S/oeaBrzwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+aVmeW3peS/oeaBryc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTEyMCYmdWN0bD1NeUNvbnRyb2xcYWxsX3RlYWNoZXIuYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuaVmeW3peS/oeaBrzwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+efreS/oeW5s+WPsCc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTEyMiYmdWN0bD1NeUNvbnRyb2xcbWFpbF9saXN0LmFzY3giIHRhcmdldD0ncGFyZW50Jz7nn63kv6HlubPlj7A8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmlZnlrqTmlZnlrablronmjpInPjxhIGhyZWY9Ii4uXE15Q29udHJvbFxwdWJsaWNfY2xhc3Nyb29tLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7mlZnlrqTmlZnlrablronmjpI8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflj4zlrabkvY3or77nqIvmiJDnu6knPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygnZGV6eV9jai5hc2N4JkFjdGlvbj1QZXJzb25hbCcpOyIgdGFyZ2V0PScnPuWPjOWtpuS9jeivvueoi+aIkOe7qTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+avleS4mueUn+WbvuWDj+mHh+mbhuS/oeaBr+agoeWvuSc+PGEgaHJlZj0iLi5cTXlDb250cm9sXFRYQ0pfSW5mb3JDaGVjay5hc3B4IiB0YXJnZXQ9J19ibGFuayc+5q+V5Lia55Sf5Zu+5YOP6YeH6ZuG5L+h5oGv5qCh5a+5PC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5pyf5pyr5oiQ57up5p+l6K+iJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ3hmel9UZXN0X2NqLmFzY3gnKTsiIHRhcmdldD0nJz7mnJ/mnKvmiJDnu6nmn6Xor6I8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmnJ/mnKvmiJDnu6nmn6XliIbnlLPor7cnPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygnQ2ZzcV9TdHVkZW50LmFzY3gnKTsiIHRhcmdldD0nJz7mnJ/mnKvmiJDnu6nmn6XliIbnlLPor7c8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfooaXnvJPogIPlronmjpInPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygneGZ6X1Rlc3RfQkhLLmFzY3gnKTsiIHRhcmdldD0nJz7ooaXnvJPogIPlronmjpI8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflrabkuaDpl67nrZQnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xNTkmJnVjdGw9TXlDb250cm9sXEFsbF9TdHVkeV9MaXN0LmFzY3giIHRhcmdldD0ncGFyZW50Jz7lrabkuaDpl67nrZQ8L2E+PC9kaXY+PC9kaXY+PGRpdiBpZD0nbWVudVBhcmVudF8yJyBjbGFzcz0nbWVudVBhcmVudCcgb25jbGljaz0nbWVudUdyb3VwU3dpdGNoKDIpOyc+5pWZ5a2m5L+h5oGvPC9kaXY+PGRpdiBpZD0nbWVudUdyb3VwMicgY2xhc3M9J21lbnVHcm91cCc+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfnvZHkuIror4TmlZknPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygncGpfc3R1ZGVudF9pbmRleC5hc2N4Jyk7IiB0YXJnZXQ9Jyc+572R5LiK6K+E5pWZPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5pWZ5Yqh5oSP6KeB566xJz48YSBocmVmPSIuLi9EZWZhdWx0LmFzcHg/QWN0aW9uPUFkdmlzZSIgdGFyZ2V0PSdfYmxhbmsnPuaVmeWKoeaEj+ingeeusTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+acn+acq+iAg+ivleWuieaOkic+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTEyOSYmdWN0bD1NeUNvbnRyb2xceGZ6X3Rlc3Rfc2NoZWR1bGUuYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuacn+acq+iAg+ivleWuieaOkjwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+i+heS/ruWPjOS4k+S4muWPjOWtpuS9jeaKpeWQjSc+PGEgaHJlZj0iamF2YXNjcmlwdDpPcGVuV2luZG93KCdEZXp5X2JtLmFzY3gnKTsiIHRhcmdldD0nJz7ovoXkv67lj4zkuJPkuJrlj4zlrabkvY3miqXlkI08L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPScyMDE057qn5pys56eR5a2m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"));
//                    params.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWCwLt6PTMDAKKhuW9AQL9g+OSAgLeg4+HCQL9g/eyBwLItunkDwLvttGQDQLItv0EAu+25bAOAoaZ/bIBAubhijNJx8IKo657Mi9owqHEtKUMj5eHYObCCfnEc4rmCsFzLQ=="));
//                    params.add(new BasicNameValuePair("_ctl1:ddlSterm",term));
//                    params.add(new BasicNameValuePair("_ctl1:btnSearch", "确定"));
//                    post.setEntity(new UrlEncodedFormEntity(params));
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        value = EntityUtils.toString(response.getEntity());//取得返回的内容
                        System.out.println("成功返回内容"+value);
                    }else{
                        System.out.println("返回的状态码为：" + response.getStatusLine().getStatusCode() );
                    }
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
                    System.out.println("解析出来的数据长度为："+a.length);
                    System.out.println("解析出来的数据的头部为："+a[0]);
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
                        addData(weeka);
                    }
                    else{
                        System.out.println("解析出来的网页数据为：\n"+result);
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