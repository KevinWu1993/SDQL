package sdql.fsyt.sdql.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sdql.fsyt.sdql.R;


/**
 * Created by KevinWu on 2015/12/18.
 */
public class KsapListAdapter  extends RecyclerView.Adapter<KsapListAdapter.ViewHolder> {
    private List<KsapStructure> ksap;
    private Context mContext;

    public KsapListAdapter(Context context, List ksap) {
        this.mContext = context;
        this.ksap= ksap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ksap_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 给ViewHolder设置元素
        KsapStructure k = ksap.get(position);
        holder.tvkecheng.setText(k.kecheng);
        holder.tvshijian.setText(k.shijian);
        holder.tvkaochang.setText(k.kaochang);
        holder.tvzuowei.setText(k.zuowei);
        holder.tvbeizhu.setText(k.beizhu);
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return ksap == null ? 0 : ksap.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvkecheng;
        public TextView tvshijian;
        public TextView tvkaochang;
        public TextView tvzuowei;
        public TextView tvbeizhu;

        public ViewHolder(View itemView) {
            super(itemView);
            tvkecheng=(TextView)itemView.findViewById(R.id.ksap_kecheng);
            tvshijian=(TextView)itemView.findViewById(R.id.ksap_shijian);
            tvkaochang=(TextView)itemView.findViewById(R.id.ksap_kaochang);
            tvzuowei=(TextView)itemView.findViewById(R.id.ksap_zuowei);
            tvbeizhu=(TextView)itemView.findViewById(R.id.ksap_beizhu);
            itemView.findViewById(R.id.ksap_item_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

}
