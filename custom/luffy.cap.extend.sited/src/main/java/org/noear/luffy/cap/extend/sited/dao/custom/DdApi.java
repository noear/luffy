package org.noear.luffy.cap.extend.sited.dao.custom;


import org.noear.luffy.cap.extend.sited.dao.engine.SdApi;
import org.noear.luffy.cap.extend.sited.utils.Base64Util;

import java.nio.charset.Charset;

/**
 * Created by yuety on 16/2/1.
 */
public class DdApi extends SdApi {
    /*
    * v36: 增加level（对用户等级限制的支持）
    * v35: 更换检查 referer 和 cookie 条件的方案;
    *    : 增加schame=2 的新结构
    *    : 增加buildReferer,替代: buildRef
    *    : 增加onParse 替代: parse
    *    : 增加onParseUrl 替代: parseUrl
    *    : 增加onCheck 替代: check
    *    : //on 开头，说明默认是js函数 //旧的仍可使用
    * v34: 使用新的header格式：key:val $$ key:val $$ key:val （旧的：key=val; key=vak;）
    *
    * v33: 增加WEB::url播放支持
    * v32: dtype[8] 增加通过 ss 控制是否使用section节点打开
    * v31: 增加 subtag 节点（子分类）
    *
    *
    * v30: Add增加parseUrl(支持CALL::),Search,Tag增加Add
    * v29: 为插件提供存储能力SiteD.get(key);SiteD.set(key,val);
    * v28: 增加dtype=8;;;增加btn,key;;;支持有item+parse的组合
    *
    *
    * v27: 支持新的插件格式架构
    *    : 增加 meta.contact
    * v26: 增加showNav,donwAll控制属性；
    *    : section[2]支持t=10（原始大小图片，居中）；支持只有group的Item
    *    : 增加btype; dtag 改为 btag
    *    : 4,5,6,7，增加{name,logo,list:[...]}格式，确可能有name 和 logo
    *    : 支付 search 结果跳转到tag
    * v25: 增加@style
    *    : dtype=3、7时，section/book.parse 增加支持：[{url,type,mime,logo}] 格式 //logo可做为图标
    *    : section[1]、book[4].parse 增加支持：{bg,list["",""]}、{bg,list[{url,time}]}格式
    *    : method 支持 @null（表示不用请求http）
    * v24: @parseUrl 支持返回 CALL::url 和 url 的自由组合;CALL的请求结果，仍由parseUrl处理；
    *    : 为SiteD注入新的uid,usex等信息
    *    : dtype=3、7时，section/book.parse 增加支持返回：[{url,type,mime}] 格式 //可更好支持下载
    * v23: 增加 reward 打赏节点；增加@txt属性
    * v22: hots, updates, tag ，全部支持showImg=0,1,2；并都支持w,h属性
    * v21: 支持302跳转url的获取;
    * v20: 插件主页热门、更新，支持分类跳转（提供更自由的数据组合）
    *    : book,section数据支持sited://格式跳转（可提供插件中心作为插件）
    *    : 支持无图的tag内容样式(showImg=0)
    *    : 支持有图的update内容样式(showImg=1)
    *    : 完善vip限制
    *    : 支持updates通过item配置,增加logo的数据接收
    *    : 增加bookViewModel.isSectionsAsc输出
    * v19: 完成插件WEB登录功能，并验证
    * v18: 小说插件输出改为：[{d:'',t:1},...]格式
    * v17: 增加object节点配置
    * v16: 增加buildCookie;增加处理分支；增加loop属性
    * v15: 增加login、trace支持;;;协加无目录视频插件支持（dtype=7）;;调整节点插件的处理方式
    * v14: 视插插件，支持：web://
    * v13: 架构调整：1.将节点与属性打通；2.分离成内核与业务定制2部分；3.抽象化主节点（即可以随意定名字）
    * v12: 增加多数据块的加载支持
    * v11: 优化周边插件（与阿里百川sdk进一步整合）
    * v10: 增加图片、资讯、周边插件
    * v9 : 增加对#hash的过滤处理；增加hasMicroDefine()
    * v8 : 增加parseUrl()->"xxx;xxxx;xxx"的支持
    * */
    public static int version(){
        return 36;
    }

    public static String unsuan(String str, String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = str.length(); i < len; i++) {
            if (i % 2 == 0) {
                sb.append(str.charAt(i));
            }
        }

        str = sb.toString();
        str = Base64Util.decode(str);
        key = key + "ro4w78Jx";

        Charset coder = Charset.forName("UTF-8");

        byte[] data = str.getBytes(coder);
        byte[] keyData = key.getBytes(coder);
        int keyIndex = 0;

        for (int x = 0; x < data.length; x++) {
            data[x] = (byte)(data[x] ^ keyData[keyIndex]);
            keyIndex += 1;

            if (keyIndex == keyData.length) {
                keyIndex = 0;
            }
        }
        str = new String(data,coder);

        return Base64Util.decode(str);
    }


    public static String dtypeName(int dtype){
        switch (dtype){
            case 2: return "轻小说";
            case 3: return "动画";
            case 4: return "图片";
            case 5: return "周边";
            case 6: return "资讯";
            case 7: return "视频";
            case 8: return "小工具";
            default: return "漫画";
        }
    }
}
