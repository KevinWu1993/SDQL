package sdql.fsyt.sdql.utils;

import android.widget.ImageView;

/**
 * Created by KevinWu on 2015/12/20.
 * 快递单号结构体
 */

public class KddhStructure {
    int imgR;//快递公司图片id
    String ExNum;//快递单号
    String ExRemark;//快递备注信息
    String ExCo;//快递公司

    //构造方法
    public KddhStructure(int imgR,String ExNum,String ExRemark,String ExCo){
        this.imgR=imgR;
        this.ExNum=ExNum;
        this.ExRemark=ExRemark;
        this.ExCo=ExCo;
    }

    public String getExRemark() {
        return ExRemark;
    }
    public String getExNum(){
        return ExNum;
    }
    public String getExCo(){
        return ExCo;
    }
}