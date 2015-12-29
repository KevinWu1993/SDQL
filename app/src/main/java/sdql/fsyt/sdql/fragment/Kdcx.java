package sdql.fsyt.sdql.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.List;

import sdql.fsyt.sdql.R;
import sdql.fsyt.sdql.database.DBHelper;
import sdql.fsyt.sdql.utils.KddhListAdapter;
import sdql.fsyt.sdql.utils.KddhStructure;
import sdql.fsyt.sdql.utils.MyReAn;
import sdql.fsyt.sdql.windowThemeActivity.ExpressInfo;

/**
 * Created by KevinWu on 2015/11/15.
 */
public class Kdcx extends Fragment {
    private Context context;
    private View view;
    LinearLayout nulldatapic;//快递列表为空时显示的图片
    DBHelper mDBHelper = null;//声明DBHelper类
    SQLiteDatabase mSQLiteDatabase = null;
    RecyclerView mRecyclerView;
    List<KddhStructure> dhlist = new ArrayList<KddhStructure>();//快递单号
    KddhListAdapter mKddhListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kdcx, container, false);
        context = container.getContext();

        nulldatapic = (LinearLayout) view.findViewById(R.id.nulldatapic);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.base_kd_list);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.btadd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog();
            }
        });
        initView();
       //mRecyclerView.setOn

        return view;
    }

    //添加快递单号菜单
    private void addDialog() {
        final List<String> rtList = new ArrayList<>();//返回的列表
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);// 渲染器
        final View view = inflater.inflate(R.layout.fragment_kdcx_add,
                null);
        final NiceSpinner niceSpinner = (NiceSpinner) view.findViewById(R.id.ex_list);
        final ArrayList<String> dataset = new ArrayList<>();
        //添加几个师大常用的快递公司测试
        dataset.add("顺丰速运");
        dataset.add("圆通快递");
        dataset.add("天天快递");
        dataset.add("百世汇通");
        dataset.add("全峰快递");
        dataset.add("申通快递");
        dataset.add("韵达快递");
        dataset.add("中通快递");
        dataset.add("EMS");
        dataset.add("宅急送快递");
        dataset.add("飞远物流");
        niceSpinner.attachDataSource(dataset);
        ad.setView(view);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.ex_done);
        final EditText etDH = (EditText) view.findViewById(R.id.ex_num);
        final EditText etRemark = (EditText) view.findViewById(R.id.ex_remark);

        final AlertDialog dialog = ad.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etDH.getText().toString().equals("") && etRemark.getText().toString().equals("")) {
                    rtList.add(dataset.get(niceSpinner.getSelectedIndex()));//第一个元素为添加快递公司名
                    rtList.add(etDH.getText().toString().trim());//第二个单号
                    rtList.add(dataset.get(niceSpinner.getSelectedIndex()));//第三个备注，备注留空就用快递公司名代替
                    dialog.dismiss();
                    addDataToDataBase(rtList);
                    initView();
                } else if (!etDH.getText().toString().equals("") && !etRemark.getText().toString().equals("")) {
                    rtList.add(dataset.get(niceSpinner.getSelectedIndex()));//第一个元素为添加快递公司名
                    rtList.add(etDH.getText().toString().trim());//第二个单号
                    rtList.add(etRemark.getText().toString());//第三个备注
                    dialog.dismiss();
                    addDataToDataBase(rtList);
                    initView();
                } else {
                    //信息不完整的情况，要求填完整信息再提交
                    Snackbar.make(view.getRootView(), "请填写完整信息再提交~", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    //初始化UI
    private void initView() {
        mDBHelper = new DBHelper(context);
        mSQLiteDatabase = mDBHelper.getReadableDatabase();
        Cursor c = mSQLiteDatabase.rawQuery("select * from RecentExpress", null);
        int count;
        if ((count = c.getCount()) > 0) {
            dhlist.clear();
            nulldatapic.setVisibility(View.GONE);
            c.moveToFirst();
            for (int i = 0; i < count; i++) {
                int imgid = getImgID(c.getString(c.getColumnIndex("ExCompany")));
                dhlist.add(new KddhStructure(imgid, c.getString(c.getColumnIndex("ExNum")), c.getString(c.getColumnIndex("ExRemark")), c.getString(c.getColumnIndex("ExCompany"))));
                c.moveToNext();
            }
            mKddhListAdapter=new KddhListAdapter(context,dhlist);
            mKddhListAdapter.setOnItemClickListener(new KddhListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, KddhStructure dh) {
                    System.out.println("数据为：" + dh.getExCo());
                    Intent intent=new Intent();
                    intent.setClass(context, ExpressInfo.class);
                    intent.putExtra("rm", dh.getExRemark());
                    intent.putExtra("num",dh.getExNum() );
                    intent.putExtra("co",dh.getExCo());
                    startActivity(intent);
                }
            });
            mKddhListAdapter.setOnItemLongClickListener(new KddhListAdapter.OnRecyclerViewItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, KddhStructure dh) {
                    delDH(dh.getExNum(),dh.getExCo());
                }
            });
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mKddhListAdapter);
            mRecyclerView.setItemAnimator(new MyReAn());
        } else {
            nulldatapic.setVisibility(View.VISIBLE);
        }
    }

    //删除单号方法
    private void delDH(final String num,final String co) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("确定删除此单？");
        ad.setNegativeButton("返回", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDBHelper = new DBHelper(context);
                mSQLiteDatabase = mDBHelper.getWritableDatabase();
                mSQLiteDatabase.delete("RecentExpress", "ExNum=?", new String[]{num + ""});
               //  mSQLiteDatabase.rawQuery("delete  from RecentExpress where ExNum='"+num+"' and ExCompany='"+co+"'", null);
                  initView();;
            }
        });
        ad.show();
    }

    //取得快递公司的图片
    private int getImgID(String co) {
        int id=R.drawable.ex_co_shunfeng;
        switch (co) {
            case "顺丰速运":
                id=R.drawable.ex_co_shunfeng;
                break;
            case "圆通快递":
                id=R.drawable.ex_co_yuantong;
                break;
            case "天天快递":
                id=R.drawable.ex_co_tiantian;
                break;
            case "百世汇通":
                id=R.drawable.ex_co_huitong;
                break;
            case "全峰快递":
                id=R.drawable.ex_co_quanfeng;
                break;
            case "申通快递":
                id=R.drawable.ex_co_shentong;
                break;
            case "韵达快递":
                id=R.drawable.ex_co_yunda;
                break;
            case "中通快递":
                id=R.drawable.ex_co_zhongtong;
                break;
            case "EMS":
                id=R.drawable.ex_co_ems;
                break;
            case "宅急送快递":
                id=R.drawable.ex_co_zhaiji;
                break;
            case "飞远物流":
                id=R.drawable.ex_co_feiyuan;
                break;
        }
        return  id;
    }

    private void addDataToDataBase(List<String> gList) {
        if (gList.size() > 0) {
            mDBHelper = new DBHelper(context);
            mSQLiteDatabase = mDBHelper.getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put("ExCompany", gList.get(0));
            v.put("ExNum", gList.get(1));
            v.put("ExRemark", gList.get(2));
            mSQLiteDatabase.insert("RecentExpress", null, v);//插入RecentExpress表
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }
}
