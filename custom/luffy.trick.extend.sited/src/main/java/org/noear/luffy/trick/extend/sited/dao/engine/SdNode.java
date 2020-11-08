package org.noear.luffy.trick.extend.sited.dao.engine;


import org.noear.luffy.trick.extend.sited.utils.TextUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by yuety on 15/8/2.
 */
public class SdNode implements ISdNode{

    public SdNode(SdSource source){
        this.source = source;
    }

    protected void OnDidInit(){

    }

    private int _dtype;
    public int dtype(){
        if(_dtype>0)
            return _dtype;
        else {
            return source.body.dtype();
        }
    }

    private int _btype;
    public int btype(){
        if(_btype>0)
            return _btype;
        else
            return dtype();
    }


    public int nodeType(){return 1;}
    public String nodeName(){return name;}
    public SdNode nodeMatch(String url){return this;}

    public final SdAttributeList attrs = new SdAttributeList();

    //info
    public String name; //节点名称
    public String key; //自定义关键字
    public String title;//标题
    public String txt; //txt//一用于item
    public String logo; //logo
    public String expr;
    public String group;

    protected String lib;
    public String btn;
    //数据更新方式
    public int update;


    //可动态构建
    public SdValue url; //url
    public SdValue args;
    private SdValue referer;
    private SdValue cookie;
    private SdValue header;   //http header 头需求: cookies|accept


    protected String method;//http method

    private String _encode;   //http 编码
    private String _ua;     //http ua

    //cache
    protected int cache=1;//单位为秒(0不缓存；1不限时间)

    //parse
    protected String onParse; //解析函数
    protected String onParseUrl; //解析出真正在请求的Url



    //build
//    protected String buildArgs;
//    protected String buildUrl;
//    protected String buildReferer;
//    protected String buildHeader;

    //add prop for search or tag
    protected String addCookie; //需要添加的关键字
    protected String addKey; //需要添加的关键字
    protected int    addPage;//需要添加的页数值


    //ext prop (for post)


    public final SdSource source;

    private boolean _isEmpty;
    @Override
    public boolean  isEmpty(){
        return _isEmpty;
    }

    //下属项目
    private List<SdNode> _items;
    public List<SdNode> items(){
        return _items;
    }

    //下属数据节点
    private List<SdNode> _adds;
    public List<SdNode> adds(){
        return _adds;
    }


    //是否有宏定义@key,@page
    public boolean hasMacro(){
        if(url== null || url.indexOf("@")<0)
            return false;
        else
            return true;
    }

    //是否有分页
    public boolean hasPaging(){
        return hasMacro() || url.isEmptyBuild() == false || "post".equals(method);
    }

    public boolean isMatch(String url){
        if(TextUtils.isEmpty(expr)==false){
            Pattern pattern = Pattern.compile(expr);
            Matcher m = pattern.matcher(url);

            return m.find();
        }else {
            return false;
        }
    }

    public boolean isEquals(SdNode node)
    {
        if(name==null)
            return false;

        return name.equals(node.name);
    }

    public boolean isInCookie()
    {
        if(header==null)
            return false;
        else
            return header.indexOf("cookie")>=0;
    }

    public boolean isInReferer()
    {
        if(header==null)
            return false;
        else
            return header.indexOf("referer")>=0;
    }

    public boolean hasItems(){
        if(_items == null || _items.size()==0)
            return false;
        else
            return true;
    }

    public boolean hasAdds(){
        if(_adds == null || _adds.size()==0)
            return false;
        else
            return true;
    }

    public String ua(){
        if(TextUtils.isEmpty(_ua))
            return source.ua();
        else
            return _ua;
    }

    public String encode(){
        if(TextUtils.isEmpty(_encode))
            return source.encode();
        else
            return _encode;
    }

