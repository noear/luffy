package org.noear.luffy.trick.extend.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.noear.solon.annotation.XNote;

public class eDom {
    @XNote("解析为 html 并生成 dom（具体参考：Jsoup 接口）")
    public Document parse(String html) throws Exception{
        return Jsoup.parse(html);
    }

    @XNote("解析为 html 并生成 dom")
    public Document parse(String html, String baseUri) throws Exception{
        return Jsoup.parse(html, baseUri);
    }

    @XNote("生成网络请求，为解析做准备")
    public Connection connect(String url) throws Exception{
        return Jsoup.connect(url);
    }
}
