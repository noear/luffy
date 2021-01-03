package org.noear.luffy.cap.extend.sited.dao.custom;


import org.noear.luffy.cap.extend.sited.dao.engine.SdAttributeList;
import org.noear.luffy.cap.extend.sited.dao.engine.SdNode;
import org.noear.luffy.cap.extend.sited.dao.engine.SdSource;
import org.noear.luffy.cap.extend.sited.dao.engine.SdValue;
import org.noear.luffy.cap.extend.sited.utils.TextUtils;

/**
 * Created by yuety on 16/2/1.
 */
public class DdNode extends SdNode {

    public DdSource s(){
        return (DdSource)source;
    }


    //是否支持全部下载(book[1,2,3])
    public boolean donwAll = true;
    //是否显示导航能力（用于：section[1,2,3]）;即上一章下一章;
    public boolean showNav = true;
    //是否显示图片（null：默认；0：不显示；1：显示小图；2：显示大图）
    public String showImg;
    //是否自适应大小（基于pad 或 phone 显示不同的大小）
    public boolean autoSize=false;
    //是否显示S按钮
    public boolean showWeb=true;
    //屏幕方向（v/h）
    public String screen;
    //首页图片显示的宽高比例
    public float WHp = 0;
    //是否循环播放
    public boolean loop = false;
    //样式风格
    public int style;

    //预设选项
    public String options;

    public static final int STYLE_VIDEO = 11;
    public static final int STYLE_AUDIO = 12;
    public static final int STYLE_INWEB = 13;

    private SdValue _web;


    public DdNode(SdSource source){
        super(source);
    }

    @Override
    public void OnDidInit() {
        donwAll = attrs.getInt("donwAll", 1) > 0;
        showNav = attrs.getInt("showNav", 1) > 0;
        showImg = attrs.getString("showImg");
        autoSize = attrs.getInt("autoSize", 0) > 0;
        showWeb = attrs.getInt("showWeb", s().isPrivate() ? 0 : 1) > 0; //isPrivate时，默认不显示；否则默认显示
        screen  = attrs.getString("screen");
        loop    = attrs.getInt("loop", 0) > 0;

        _web     = attrs.getValue("web"); //控制外部浏览器的打开
        if(source.schema < 2) {
            _web.build     = attrs.getString("buildWeb");
        }


        options  = attrs.getString("options");


        style = attrs.getInt("style", STYLE_VIDEO);

        if(TextUtils.isEmpty(screen) && style == STYLE_AUDIO) {
            screen = "v";
        }

        String w = attrs.getString("w");
        if (TextUtils.isEmpty(w) == false) {
            String h = attrs.getString("h");
            WHp = Float.parseFloat(w) / Float.parseFloat(h);
        }
    }

    //是否内部WEB运行
    public boolean isWebrun(){
        String run = attrs.getString("run");

        if(run==null)
            return false;

        return run.indexOf("web")>=0;
    }

    //是否外部WEB运行
    public boolean isOutWebrun(){
        String run = attrs.getString("run");

        if(run==null)
            return false;

        return run.indexOf("outweb")>=0;
    }

    public String getWebUrl(String url) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url",url);

        return _web.run(source,atts,url);
    }
}
