package sdql.fsyt.sdql.timeline;

/**
 * Created by KevinWu on 2015/12/22.
 */
public class DateText {
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    private String time;//快递时间
    private String context;//返回快递说明信息

    public DateText() {

    }

    public DateText(String time, String context) {
        super();
        this.time = time;
        this.context = context;
    }

}
