package org.noear.luffy.trick.extend.flexmark;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.luffy.trick.extend.flexmark.utils.MarkdownUtils;

public class eMark {
    @Note("md格式转为html格式")
    public String mdToHtml(String markdown){
        return mdToHtml(markdown, null);
    }

    @Note("md格式转为html格式")
    public String mdToHtml(String markdown, String tabReplacer){
        if(Utils.isNotEmpty(tabReplacer)){
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
