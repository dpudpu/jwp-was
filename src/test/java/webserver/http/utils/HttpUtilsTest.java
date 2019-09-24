package webserver.http.utils;

import org.junit.jupiter.api.Test;
import webserver.http.request.Pair;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webserver.http.HttpHeaders.CONNECTION;

class HttpUtilsTest {

    @Test
    void parseQueryStringTest() {
        // given & when
        final Map<String, String> parameters = HttpUtils.parseQueryString("userId=javajigi&password=password&email=javajigi%40slipp.net");

        // then
        assertThat(parameters).hasSize(3)
                .containsEntry("userId", "javajigi")
                .containsEntry("password", "password")
                .containsEntry("email", "javajigi@slipp.net");
    }

    @Test
    void queryString_빈값_일경우() {
        // given & when
        final Map<String, String> parameters = HttpUtils.parseQueryString("");

        // then
        assertThat(parameters).hasSize(0);
    }

    @Test
    void parseHeaderTest() {
        // given
        final String header = "Connection: keep-alive";

        // when
        final Pair expected = HttpUtils.parseHeader(header);

        // then
        assertThat(new Pair(CONNECTION, "keep-alive")).isEqualTo(expected);
    }

    @Test
    void 옳바르지_않은_헤더_입력() {
        // given
        final String header = "Connection= keep-alive";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> HttpUtils.parseHeader(header));
    }

    @Test
    void parseCookieTest() {
        // given
        final String cookieText = "JSESSIONID=123; logined=true";

        // when
        final Map<String, String> cookie = HttpUtils.parseCookie(cookieText);

        // then
        assertThat(cookie).hasSize(2)
                .containsEntry("JSESSIONID", "123")
                .containsEntry("logined", "true");
    }

    @Test
    void parseCookie_빈값_넣었을_경우() {
        // given
        final String cookieText = "";

        // when
        final Map<String, String> cookie = HttpUtils.parseCookie(cookieText);

        // then
        assertThat(cookie).isEqualTo(Collections.EMPTY_MAP);
    }
}