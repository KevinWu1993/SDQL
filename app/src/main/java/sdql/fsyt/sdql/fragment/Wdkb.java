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
    TextView todayWeather,todayTemp;
    ImageView wicon;
    String specialInfo, specialCookie, stuName;
    String value;
    String term;
    DefaultHttpClient client;
    private List<Course> c = new ArrayList<Course>();
    private RecyclerView recyclerView;
    private Context context;
    ImageButton btnMenu;//左上角菜单按钮
    int weeka = 100;
    GetTime gt;
    View view;
    LinearLayout ll;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wdkb, container, false);
        context = container.getContext();
        btnMenu = (ImageButton) view.findViewById(R.id.btn_menu);
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
        if (isFirst == true) {
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

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("__EVENTTARGET", ""));
                    params.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                    params.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUJNzIzMTk0NzYzD2QWAgIBD2QWCgIBDw8WAh4EVGV4dAUhMjAxNeW5tDEx5pyIMTTml6Ug5pif5pyf5YWtJm5ic3A7ZGQCBQ8PFgIfAAUY5b2T5YmN5L2N572u77ya6K++56iL6KGoZGQCBw8PFgIfAAUtICAg5qyi6L+O5oKo77yMKDEzMDgwOTUwNzgsU3R1ZGVudCkg5ZC05ZCv5LicZGQCCg9kFgQCAQ8PFgIeCEltYWdlVXJsBUMuLi9NeUNvbnRyb2wvQWxsX1Bob3RvU2hvdy5hc3B4P1VzZXJOdW09MTMwODA5NTA3OCZVc2VyVHlwZT1TdHVkZW50ZGQCAw8WAh8ABaMkPGRpdiBpZD0nbWVudVBhcmVudF8wJyBjbGFzcz0nbWVudVBhcmVudCcgb25jbGljaz0nbWVudUdyb3VwU3dpdGNoKDApOyc+5oiR55qE5L+h5oGvPC9kaXY+PGRpdiBpZD0nbWVudUdyb3VwMCcgY2xhc3M9J21lbnVHcm91cCc+PERpdiBjbGFzcz0nbWVudUl0ZW1PbicgdGl0bGU9J+ivvueoi+ihqCc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTExMSYmdWN0bD1NeUNvbnRyb2xceGZ6X2tjYi5hc2N4Jk15QWN0aW9uPVBlcnNvbmFsIiB0YXJnZXQ9J3BhcmVudCc+6K++56iL6KGoPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5Z+65pys5L+h5oGvJz48YSBocmVmPSIuLlxNeUNvbnRyb2xcU3R1ZGVudF9JbmZvckNoZWNrLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7ln7rmnKzkv6Hmga88L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfkv67mlLnlr4bnoIEnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0xMTAmJnVjdGw9TXlDb250cm9sXHBlcnNvbmFsX2NoYW5nZXB3ZC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5L+u5pS55a+G56CBPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5a2m57GN6aKE6K2mJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ3hmel9ieXNoLmFzY3gmQWN0aW9uPVBlcnNvbmFsJyk7IiB0YXJnZXQ9Jyc+5a2m57GN6aKE6K2mPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5paw55Sf5a+85biIJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MjE0JiZ1Y3RsPU15Q29udHJvbFxzdHVkZW50X215dGVhY2hlci5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5paw55Sf5a+85biIPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n6K++56iL5oiQ57upJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ3hmel9jai5hc2N4JkFjdGlvbj1QZXJzb25hbCcpOyIgdGFyZ2V0PScnPuivvueoi+aIkOe7qTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+aJi+acuuWPt+eggSc+PGEgaHJlZj0iLi5cTXlDb250cm9sXFBob25lLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7miYvmnLrlj7fnoIE8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflrrbplb/nmbvlvZUnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0yMDMmJnVjdGw9TXlDb250cm9sXEp6X3N0dWRlbnRzZXR0aW5nLmFzY3giIHRhcmdldD0ncGFyZW50Jz7lrrbplb/nmbvlvZU8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflj4zkuJPkuJrlj4zlrabkvY3or77nqIvlronmjpLooagnPjxhIGhyZWY9Ii4uXE15Q29udHJvbFxEZXp5X2tiLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7lj4zkuJPkuJrlj4zlrabkvY3or77nqIvlronmjpLooag8L2E+PC9kaXY+PC9kaXY+PGRpdiBpZD0nbWVudVBhcmVudF8xJyBjbGFzcz0nbWVudVBhcmVudCcgb25jbGljaz0nbWVudUdyb3VwU3dpdGNoKDEpOyc+5q2j5aSn5b6u6K++PC9kaXY+PGRpdiBpZD0nbWVudUdyb3VwMScgY2xhc3M9J21lbnVHcm91cCc+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfnlLPor7fogIPor5UnPjxhIGhyZWY9ImRlZmF1bHQuYXNweD8mY29kZT0yMDcmJnVjdGw9TXlDb250cm9sXFdLX1BhcGVyTGlzdC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+55Sz6K+36ICD6K+VPC9hPjwvZGl2PjwvZGl2PjxkaXYgaWQ9J21lbnVQYXJlbnRfMicgY2xhc3M9J21lbnVQYXJlbnQnIG9uY2xpY2s9J21lbnVHcm91cFN3aXRjaCgyKTsnPuWFrOWFseacjeWKoTwvZGl2PjxkaXYgaWQ9J21lbnVHcm91cDInIGNsYXNzPSdtZW51R3JvdXAnPjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5Z+55YW75pa55qGIJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTA0JiZ1Y3RsPU15Q29udHJvbFxhbGxfanhqaC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5Z+55YW75pa55qGIPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n6K++56iL5L+h5oGvJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTE2JiZ1Y3RsPU15Q29udHJvbFxhbGxfY291cnNlc2VhcmNoLmFzY3giIHRhcmdldD0ncGFyZW50Jz7or77nqIvkv6Hmga88L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSflvIDor77lronmjpInPjxhIGhyZWY9Ii4uXE15Q29udHJvbFxQdWJsaWNfS2thcC5hc3B4IiB0YXJnZXQ9J19ibGFuayc+5byA6K++5a6J5o6SPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5a2m55Sf5L+h5oGvJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTE5JiZ1Y3RsPU15Q29udHJvbFxhbGxfc2VhcmNoc3R1ZGVudC5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5a2m55Sf5L+h5oGvPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5pWZ5bel5L+h5oGvJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTIwJiZ1Y3RsPU15Q29udHJvbFxhbGxfdGVhY2hlci5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5pWZ5bel5L+h5oGvPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n55+t5L+h5bmz5Y+wJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTIyJiZ1Y3RsPU15Q29udHJvbFxtYWlsX2xpc3QuYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuefreS/oeW5s+WPsDwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+aVmeWupOaVmeWtpuWuieaOkic+PGEgaHJlZj0iLi5cTXlDb250cm9sXHB1YmxpY19jbGFzc3Jvb20uYXNweCIgdGFyZ2V0PSdfYmxhbmsnPuaVmeWupOaVmeWtpuWuieaOkjwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+WPjOWtpuS9jeivvueoi+aIkOe7qSc+PGEgaHJlZj0iamF2YXNjcmlwdDpPcGVuV2luZG93KCdkZXp5X2NqLmFzY3gmQWN0aW9uPVBlcnNvbmFsJyk7IiB0YXJnZXQ9Jyc+5Y+M5a2m5L2N6K++56iL5oiQ57upPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5q+V5Lia55Sf5Zu+5YOP6YeH6ZuG5L+h5oGv5qCh5a+5Jz48YSBocmVmPSIuLlxNeUNvbnRyb2xcVFhDSl9JbmZvckNoZWNrLmFzcHgiIHRhcmdldD0nX2JsYW5rJz7mr5XkuJrnlJ/lm77lg4/ph4fpm4bkv6Hmga/moKHlr7k8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmnJ/mnKvmiJDnu6nmn6Xor6InPjxhIGhyZWY9ImphdmFzY3JpcHQ6T3BlbldpbmRvdygneGZ6X1Rlc3RfY2ouYXNjeCcpOyIgdGFyZ2V0PScnPuacn+acq+aIkOe7qeafpeivojwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+acn+acq+aIkOe7qeafpeWIhueUs+ivtyc+PGEgaHJlZj0iamF2YXNjcmlwdDpPcGVuV2luZG93KCdDZnNxX1N0dWRlbnQuYXNjeCcpOyIgdGFyZ2V0PScnPuacn+acq+aIkOe7qeafpeWIhueUs+ivtzwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+ihpee8k+iAg+WuieaOkic+PGEgaHJlZj0iamF2YXNjcmlwdDpPcGVuV2luZG93KCd4ZnpfVGVzdF9CSEsuYXNjeCcpOyIgdGFyZ2V0PScnPuihpee8k+iAg+WuieaOkjwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+WtpuS5oOmXruetlCc+PGEgaHJlZj0iZGVmYXVsdC5hc3B4PyZjb2RlPTE1OSYmdWN0bD1NeUNvbnRyb2xcQWxsX1N0dWR5X0xpc3QuYXNjeCIgdGFyZ2V0PSdwYXJlbnQnPuWtpuS5oOmXruetlDwvYT48L2Rpdj48L2Rpdj48ZGl2IGlkPSdtZW51UGFyZW50XzMnIGNsYXNzPSdtZW51UGFyZW50JyBvbmNsaWNrPSdtZW51R3JvdXBTd2l0Y2goMyk7Jz7mlZnlrabkv6Hmga88L2Rpdj48ZGl2IGlkPSdtZW51R3JvdXAzJyBjbGFzcz0nbWVudUdyb3VwJz48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9J+e9keS4iuivhOaVmSc+PGEgaHJlZj0iamF2YXNjcmlwdDpPcGVuV2luZG93KCdwal9zdHVkZW50X2luZGV4LmFzY3gnKTsiIHRhcmdldD0nJz7nvZHkuIror4TmlZk8L2E+PC9kaXY+PERpdiBjbGFzcz0nbWVudUl0ZW0nIHRpdGxlPSfmlZnliqHmhI/op4HnrrEnPjxhIGhyZWY9Ii4uL0RlZmF1bHQuYXNweD9BY3Rpb249QWR2aXNlIiB0YXJnZXQ9J19ibGFuayc+5pWZ5Yqh5oSP6KeB566xPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n5pyf5pyr6ICD6K+V5a6J5o6SJz48YSBocmVmPSJkZWZhdWx0LmFzcHg/JmNvZGU9MTI5JiZ1Y3RsPU15Q29udHJvbFx4ZnpfdGVzdF9zY2hlZHVsZS5hc2N4IiB0YXJnZXQ9J3BhcmVudCc+5pyf5pyr6ICD6K+V5a6J5o6SPC9hPjwvZGl2PjxEaXYgY2xhc3M9J21lbnVJdGVtJyB0aXRsZT0n6L6F5L+u5Y+M5LiT5Lia5Y+M5a2m5L2N5oql5ZCNJz48YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5XaW5kb3coJ0RlenlfYm0uYXNjeCcpOyIgdGFyZ2V0PScnPui+heS/ruWPjOS4k+S4muWPjOWtpuS9jeaKpeWQjTwvYT48L2Rpdj48RGl2IGNsYXNzPSdtZW51SXRlbScgdGl0bGU9JzIwMTTnuqfmnKznp5HlrabnlJ/ovazkuJPkuJrmiqXlkI0nPjxhIGhyZWY9Ii4uXE15Q29udHJvbFx6enlfc3R1ZGVudF9zcS5hc3B4IiB0YXJnZXQ9J19ibGFuayc+MjAxNOe6p+acrOenkeWtpueUn+i9rOS4k+S4muaKpeWQjTwvYT48L2Rpdj48L2Rpdj5kAgwPZBYCZg9kFgwCAQ8PFgIfAAUe5rGf6KW/5biI6IyD5aSn5a2m5a2m55Sf6K++6KGoZGQCAw8PFgIfAAVn54+t57qn5ZCN56ew77yaPFU+MTPnuqfnianogZTnvZEy54+tPC9VPuOAgOOAgOWtpuWPt++8mjxVPjEzMDgwOTUwNzg8L3U+44CA44CA5aeT5ZCN77yaPHU+5ZC05ZCv5LicPC91PmRkAgUPEA8WBh4NRGF0YVRleHRGaWVsZAUM5a2m5pyf5ZCN56ewHg5EYXRhVmFsdWVGaWVsZAUM5byA5a2m5pel5pyfHgtfIURhdGFCb3VuZGdkEBUIDzE1LTE256ysMuWtpuacnw8xNS0xNuesrDHlrabmnJ8PMTQtMTXnrKwy5a2m5pyfDzE0LTE156ysMeWtpuacnw8xMy0xNOesrDLlrabmnJ8PMTMtMTTnrKwx5a2m5pyfDzEyLTEz56ysMuWtpuacnw8xMi0xM+esrDHlrabmnJ8VCBAyMDE2LzMvMSAwOjAwOjAwEDIwMTUvOS8xIDA6MDA6MDAQMjAxNS8zLzEgMDowMDowMBAyMDE0LzkvMSAwOjAwOjAwEDIwMTQvMy8xIDA6MDA6MDAQMjAxMy85LzEgMDowMDowMBAyMDEzLzMvMSAwOjAwOjAwEDIwMTIvOS8xIDA6MDA6MDAUKwMIZ2dnZ2dnZ2dkZAIJDw8WAh4HVmlzaWJsZWhkZAIKDzwrAAsBAA8WCB4IRGF0YUtleXMWAB4LXyFJdGVtQ291bnQC/////w8eFV8hRGF0YVNvdXJjZUl0ZW1Db3VudAL/////Dx4JUGFnZUNvdW50ZmRkAgsPPCsACwEADxYIHwYWAB8HAv////8PHwgC/////w8fCWZkZGQNFUutR1Pgx2qwqZJsZH2AqUV/0Ruk+5OOoJNXvbfpbA=="));
                    params.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWCwLKg8QGAoqG5b0BAv2D45ICAt6Dj4cJAv2D97IHAsi26eQPAu+20ZANAsi2/QQC77blsA4Chpn9sgEC5uGKM/LjAPhoFI4PKgkE+GHx+7WMoWSvxX2zg11LMF+sPypa"));
                    params.add(new BasicNameValuePair("_ctl1:ddlSterm",term));
                    params.add(new BasicNameValuePair("_ctl1:btnSearch", "确定"));
                    post.setEntity(new UrlEncodedFormEntity(params));
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