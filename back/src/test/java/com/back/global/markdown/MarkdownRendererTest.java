package com.back.global.markdown;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownRendererTest {

    private final MarkdownRenderer renderer = new MarkdownRenderer();

    @Test
    void 기본_마크다운을_HTML로_변환한다() {
        String html = renderer.render("# 제목\n\n**굵게** 그리고 _기울임_");

        assertThat(html).contains("<h1>제목</h1>");
        assertThat(html).contains("<strong>굵게</strong>");
        assertThat(html).contains("<em>기울임</em>");
    }

    @Test
    void 리스트를_변환한다() {
        String html = renderer.render("- 하나\n- 둘");

        assertThat(html).contains("<ul>");
        assertThat(html).contains("<li>하나</li>");
    }

    @Test
    void script_태그를_제거한다() {
        String html = renderer.render("정상 텍스트\n\n<script>alert('xss')</script>");

        assertThat(html).doesNotContain("<script>");
        assertThat(html).contains("정상 텍스트");
    }

    @Test
    void 위험한_속성을_제거한다() {
        String html = renderer.render("<img src=\"x\" onerror=\"alert(1)\">");

        assertThat(html).doesNotContain("onerror");
    }

    @Test
    void null과_공백은_빈문자열을_반환한다() {
        assertThat(renderer.render(null)).isEmpty();
        assertThat(renderer.render("   ")).isEmpty();
    }
}
