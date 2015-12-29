package sdql.fsyt.sdql;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import sdql.fsyt.sdql.utils.BaseActivity;
import sdql.fsyt.sdql.utils.GetTime;

public class Login extends BaseActivity {
    DefaultHttpClient client;//网络连接
    private int mMorphCounter1 = 1;
    List<Cookie> cookies;//用来保存cookie
    int loginSign = -1;//用于判断登录成功与否的标志，默认状态为-1
    TextInputLayout usernameWrapper, passwordWrapper;
    String c1, c2;
    String content;//保存返回的网页的具体内容，用来后面解析得到学生的姓名用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先检查有没有登录了，如果已经登录了就直接进入主界面
        SharedPreferences sp = getSharedPreferences("UserInfo", 0);
        if (sp.getString("LoginSucceed", "").equals("Yes")) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            Login.this.finish();
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.login);
        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
        RelativeLayout rlh = (RelativeLayout) findViewById(R.id.titleimg);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) rlh
                .getLayoutParams();
        System.out.println(height);
        linearParams.height = (int) (height * 0.31);

        client = new DefaultHttpClient();//实例化连接对象


        //#fbf5c4
        LinearLayout ll = (LinearLayout) findViewById(R.id.loginback);
        //BreathingViewHelper.setBreathingBackgroundColor(btnMorph1, Color.parseColor("#4400BCD4"));
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        usernameWrapper.setHint("学号");
        passwordWrapper.setHint("教务在线密码");
        final IndeterminateProgressButton btnMorph1 = (IndeterminateProgressButton) findViewById(R.id.btnMorph1);

        //设置呼吸特效
        //BreathingViewHelper.setBreathingBackgroundColor(btnMorph1, Color.parseColor("#4400BCD4"));
        btnMorph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginSign = 4;//重置登录状态
                if ((!usernameWrapper.getEditText().getText().toString().equals("")) || (!passwordWrapper.getEditText().getText().toString().equals(""))) {
                    onMorphButton1Clicked(btnMorph1);
                } else {
                    loginSign = -1;
                    aleart();
                }

            }
        });
        morphToSquare(btnMorph1, 0);
    }

    private void onMorphButton1Clicked(final IndeterminateProgressButton btnMorph) {
        if (mMorphCounter1 == 0) {
            mMorphCounter1++;
            morphToSquare(btnMorph, 600);
        } else if (mMorphCounter1 == 1) {
            mMorphCounter1 = 0;
            simulateProgress1(btnMorph);
        }
    }


    private void morphToSquare(final IndeterminateProgressButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(0)//dimen(R.dimen.mb_corner_radius_4)
                .width(dimen(R.dimen.mb_width_280))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.lobtc))
                .colorPressed(color(R.color.mb_blue_dark))
                .text("登  录");
        btnMorph.morph(square);
    }

    private void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(600)
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }

    private void morphToFailure(final IndeterminateProgressButton btnMorph, int duration) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_red))
                .colorPressed(color(R.color.mb_red_dark))
                .icon(R.drawable.ic_fail);
        btnMorph.morph(circle);
    }

    private void simulateProgress1(@NonNull final IndeterminateProgressButton button) {
        int progressColor1 = color(R.color.holo_blue_bright);
        int progressColor2 = color(R.color.holo_green_light);
        int progressColor3 = color(R.color.holo_orange_light);
        int progressColor4 = color(R.color.holo_red_light);
        int color = color(R.color.mb_gray);
        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
        int width = dimen(R.dimen.mb_width_200);
        int height = dimen(R.dimen.mb_height_8);
        int duration = 600;
        readNet("http://jwc.jxnu.edu.cn/Default_Login.aspx?preurl=", usernameWrapper.getEditText().getText().toString(), passwordWrapper.getEditText().getText().toString());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginSign == 3) {
                    morphToSuccess(button);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //execute the task
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            Login.this.finish();
                        }
                    }, 2000);
                    //button.unblockTouch();
                } else {
                    morphToFailure(button, 600);
                    aleart();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            morphToSquare(button, 600);
                            button.unblockTouch();
                        }
                    }, 3000);

                }

            }
        }, 3000);

        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);

    }


    public void readNet(String url, final String un, final String pw) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String urlString = arg0[0];
                HttpPost post = new HttpPost(urlString);
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("__EVENTTARGET", ""));
                    params.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
                    params.add(new BasicNameValuePair("__LASTFOCUS", ""));
                    params.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUJNjk1MjA1MTY0D2QWAgIBD2QWBAIBDxYCHgdWaXNpYmxlZxYEZg8QZGQWAWZkAgEPEA8WBh4NRGF0YVRleHRGaWVsZAUM5Y2V5L2N5ZCN56ewHg5EYXRhVmFsdWVGaWVsZAUJ5Y2V5L2N5Y+3HgtfIURhdGFCb3VuZGdkEBU/CeS/neWNq+WkhAnotKLliqHlpIQS6LSi5pS/6YeR6J6N5a2m6ZmiEuaIkOS6uuaVmeiCsuWtpumZohLln47luILlu7rorr7lrabpmaIS5Yid562J5pWZ6IKy5a2m6ZmiDOS8oOaSreWtpumZoiHlvZPku6PlvaLmgIHmlofoibrlrabnoJTnqbbkuK3lv4MP5YWa5Yqe44CB5qCh5YqeCeaho+ahiOmmhhXlnLDnkIbkuI7njq/looPlrabpmaIb5a+55aSW6IGU57uc5LiO5o6l5b6F5Lit5b+DGOmrmOetieaVmeiCsueglOeptuS4reW/gxjlm73pmYXlkIjkvZzkuI7kuqTmtYHlpIQS5Zu96ZmF5pWZ6IKy5a2m6ZmiD+WQjuWLpOS/nemanOWkhBjljJblt6XlvIDlj5HnoJTnqbbkuK3lv4MS5YyW5a2m5YyW5bel5a2m6ZmiCeWfuuW7uuWkhBvorqHnrpfmnLrkv6Hmga/lt6XnqIvlrabpmaIq5rGf6KW/55yB5YWJ55S15a2Q5LiO6YCa5L+h6YeN54K55a6e6aqM5a6kD+aVmeW4iOaVmeiCsuWkhAnmlZnliqHlpIQM5pWZ6IKy5a2m6ZmiD+WGm+S6i+aVmeeglOWupBLnp5HmioDjgIHnpL7np5HlpIQS56eR5a2m5oqA5pyv5a2m6ZmiGOivvueoi+S4juaVmeWtpueglOeptuaJgBjnprvpgIDkvJHlt6XkvZzlip7lhazlrqQS55CG5YyW5rWL6K+V5Lit5b+DG+WOhuWPsuaWh+WMluS4juaXhea4uOWtpumZogznvo7mnK/lrabpmaIS5YWN6LS55biI6IyD55Sf6ZmiEuS6uuaJjeS6pOa1geS4reW/gwnkurrkuovlpIQM6L2v5Lu25a2m6ZmiCeWVhuWtpumZohvorr7lpIfkuI7lrp7pqozlrqTnrqHnkIblpIQS55Sf5ZG956eR5a2m5a2m6ZmiEuW4iOi1hOWfueiureS4reW/gxvmlbDlrabkuI7kv6Hmga/np5HlrablrabpmaIS57Sg6LSo5pWZ6IKy5Lit5b+DDOS9k+iCsuWtpumZognlm77kuabppoYP5aSW5Zu96K+t5a2m6ZmiHuWkluexjeS4k+WutueuoeeQhuacjeWKoeS4reW/gxLlpJbor63ogIPor5XkuK3lv4MP5paH5YyW56CU56m26ZmiCeaWh+WtpumZohvniannkIbkuI7pgJrkv6HnlLXlrZDlrabpmaIe546w5Luj5pWZ6IKy5oqA5pyv5bqU55So5Lit5b+DFeagoeWPi+W3peS9nOWKnuWFrOWupBXmoKHlm63nvZHnrqHnkIbkuK3lv4MM5b+D55CG5a2m6ZmiEuaWsOmXu+S/oeaBr+S4reW/gw/lrabmiqXmnYLlv5fnpL4P5a2m56eR5bu66K6+5aSECeWtpueUn+WkhAznoJTnqbbnlJ/pmaIS6Im65pyv56CU56m25Lit5b+DDOmfs+S5kOWtpumZog/mi5vnlJ/lsLHkuJrlpIQM5pS/5rOV5a2m6ZmiFT8IMTgwICAgICAIMTcwICAgICAINjgwMDAgICAINDUwICAgICAINjMwMDAgICAIODIwMDAgICAINjQwMDAgICAIMzgyICAgICAIMTMwICAgICAIMTA5ICAgICAINDgwMDAgICAIMTMyICAgICAIMzkwICAgICAIMTYwICAgICAINjkwMDAgICAIODcwMDAgICAIMzY1ICAgICAINjEwMDAgICAIMTQ0ICAgICAINjIwMDAgICAIMzgxICAgICAIMjUwICAgICAIMjQwMDAgICAINTAwMDAgICAIMzcwMDAgICAIMTQwICAgICAIODEwMDAgICAIMzI0ICAgICAIMTA0ICAgICAIMzIwICAgICAINTgwMDAgICAINjUwMDAgICAINTcwMDAgICAIMzMwICAgICAIMTUwICAgICAINjcwMDAgICAINTQwMDAgICAIMzYwICAgICAINjYwMDAgICAIMzEwICAgICAINTUwMDAgICAIMzgwMDAgICAINTYwMDAgICAIMjkwICAgICAINTIwMDAgICAIODkwMDAgICAIMzAwICAgICAIMzUwICAgICAINTEwMDAgICAINjAwMDAgICAIMzYxICAgICAIMTg5ICAgICAIMzA0ICAgICAINDkwMDAgICAIMTA2ICAgICAINDIwICAgICAIMTM2ICAgICAIMTEwICAgICAIMTkwICAgICAIMTQ2ICAgICAINTMwMDAgICAINDQwICAgICAINTkwMDAgICAUKwM/Z2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZ2dnZGQCAw8PFgIfAGhkFgQCAQ8PFgIeCEltYWdlVXJsBUBNeUNvbnRyb2wvQWxsX1Bob3RvU2hvdy5hc3B4P1VzZXJOdW09MTMwODA5NTA3OCZVc2VyVHlwZT1TdHVkZW50ZGQCAw8PFgIeBFRleHQFnQHmrKLov47mgqjvvIzlkLTlkK/kuJw8YnI+PGEgdGFyZ2V0PV9ibGFuayBocmVmPU15Q29udHJvbC9TdHVkZW50X0luZm9yQ2hlY2suYXNweD48c3Ryb25nPjxmb250IGNvbG9yPXJlZCBzaXplPTM+5qCh5a+55Liq5Lq65L+h5oGvPC9mb250PjwvZm9udD48L3N0cm9uZz48L2E+ZGQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgEFClJlbWVuYmVyTWVL9DEQwq27B1OYvZ515c+Dw2RwqwMstx3xyKGTxh2WIA=="));
                    params.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWSgLYl4WMBgLr6+/kCQK3yfbSBAKDspbeCQL21fViApC695MMAsjQmpEOAsjQpo4OAv3S2u0DAq/RroAOAv3S9t4DAqPW8tMDAv3S6tEDAoSwypgNAsjQtoIOArWVmJEHAr/R2u0DAsaw5o0NAo7QnpwOAsjQooMOAv3S3ugDAqPW5toDArfW7mMC/dL+0AMCvJDK9wsC/dLy0wMCw5aHjwMC6dGugA4C+dHq0QMC3NH61QMCntDm2gMCyNCqhQ4Co9b+0AMC8pHSiQwCvJDaiwwCjtCyhw4C3NHa7QMC/dLu3AMC3NHm2gMCjtC2gg4CyNCugA4C/dLm2gMC3NHq0QMCjtCigw4C/dLi3wMCjtC+hA4C3NHu3AMCntDa7QMC3NHi3wMC6dGenA4C3NHy0wMCo9be6AMCjtC6mQ4CjtCugA4C3NH+0AMC/dL61QMCw5bP/gICtZX4qQcC8pHaiwwCv9He6AMCqvCJ9QoCr9Gyhw4CqvCF/goCyNC+hA4CyNCenA4CqvC58QoC3NH23gMCr9GqhQ4C3NHe6AMC+euUqg4C2tqumwgC0sXgkQ8CuLeX+QECj8jxgAoP0m8Sj7LwLyeNyl7ka0HEgwkEhTIbhgvRBFELqH13qw=="));
                    params.add(new BasicNameValuePair("rblUserType", "Student"));
                    params.add(new BasicNameValuePair("ddlCollege", "180     "));
                    params.add(new BasicNameValuePair("StuNum", un));
                    params.add(new BasicNameValuePair("TeaNum", ""));
                    params.add(new BasicNameValuePair("Password", pw));
                    params.add(new BasicNameValuePair("login", "登录"));
                    post.setEntity(new UrlEncodedFormEntity(params));
                } catch (UnsupportedEncodingException e1) {

                }
                try {
                    HttpResponse response = client.execute(post);
                    cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
                    String value = EntityUtils.toString(response.getEntity());
                    Document doc = Jsoup.parse(value);   //把HTML代码加载到doc中，这部分是课程表
                    Elements title = doc.select("span#lblMsg");
                    content = title.text();
                    //System.out.println(title.);
                    //System.out.println(value);
                    if (value.indexOf("学号不存在!") >= 0) {
                        loginSign = 1;
                    } else if (value.indexOf("对不起，您的密码不正确，请注意您是否区分了大小写!") >= 0) {
                        loginSign = 2;
                    } else {
                        loginSign = 3;
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
                if (loginSign == 3) {
                    try {
                        SharedPreferences sp = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor ed = sp.edit();
                        //String key = "41227677";//密钥
                        //ed.putString("Name", key);
                        //String encryptData = new Encrypt().encrypt(key, pw);//加密
                        c1 = cookies.get(1).getValue();
                        c2 = cookies.get(0).getValue();
                        //System.out.println(content);

                        String names[] = content.split("，");
                        if (names[1] != null) {
                            String names2[] = names[1].split(" ");
                            ed.putString("UserName", names2[0]);
                        }

                        System.out.println(names[1]);
                        // Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                        ed.putString("StuNum", un);
                        ed.putString("Special", c1);
                        ed.putString("Cookie", c2);


                        ed.putString("LoginSucceed", "Yes");
                        ed.putBoolean("isFirst", true);
                        ed.commit();
                        SharedPreferences sp2 = getSharedPreferences("TermInfo", 0);
                        SharedPreferences.Editor ed2 = sp2.edit();
                        GetTime gt = new GetTime();
                        gt.set();
                        String y = gt.getYear();//年份
                        int m = Integer.parseInt(gt.getMonth());//月份
                        m = (m >= 6 ? 9 : 3);
                        String T = y + "/" + m + "/1+0:00:00";
                        ed2.putString("Term", T);
                        ed2.commit();

                    } catch (Exception e) {
                        loginSign = 4;
                        aleart();
                        e.printStackTrace();
                    }

                }

            }


        }.execute(url);
    }


    public void aleart() {
        AlertDialog.Builder ad1 = new AlertDialog.Builder(Login.this);
        //ad1.setTitle("温馨提示");
        ad1.setCancelable(false);
        if (loginSign == 1) ad1.setMessage("学号不存在！");
        else if (loginSign == 2) ad1.setMessage("对不起，您的密码不正确，请注意您是否区分了大小写!");
        else if (loginSign == -1) ad1.setMessage("请填写完整信息!");
        else
            ad1.setMessage("未知错误，请再次尝试登录!");

        ad1.setNegativeButton("返回", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int i) {
                // TODO Auto-generated method stub

            }
        });
        ad1.show();
    }

}
