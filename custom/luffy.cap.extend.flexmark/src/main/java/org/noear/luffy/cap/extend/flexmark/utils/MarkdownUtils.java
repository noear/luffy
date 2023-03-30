package org.noear.luffy.cap.extend.flexmark.utils;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacterExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.builder.Extension;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.noear.solon.Utils;

import java.util.Arrays;

public class MarkdownUtils {

    public static String markdown2Html(String markdown) {
        if (Utils.isEmpty(markdown)) {
            return markdown;
        }

        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.SPACE_IN_LINK_URLS, true);
        options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[]{
                TablesExtension.create(),
                TaskListExtension.create(),
                DefinitionExtension.create(),
                AutolinkExtension.create(),
                StrikethroughExtension.create(),
                EscapedCharacterExtension.create()}));


        Parser parser = Parser.builder(options).build();

        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Document document = parser.parse(markdown);

        String html = renderer.render(document);

        return html;
    }
}
