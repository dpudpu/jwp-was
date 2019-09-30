package webserver.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.Cookie;
import webserver.http.Cookies;
import webserver.http.HttpHeaders;
import webserver.http.HttpVersion;
import webserver.http.request.HttpRequest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static webserver.http.HttpHeaders.LOCATION;
import static webserver.http.HttpHeaders.SET_COOKIE;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    public static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP_1_1;
    private static final String DEFAULT_ERROR_MESSAGE = "";

    private final Map<String, Object> attributes = new HashMap<>();
    private final HttpHeaders headers = new HttpHeaders();
    private final OutputStream out;
    private final Cookies cookies;
    private final HttpRequest httpRequest;
    private StatusLine statusLine;
    private String resource;

    public HttpResponse(final HttpRequest httpRequest, final OutputStream out) {
        this.httpRequest = httpRequest;
        this.out = out;
        this.cookies = new Cookies();
        this.statusLine = new StatusLine();
    }

    public void forward(final String resource) {
        forward(resource, HttpStatus.OK);
    }

    public void forward(final String resource, final HttpStatus httpStatus) {
        this.resource = resource;
        setStatus(httpStatus);
    }

    public void sendRedirect(final String location) {
        sendRedirect(location, HttpStatus.FOUND);
    }

    public void sendRedirect(final String location, final HttpStatus httpStatus) {
        setStatus(httpStatus);
        setHeader(LOCATION, location);
    }

    public void sendError(final HttpStatus httpStatus) {
        sendError(httpStatus, DEFAULT_ERROR_MESSAGE);
    }

    public void sendError(final HttpStatus httpStatus, final String message) {
        setStatus(httpStatus);
    }

    public void write() {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            writeStartLine(dos);
            writeHeader(dos);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void write(final byte[] body) {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            writeStartLine(dos);
            writeHeader(dos);
            writeBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private void writeStartLine(final DataOutputStream dos) throws IOException {
        final HttpVersion httpVersion = statusLine.getHttpVersion();
        final HttpStatus httpStatus = statusLine.getHttpStatus();
        dos.writeBytes(String.format("%s %s %s\n", httpVersion.getHttpVersion(), httpStatus.getCode(), httpStatus.getPhrase()));
    }

    private void writeHeader(final DataOutputStream dos) throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
        }
        writeSetCookie(dos);
        dos.writeBytes("\n");
    }

    private void writeSetCookie(final DataOutputStream dos) throws IOException {
        if (httpRequest.isCreatedSession()) {
            cookies.add(Cookie.builder(Cookies.JSESSIONID, httpRequest.getSessionId())
                    .path("/")
                    .httpOnly(true)
                    .build());
        }

        for (Cookie cookie : cookies.values()) {
            dos.writeBytes(String.format("%s: %s\n", SET_COOKIE, cookie.parseInfoAsString()));
        }
    }

    private void writeBody(final DataOutputStream dos, final byte[] body) throws IOException {
        if (body != null) {
            dos.write(body, 0, body.length);
        }
    }

    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
    }

    public void setAttribute(final String key, final Object value) {
        attributes.put(key, value);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void setHttpVersion(final HttpVersion httpVersion) {
        statusLine.setHttpVersion(httpVersion);
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void setStatus(final HttpStatus httpStatus) {
        statusLine.setHttpStatus(httpStatus);
    }

    public String getResource() {
        return resource;
    }

    public HttpStatus getHttpStatus() {
        return statusLine.getHttpStatus();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public HttpVersion getHttpVersion() {
        return statusLine.getHttpVersion();
    }
}
