package webserver.http.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.http.HttpVersion;
import webserver.http.request.HttpRequest;
import webserver.http.request.HttpRequestFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static webserver.http.HttpHeaders.LOCATION;

class HttpResponseTest {
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        final String plainTextRequest = "POST /user/create?id=1 HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Cookie: user=bedi;\n" +
                "\n";

        final InputStream in = new ByteArrayInputStream(plainTextRequest.getBytes());
        final HttpRequest httpRequest = HttpRequestFactory.generate(in);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        response = new HttpResponse(httpRequest, out);
    }

    @Test
    @DisplayName("forward(resource) 요청")
    void forwardTest() {
        // when
        response.forward("./templates/index.html");

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHttpVersion()).isEqualTo(HttpResponse.DEFAULT_HTTP_VERSION);
    }

    @Test
    @DisplayName("forward(resource, httpStatus) 요청")
    void forwardTest2() {
        // when
        response.forward("./templates/index.html", HttpStatus.CREATED);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("redirect(resource) 요청")
    void redirectTest() {
        // when
        final String location = "/test";
        response.sendRedirect(location);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader(LOCATION)).isEqualTo(location);
    }

    @Test
    @DisplayName("redirect(resource, httpStatus) 요청")
    void redirectTest2() {
        // when
        final String location = "/test";
        response.sendRedirect(location, HttpStatus.NOT_MODIFIED);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(response.getHeader(LOCATION)).isEqualTo(location);
    }

    @Test
    @DisplayName("sendError(httpStatus) 요청")
    void sendErrorTest() {
        // when
        response.sendError(HttpStatus.NOT_FOUND);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("sendError(httpStatus, message) 요청")
    void sendErrorTest2() {
        // when
        final String message = "접근 불가 페이지";
        response.sendError(HttpStatus.FORBIDDEN, message);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("setHttpVersion() 해주면 적용되는지 확인")
    void setHttpVersionTest() {
        // given
        final HttpVersion httpVersion = HttpVersion.HTTP_2_0;
        final String location = "/test";

        // when
        response.setHttpVersion(httpVersion);
        response.sendRedirect(location, HttpStatus.NOT_MODIFIED);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(response.getHeader(LOCATION)).isEqualTo(location);
    }
}