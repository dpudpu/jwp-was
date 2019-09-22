package webserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.HttpTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {
    private HttpTestClient httpTestClient;

    @BeforeEach
    void setUp() throws IOException {
        httpTestClient = new HttpTestClient(WebServer.DEFAULT_PORT);
    }

    @Test
    void static_파일_요청() {
        final String response = httpTestClient.get()
                .uri("/css/styles.css")
                .exchange();

        assertThat(response).contains("background-color:#e0e0e0;");
    }
}