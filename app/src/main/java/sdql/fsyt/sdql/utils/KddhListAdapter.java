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
 * Created by KevinWu on 2015/12/20.
 * 快递单号列表适配器
 */
public class KddhListAdapter  extends RecyclerView.Adapter<KddhListAdapter.ViewHolder>
        implements  View.OnClickListener,View.OnLongClickListener{
    private List<KddhStructure> kddh;
    private Context mContext;
    public KddhListAdapter(Context context, List kddh) {
        this.mContext = context;
        this.kddh = kddh;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_kdcx_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 给ViewHolder设置元素
        KddhStructure dh = kddh.get(position);
        holder.tvNum.setText(dh.ExNum);
        holder.tvRemark.setText(dh.ExRemark);
        holder.img.setBackgroundDrawable(mContext.getResources().getDrawable(dh.imgR));
        //将数据保存在itemView的Tag中，以便点击时进行获取
       holder.itemView.setTag(kddh.get(position));
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return kddh== null ? 0 : kddh.size();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    //默认接口
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view ,KddhStructure dh);
    }
    public static interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view ,KddhStructure dh);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(KddhStructure)v.getTag());
        }
    }
    @Override
    public boolean onLongClick(View v){
        if(mOnItemLongClickListener != null){
            mOnItemLongClickListener.onItemLongClick(v, (KddhStructure) v.getTag());
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
        public TextView tvNum;
        public TextView tvRemark;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNum = (TextView) itemView.findViewById(R.id.ex_tv_num);
            tvRemark = (TextView) itemView.findViewById(R.id.ex_tv_remark);
            img=(ImageView)itemView.findViewById(R.id.ex_img_co);
            itemView.findViewById(R.id.kdcx_item_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

}