    //获取cookies
    public String buildCookies(String url) {
        String cookies = source.cookies();

        SdAttributeList attrs = new SdAttributeList();
        attrs.set("url",url);
        attrs.set("cookies",(cookies == null ? "" : cookies) );

        cookies = cookie.run(source,attrs,cookies);

        if (TextUtils.isEmpty(addCookie) == false) {
            if (TextUtils.isEmpty(cookies)) {
                cookies = addCookie + "; Path=/; Domain=" + URI.create(url).getHost();
            } else {
                cookies = addCookie + "; " + cookies;
            }
        }


        return cookies;
    }

    protected SdNode buildForNode(Element cfg) {
        _isEmpty = (cfg == null);

        if (_isEmpty == false) {

            this.name = cfg.getTagName();//默认为标签名

            NamedNodeMap nnMap = cfg.getAttributes();
            for(int i=0,len=nnMap.getLength(); i<len; i++) {
                Node att = nnMap.item(i);
                attrs.set(att.getNodeName(), att.getNodeValue());
            }

            _dtype  = attrs.getInt("dtype");
            _btype  = attrs.getInt("btype");

            this.key     = attrs.getString("key");
            this.title   = attrs.getString("title");
            this.method  = attrs.getString("method","get");
            this.onParse   = attrs.getString2("onParse","parse");
            this.onParseUrl= attrs.getString2("onParseUrl","parseUrl");

            this.txt     = attrs.getString("txt");//
            this.lib     = attrs.getString("lib");
            this.btn     = attrs.getString("btn");
            this.expr    = attrs.getString("expr");

            this.update  = attrs.getInt("update",0);

            this._encode = attrs.getString("encode");
            this._ua     = attrs.getString("ua");

            //book,section 特有

            this.addCookie  = attrs.getString("addCookie");
            this.addKey     = attrs.getString("addKey");
            this.addPage    = attrs.getInt("addPage");

            buildDynamicProps();


            {
                String temp = attrs.getString("cache");
                if (TextUtils.isEmpty(temp) == false) {
                    int len = temp.length();
                    if (len == 1) {
                        cache = Integer.parseInt(temp);
                    } else if (len > 1) {
                        cache = Integer.parseInt(temp.substring(0, len - 1));

                        String p = temp.substring(len - 1);
                        switch (p) {
                            case "d": cache = cache * 24 * 60 * 60; break;
                            case "h": cache = cache * 60 * 60; break;
                            case "m": cache = cache * 60; break;
                        }
                    }
                }
            }

            if (cfg.hasChildNodes()) {
                _items = new ArrayList<SdNode>();
                _adds  = new ArrayList<SdNode>();

                NodeList list = cfg.getChildNodes();
                for (int i=0,len=list.getLength(); i<len; i++){
                    Node n1 = list.item(i);
                    if(n1.getNodeType()==Node.ELEMENT_NODE) {
                        Element e1 = (Element)n1;
                        String tagName = e1.getTagName();

                        if(tagName.equals("item")) {
                            SdNode temp = SdApi.createNode(source,tagName).buildForItem(e1, this);
                            _items.add(temp);
                        }
                        else if(e1.hasAttributes()){
                            SdNode temp = SdApi.createNode(source, tagName).buildForAdd(e1, this);
                            _adds.add(temp);
                        }else {
                            attrs.set(e1.getTagName(), e1.getTextContent());
                        }
                    }
                }
            }
        }

        OnDidInit();

        return this;
    }

    //item(不继承父节点)
    private SdNode buildForItem(Element cfg, SdNode p) {
        NamedNodeMap nnMap = cfg.getAttributes();
        for(int i=0,len=nnMap.getLength(); i<len; i++) {
            Node att = nnMap.item(i);
            attrs.set(att.getNodeName(), att.getNodeValue());
        }

        this.name    = p.name;

        this.url     = attrs.getValue("url");//

        this.key     = attrs.getString("key");
        this.title   = attrs.getString("title");//可能为null
        this.group   = attrs.getString("group");
        this.txt     = attrs.getString("txt");//
        this.lib     = attrs.getString("lib");
        this.btn     = attrs.getString("btn");
        this.expr    = attrs.getString("expr");
        this.logo    = attrs.getString("logo");
        this._encode = attrs.getString("encode");

        return this;
    }

