package sdql.fsyt.sdql.utils;

/**
 * Created by KevinWu on 2015/12/10.
 */
public class KccjStructure {
    String className;//课程名称
    String classScore;//课程成绩

    //构造方法
    public KccjStructure(String className,String classScore){
        this.className=className;
        this.classScore=classScore;
    }
}