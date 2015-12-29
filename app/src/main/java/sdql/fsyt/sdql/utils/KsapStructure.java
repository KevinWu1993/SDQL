package sdql.fsyt.sdql.utils;

/**
 * Created by KevinWu on 2015/12/18.
 * 考试安排的数据的结构
 */
public class KsapStructure {
    String kecheng;//课程名
    String shijian;//时间
    String kaochang;//考场
    String zuowei;//座位
    String beizhu;//备注
    public KsapStructure(String kecheng,String shijian,String kaochang,String zuowei,String beizhu){
        this.kecheng=kecheng;
        this.shijian=shijian;
        this.kaochang=kaochang;
        this.zuowei=zuowei;
        this.beizhu=beizhu;
    }
}
