package org.noear.luffy.trick.extend.flexmark.utils;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.noear.solon.XUtil;

import java.util.Arrays;

public class MarkdownUtils {

    public static String markdown2Html(String markdown)  {
        if(XUtil.isEmpty(markdown)){
            return markdown;
        }

        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[] { TablesExtension.create()}));


        Parser parser = Parser.builder(options).build();

        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Document document = parser.parse(markdown);

        String html = renderer.render(document);

        return html;
    }
}
