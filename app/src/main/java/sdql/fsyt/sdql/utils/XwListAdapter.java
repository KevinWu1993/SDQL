package sdql.fsyt.sdql.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sdql.fsyt.sdql.R;

/**
 * Created by KevinWu on 2015/12/26.
 * 新闻列表适配器
 */
public class XwListAdapter   extends RecyclerView.Adapter<XwListAdapter.ViewHolder>
        implements  View.OnClickListener,View.OnLongClickListener{
    private List<XwStructure> xwList;
    private Context mContext;
    public XwListAdapter(Context context, List xwList) {
        this.mContext = context;
        this.xwList = xwList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_xw_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 给ViewHolder设置元素
        XwStructure xw = xwList.get(position);
        holder.tvTitle.setText(xw.newsTitle);
        holder.tvTime.setText(xw.newsTime);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(xwList.get(position));
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return xwList== null ? 0 : xwList.size();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    //默认接口
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view ,XwStructure xw);
    }
    public static interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view ,XwStructure xw);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(XwStructure)v.getTag());
        }
    }
    @Override
    public boolean onLongClick(View v){
        if(mOnItemLongClickListener != null){
            mOnItemLongClickListener.onItemLongClick(v, (XwStructure) v.getTag());
        }
        return true;
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvtitle);
            tvTime = (TextView) itemView.findViewById(R.id.tvtime);
//            itemView.findViewById(R.id.xw_item_container).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }

    }

}

