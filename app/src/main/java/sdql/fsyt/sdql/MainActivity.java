package sdql.fsyt.sdql;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import sdql.fsyt.sdql.database.DBHelper;
import sdql.fsyt.sdql.fragment.Kccj;
import sdql.fsyt.sdql.fragment.Kdcx;
import sdql.fsyt.sdql.fragment.Ksap;
import sdql.fsyt.sdql.fragment.Sdyw;
import sdql.fsyt.sdql.fragment.Sstq;
import sdql.fsyt.sdql.fragment.Wdkb;
import sdql.fsyt.sdql.fragment.Xydt;
import sdql.fsyt.sdql.uiToolkit.CircleTransformation;
import sdql.fsyt.sdql.windowThemeActivity.ReadNew;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView ivMenuUserProfilePhoto;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("实时天气");
        setSupportActionBar(toolbar);
        setupHeader();//设置header
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();
        drawer.setDrawerListener(toggle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new Sstq())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_sstq) {
            toolbar.setTitle("实时天气");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new Sstq())
                    .commit();
        } else if (id == R.id.menu_kccj) {
            toolbar.setTitle("课程成绩");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new Kccj())
                    .commit();
        } else if (id == R.id.menu_ksap) {
            toolbar.setTitle("考试安排");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new Ksap())
                    .commit();
        }else if(id==R.id.menu_kdcx){
            toolbar.setTitle("快递查询");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,new Kdcx())
            .commit();
        }
        else if(id==R.id.menu_wdkb){
            toolbar.setTitle("我的课表");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,new Wdkb()).commit();
        }
        else if(id==R.id.menu_sdyw){
            //先跳转过去测试用
//            Intent intent=new Intent();
//            intent.setClass(this, ReadNew.class);
//            startActivity(intent);
            toolbar.setTitle("师大要闻");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,new Sdyw())
                    .commit();
        }else if(id==R.id.menu_xydt){
            toolbar.setTitle("校园动态");
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,new Xydt())
                    .commit();
        }
        else if(id==R.id.menu_logout){
            logoutAleart();//调用退出登录对话框
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupHeader() {
        ivMenuUserProfilePhoto = (ImageView) findViewById(R.id.imageView);
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.global_menu_avatar_size);
        SharedPreferences sp = getSharedPreferences("UserInfo", 0);
        String stunum = sp.getString("StuNum", "");
        String stuname = sp.getString("UserName", "");
        System.out.println("取得名字的值为：" + stuname);
        System.out.println("取到学号的值为：" + stunum);
        final TextView tvn = (TextView) findViewById(R.id.tvname);
        tvn.setText(stuname);
        String profilePhoto = "http://jwc.jxnu.edu.cn/MyControl/All_PhotoShow.aspx?UserNum=" + stunum + "&UserType=Student";
//     String profilePhoto = "http://t11.baidu.com/it/u=2831784145,2588609848&fm=58";
        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);
    }

    public void logoutAleart() {
        AlertDialog.Builder ad1 = new AlertDialog.Builder(this);
        ad1.setTitle("温馨提示");
        ad1.setCancelable(false);
        ad1.setMessage("确定要注销账户并清空数据么!");
        ad1.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int i) {
                // TODO Auto-generated method stub
                boolean isdone = true;
                isdone=MainActivity.this.deleteDatabase("SDQL.db");
                if (isdone ) {
                    SharedPreferences sp =getSharedPreferences("UserInfo", 0);
                    SharedPreferences sp2 = getSharedPreferences("TermInfo", 0);
                    SharedPreferences.Editor ed = sp.edit();
                    SharedPreferences.Editor ed2 = sp2.edit();
                    ed2.clear();
                    ed.clear();
                    ed2.clear();
                    ed.commit();
                    finish();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "註銷登錄失敗，請再次嚐試", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad1.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int i) {
                // TODO Auto-generated method stub

            }
        });
        ad1.show();
    }
}
