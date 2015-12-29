package sdql.fsyt.sdql.utils;

/**
 * Created by KevinWu on 2015/12/26.
 * 新闻列表数据结构
 */
public class XwStructure {
    String newsTitle;//新闻标题

    public String getNewsTime() {
        return newsTime;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsURL() {
        return newsURL;
    }

    public String getNewsPicURL() {
        return newsPicURL;
    }

    String newsTime;//新闻时间
    String newsURL;//新闻地址
    String newsPicURL;//新闻缩略图URL，预留变量
    public XwStructure(String newsTitle,String newsTime,String newsURL){
        this.newsTitle=newsTitle;
        this.newsTime=newsTime;
        this.newsURL=newsURL;
    }

    /**
     * 预留构造方法，供要拓展图片缩略图列表时用到
     * @param newsTitle
     * @param newsTime
     * @param newsURL
     * @param newsPicURL
     */
    public XwStructure(String newsTitle,String newsTime,String newsURL,String newsPicURL ){
        this.newsTitle=newsTitle;
        this.newsTime=newsTime;
        this.newsURL=newsURL;
        this.newsPicURL=newsPicURL;
    }
}
