package org.noear.luffy.trick.extend.sited.dao.custom;

import org.noear.luffy.trick.extend.sited.dao.engine.SdNode;
import org.noear.luffy.trick.extend.sited.dao.engine.SdSource;
import org.noear.luffy.trick.extend.sited.utils.TextUtils;
import org.noear.luffy.trick.extend.sited.dao.engine.ISdNode;

/**
 * Created by yuety on 15/8/3.
 */
public class DdSource extends SdSource {
    public final int ver; //版本号
    public final String sds; //插件平台服务
    public final int vip;
    public final int level;
    //是否为私密型插件
    public boolean isPrivate(){
        return attrs.getInt("private") > 0;
    }

    public String guid;

    public final String logo;  //图标
    public final String author;
    public final String contact;
    public final String alert; //提醒（打开时跳出）
    public final String intro; //介绍
    //---------------------------------------------------
    public final DdNodeAbout about;
    //---------------------------------------------------
    public final DdNodeSet meta;
    public final DdNodeSet main;

    public final DdNode hots;
    public final DdNode updates;
    public final DdNode search;
    public final DdNode tags;
    public final DdNodeSet home;

    private ISdNode _tag;
    private ISdNode _subtag;
    private ISdNode _book;
    private ISdNode _section;
    private ISdNode _objectSlf;
    private ISdNode _objectExt;
    private ISdNode _cover;

    public DdNode tag(String url){
        return  (DdNode)_tag.nodeMatch(url);
    }
    public DdNode subtag(String url){
        return  (DdNode)_subtag.nodeMatch(url);
    }
    public DdNode book(String url){
        return  (DdNode)_book.nodeMatch(url);
    }
    public DdNode section(String url){
        return  (DdNode)_section.nodeMatch(url);
    }

    public DdNode objectExt(String url){

        return  (DdNode)_objectExt.nodeMatch(url);
    }

    public DdNode objectSlf(String url){

        return  (DdNode)_objectSlf.nodeMatch(url);
    }

    public DdNode cover(String url){

        return  (DdNode)_cover.nodeMatch(url);
    }



    public final DdNodeLogin login;

    private final String trace_url;

//    public String sited;

    public DdSource(String xml) throws Exception {
        super();
        DdApi.tryInit(DdAdapter.g);

        if (xml.startsWith("sited::")) {
            int start = xml.indexOf("::") + 2;
            int end = xml.lastIndexOf("::");
            String txt = xml.substring(start, end);
            String key = xml.substring(end + 2);
            xml = DdApi.unsuan(txt, key);
        }

        //sited = xml;

        doInit( xml);
        if(schema>=1){
            xmlHeadName = "meta";
            xmlBodyName = "main";
            xmlScriptName = "script";
        }else{
            xmlHeadName = "meta";
            xmlBodyName = "main";
            xmlScriptName = "jscript";
        }

        doLoad();

        meta = (DdNodeSet) head;
        main = (DdNodeSet) body;


        //--------------

        sds = head.attrs.getString("sds");
        ver = head.attrs.getInt("ver");
        vip = head.attrs.getInt("vip");
        level = head.attrs.getInt("level");

        author = head.attrs.getString("author");
        contact = head.attrs.getString("contact");

        intro = head.attrs.getString("intro");
        logo = head.attrs.getString("logo");

        guid = head.attrs.getString("guid");

        if (engine > DdApi.version())
            alert = "此插件需要更高版本引擎支持，否则会出错。建议升级！";
        else
            alert = head.attrs.getString("alert");

        //
        //---------------------
        //

        trace_url = main.attrs.getString("trace");

        home = (DdNodeSet) main.get("home");
        {
            hots = (DdNode) home.get("hots");
            updates = (DdNode) home.get("updates");
            tags = (DdNode) home.get("tags");
        }

        search = (DdNode) main.get("search");

        _tag = main.get("tag");
        _subtag = main.get("subtag");
        _book = main.get("book");
        _section = main.get("section");
        _objectSlf = main.get("object");
        _objectExt = _objectSlf;
        _cover = main.get("cover");

        if (_objectExt.isEmpty()) {
            if (_section.isEmpty())
                _objectExt = _book;
            else
                _objectExt = _section;
        }


        if(schema>=1) {
            login = (DdNodeLogin) head.get("login");//登录
            DdNodeAbout temp  = (DdNodeAbout) head.get("reward");//打赏

            if(temp.isEmpty()){
                temp = (DdNodeAbout) head.get("about");//打赏
            }

            about = temp;
        }else{
            login = (DdNodeLogin) main.get("login");//登录
            DdNodeAbout temp = (DdNodeAbout) main.get("reward");//打赏

            if(temp.isEmpty()){
                temp = (DdNodeAbout) main.get("about");//打赏
            }

            about = temp;
        }
    }

    private String _FullTitle;
    public String fullTitle() {
        if (_FullTitle == null) {
            if(isPrivate()){
                _FullTitle = title;
            }else {
                int idx = url.indexOf('?');
                if (idx < 0)
                    _FullTitle = title + " (" + url + ")";
                else
                    _FullTitle = title + " (" + url.substring(0, idx) + ")";
            }
        }

        return _FullTitle;
    }

    public String webUrl(){
        if(TextUtils.isEmpty(main.durl))
            return url;
        else
            return main.durl;
    }

    @Override
    public void setCookies(String cookies) {
        if (cookies == null)
            return;

    }

    @Override
    public String cookies() {


        return _cookies;
    }

    @Override
    protected boolean DoCheck(String url, String cookies, boolean isFromAuto) {
        if (login.isEmpty()) {
            return true;
        } else {
            return login.doCheck(url, cookies, isFromAuto);
        }
    }



    public static boolean isHots(SdNode node){
        return "hots".equals(node.name);
    }

    public static boolean isUpdates(SdNode node){
        return "updates".equals(node.name);
    }

    public static boolean isTags(SdNode node){
        return "tags".equals(node.name);
    }

    public static boolean isBook(SdNode node){
        return "book".equals(node.name);
    }


}
