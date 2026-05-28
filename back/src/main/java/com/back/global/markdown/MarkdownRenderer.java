package com.back.global.markdown;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * 마크다운 원본을 안전한 HTML로 변환한다.
 * commonmark로 HTML을 만들고 Jsoup Safelist로 정화해 XSS를 차단한다. (ADR 0001)
 */
@Component
public class MarkdownRenderer {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
    private final Safelist safelist = Safelist.relaxed()
            .addAttributes("a", "rel")
            .addProtocols("a", "href", "http", "https", "mailto");

    public String render(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }

        String unsafeHtml = htmlRenderer.render(parser.parse(markdown));
        return Jsoup.clean(unsafeHtml, safelist);
    }
}
