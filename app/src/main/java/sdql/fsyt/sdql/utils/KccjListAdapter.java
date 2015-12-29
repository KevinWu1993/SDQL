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
 * Created by KevinWu on 2015/12/10.
 */
public class KccjListAdapter extends RecyclerView.Adapter<KccjListAdapter.ViewHolder> {
    private List<KccjStructure> kccj;
    private Context mContext;

    public KccjListAdapter(Context context, List kccj) {
        this.mContext = context;
        this.kccj = kccj;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_kccj_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 给ViewHolder设置元素
        KccjStructure kc = kccj.get(position);
        holder.tvclassName.setText(kc.className);
        holder.tvclassScore.setText(kc.classScore);
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return kccj == null ? 0 : kccj.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvclassName;
        public TextView tvclassScore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvclassName = (TextView) itemView.findViewById(R.id.tvkc);
            tvclassScore = (TextView) itemView.findViewById(R.id.tvfs);
            itemView.findViewById(R.id.kccj_item_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

}
