package handler;

import http.HttpRequest;
import http.HttpResponse;

public interface Handler {
    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
    void isMapping(final String path);
}