    //add (不继承父节点)
    private SdNode buildForAdd(Element cfg, SdNode p) { //add不能有自己独立的url //定义为同一个page的数据获取(可能需要多个ajax)
        NamedNodeMap nnMap = cfg.getAttributes();
        for(int i=0,len=nnMap.getLength(); i<len; i++) {
            Node att = nnMap.item(i);
            attrs.set(att.getNodeName(), att.getNodeValue());
        }

        _dtype  = attrs.getInt("dtype");
        _btype  = attrs.getInt("btype");

        this.name = cfg.getTagName();//默认为标签名

        this.title    = attrs.getString("title");//可能为null

        this.key     = attrs.getString("key");
        this.btn     = attrs.getString("btn");
        this.txt     = attrs.getString("txt");//

        this.method  = attrs.getString("method");
        this._encode = attrs.getString("encode");
        this._ua     = attrs.getString("ua");

        buildDynamicProps();

        //--------
        this.onParse   = attrs.getString2("onParse","parse");
        this.onParseUrl= attrs.getString2("onParseUrl","parseUrl");


        return this;
    }

    private void buildDynamicProps(){
        url     = attrs.getValue("url");
        args    = attrs.getValue("args");
        header  = attrs.getValue("header","");
        referer = attrs.getValue("referer");
        cookie  = attrs.getValue("cookie");

        if(source.schema < 2) {
            url.build     = attrs.getString("buildUrl");
            args.build    = attrs.getString("buildArgs");
            header.build  = attrs.getString("buildHeader");
            referer.build = attrs.getString2("buildReferer","buildRef");
            cookie.build  = attrs.getString("buildCookie");
        }
    }

    //
    //=======================================
    //

    public String getArgs(String url, String key, int page) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url",url);
        if(key != null) {
            atts.set("key", key);
        }
        atts.set("page",page+"");

        return this.args.run(source, atts);
    }

    public String getArgs(String url, Map<String,String> data) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url",url);
        if (data != null) {
            atts.set("data", Util.toJson(data));
        }

        return this.args.run(source, atts);
    }

    public String getUrl() {
        SdAttributeList atts = new SdAttributeList();

        return this.url.run(source, atts);
    }

    public String getUrl(String url) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url",url);

        return this.url.run(source, atts, url);
    }

    public String getUrl(String url, String key, Integer page) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url", url);
        if (key != null) {
            atts.set("key", key);
        }
        atts.set("page", page + "");

        return this.url.run(source, atts, url);
    }

    public String getUrl(String url, Map<String,String> data) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url", url);
        if (data != null) {
            atts.set("data", Util.toJson(data));
        }

        return this.url.run(source, atts, url);
    }

    public String getReferer(String url) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url",url);

        return this.referer.run(source, atts, url);
    }

    public String getHeader(String url) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url",url);

        return this.header.run(source, atts);
    }

    public Map<String,String> getFullHeader(String url){
        Map<String,String> list = new HashMap();

        SdApi.buildHttpHeader(this,url,(key,val)->{
            list.put(key,val);
        });

        return list;
    }

    public String parse(String url, String html) {
        if(TextUtils.isEmpty(this.onParse)){
            return html;
        }

        if ("@null".equals(this.onParse)) //如果是@null，说明不需要通过js解析
            return html;
        else {
            SdAttributeList atts = new SdAttributeList();
            atts.set("url", url);
            atts.set("html", html);

            return source.js.callJs(this.onParse, atts);
        }
    }

    protected String parseUrl(String url, String html) {
        SdAttributeList atts = new SdAttributeList();
        atts.set("url",url);
        atts.set("html",html);

        String temp = source.js.callJs(this.onParseUrl, atts);

        if(temp == null)
            return "";
        else
            return temp;
    }

    public boolean isEmptyUrl(){
        return url == null || url.isEmpty();
    }

    public boolean isEmptyHeader(){
        return header == null || header.isEmpty();
    }

}
