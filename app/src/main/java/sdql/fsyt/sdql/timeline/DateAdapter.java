package sdql.fsyt.sdql.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

import sdql.fsyt.sdql.R;

/**
 * Created by KevinWu on 2015/12/22.
 */
public class DateAdapter extends BaseAdapter {
    private Context context;
    private List<DateText> list;

    public DateAdapter(Context context, List<DateText> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        holder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(
                R.layout.item_time_line, parent, false);
        holder.time = (TextView) convertView
                .findViewById(R.id.txt_date_time);

        holder.context = (TextView) convertView
                .findViewById(R.id.txt_date_context);
        holder.title = (RelativeLayout) convertView
                .findViewById(R.id.rl_title);
        holder.line = (View) convertView.findViewById(R.id.v_line);
        convertView.setTag(holder);
        //时间轴竖线的layout
        LayoutParams params = (LayoutParams) holder.line.getLayoutParams();
        //第一条数据，肯定显示时间标题
        holder.title.setVisibility(View.VISIBLE);
        holder.time.setText(TimeFormat.format("yyyy-MM-dd HH:mm:ss",
                list.get(position).getTime()));
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.rl_title);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.txt_date_context);
        holder.line.setLayoutParams(params);
        holder.context.setText(list.get(position).getContext());
        return convertView;
    }

    public static class ViewHolder {
        RelativeLayout title;
        View line;
        TextView time;
        TextView context;
    }
}
