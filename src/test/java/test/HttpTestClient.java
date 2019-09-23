package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.WebServer;
import webserver.http.HttpStatus;
import webserver.http.request.HttpMethod;
import webserver.http.request.HttpVersion;
import webserver.http.request.Pair;
import webserver.http.utils.HttpUtils;
import webserver.http.utils.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static webserver.http.HttpHeaders.HOST;
import static webserver.http.response.HttpResponse.DEFAULT_HTTP_VERSION;

public class HttpTestClient {
    private static final Logger log = LoggerFactory.getLogger(HttpTestClient.class);

    private static final String LOCALHOST = "localhost";

    private final BufferedReader in;
    private final PrintWriter out;
    private final int port;

    public HttpTestClient(int port) {
        this(LOCALHOST, port);
    }

    public HttpTestClient(String host, int port) {
        try {
            this.port = port;
            WebServer webServer = new WebServer(port);
            Thread thread = new Thread(webServer);
            thread.start();

            Socket socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public String send(String request) {
        out.print(request);
        out.flush();

        List<String> lines = in.lines().collect(Collectors.toList());
        return String.join("\n", lines);
    }

    public HttpRequestBuilder get() {
        return new HttpRequestBuilder(HttpMethod.GET.name());
    }

    public HttpRequestBuilder post() {
        return new HttpRequestBuilder(HttpMethod.POST.name());
    }

    public HttpRequestBuilder put() {
        return new HttpRequestBuilder(HttpMethod.PUT.name());
    }

    public HttpRequestBuilder delete() {
        return new HttpRequestBuilder(HttpMethod.DELETE.name());
    }

    public class HttpRequestBuilder {
        private String method;
        private String uri;
        private String protocolVersion;
        private Map<String, String> headers = new HashMap<>();
        private String body;

        HttpRequestBuilder(String method) {
            this.method = method;
            this.protocolVersion = DEFAULT_HTTP_VERSION.getHttpVersion();
            headers.put(HOST, "localhost:" + port);
        }

        public HttpRequestBuilder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public HttpRequestBuilder protocolVersion(String protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public HttpRequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequestBuilder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public ResponseSpec exchange() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s %s %s\n", method, uri, protocolVersion));
            for (final String key : headers.keySet()) {
                sb.append(String.format("%s: %s\n", key, headers.get(key)));
            }
            sb.append("\n");
            sb.append(body);

            log.debug("\n" + sb.toString());
            final String response = send(sb.toString());
            return new ResponseSpec(response);
        }
    }

    // TODO 리팩토링 많이 필요!
    public class ResponseSpec {
        private final HttpVersion httpVersion;
        private final HttpStatus httpStatus;
        private final Map<String, String> headers = new HashMap<>();
        private final String body;

        public ResponseSpec(final String response) {
            final String[] split = response.split("\n");

            final String[] responseLine = split[0].split(" ");
            this.httpVersion = HttpVersion.of(responseLine[0]);
            this.httpStatus = HttpStatus.from(responseLine[1]);

            int i = 1;
            for (String line = split[i]; StringUtils.isNotEmpty(line) && i < split.length; line = split[i++]) {
                final Pair pair = HttpUtils.parseHeader(line);
                headers.put(pair.getKey(), pair.getValue());
            }

            final StringBuilder sb = new StringBuilder();
            while (i < split.length) {
                sb.append(split[i]);
                sb.append("\n");
                i++;
            }
            body = sb.toString();
        }

        public ResponseSpec matchHttpVersion(final HttpVersion expected) {
            assertThat(httpVersion).isEqualTo(expected);
            return this;
        }

        public ResponseSpec matchHttpStatus(final HttpStatus expected) {
            assertThat(httpStatus).isEqualTo(expected);
            return this;
        }

        public ResponseSpec matchHeader(final String key, final String value) {
            assertThat(headers.get(key)).isEqualTo(value);
            return this;
        }

        public ResponseSpec containsBody(final String body) {
            assertThat(this.body.contains(body)).isTrue();
            return this;
        }
    }
}
