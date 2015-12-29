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
 * Created by KevinWu on 2015/10/25.
 */
public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.GroupClassItemHolder> {
    private List<Course> Courses;

    private Context mContext;

    public CourseListAdapter( Context context , List<Course> Courses)
    {
        this.mContext = context;
        this.Courses = Courses;
    }

    @Override
    public GroupClassItemHolder onCreateViewHolder( ViewGroup viewGroup, int i )
    {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_base_class_group_item, viewGroup, false);
        return new GroupClassItemHolder(v);
    }


    @Override
    public void onBindViewHolder(GroupClassItemHolder viewHolder, int i )
    {
        // 给ViewHolder设置元素
        Course p = Courses.get(i);
        viewHolder.classesTime.setText(p.cTime);
        viewHolder.classesTitle.setText(p.cTitle);
        viewHolder.classesPos.setText(p.cPos);
        viewHolder.classesTeacher.setText(p.cTeacher);
    }

    @Override
    public int getItemCount()
    {
        // 返回数据总数
        return Courses == null ? 0 : Courses.size();
    }





    public static class GroupClassItemHolder  extends ItemHolder{
        TextView classesTime;

        public GroupClassItemHolder(View itemView) {
            super(itemView);
            classesTime = (TextView) itemView.findViewById(R.id.base_class_group_item_time);
        }
    }
    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView classesTitle,classesPos,classesTeacher;//课程名，上课地点，老师

        public ItemHolder(View itemView) {
            super(itemView);
            classesTitle=(TextView)itemView.findViewById(R.id.base_couse_title);
            classesPos=(TextView)itemView.findViewById(R.id.base_pos_title);
            classesTeacher=(TextView)itemView.findViewById(R.id.base_tea_title);
            itemView.findViewById(R.id.base_item_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
