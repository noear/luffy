package org.noear.usopp.extend.flexmark;

import org.noear.solon.XUtil;
import org.noear.solon.annotation.XNote;
import org.noear.usopp.extend.flexmark.utils.MarkdownUtils;

public class eMark {
    @XNote("md格式转为html格式")
    public String mdToHtml(String markdown){
        return mdToHtml(markdown, null);
    }

    @XNote("md格式转为html格式")
    public String mdToHtml(String markdown, String tabReplacer){
        if(XUtil.isNotEmpty(tabReplacer)){
            markdown = markdown.replace("\t",tabReplacer);
        }

        String html = MarkdownUtils.markdown2Html(markdown);

        String html2 = html
                .replace("<li>[ ]", "<li class='task-list-item'><input type=\"checkbox\" disabled=\"\">")
                .replace("<li>[x]","<li class='task-list-item'><input type=\"checkbox\" disabled=\"\" checked=\"\">")
                .replace("\r\n\r\n", "<br/>");

        return  html2;
    }
}
