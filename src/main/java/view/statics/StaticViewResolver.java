package view.statics;

import view.View;
import view.ViewResolver;
import webserver.http.HttpHeaders;
import webserver.http.MimeType;
import webserver.http.response.HttpResponse;
import webserver.http.utils.HttpUtils;

public class StaticViewResolver implements ViewResolver {
    private final StaticResourceMapping mapping;

    public StaticViewResolver(final StaticResourceMapping mapping) {
        this.mapping = mapping;
    }

    public boolean isStaticFile(final String name) {
        return mapping.isMapping(name);
    }

    public View resolveView(final String name, final HttpResponse httpResponse) {
        final String viewName = mapping.addPrefix(name);
        final StaticView view = new StaticView(viewName);
        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MimeType.getType(HttpUtils.parseExtension(viewName)));
        httpResponse.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(view.getContentLength()));
        httpResponse.forward(viewName);
        return view;
    }
}
