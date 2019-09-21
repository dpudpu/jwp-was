package handler.resource;

import handler.Handler;
import http.HttpRequest;
import http.HttpResponse;

public class StaticResourceHandler implements Handler {
    private StaticResourceMapping staticResourceMapping;

    public StaticResourceHandler(final StaticResourceMapping staticResourceMapping) {
        this.staticResourceMapping = staticResourceMapping;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String resource = httpRequest.getPath();
        String extension = resource.substring(resource.lastIndexOf(".") + 1);
        resource += staticResourceMapping.getLocation(extension);

        httpResponse.forward(resource);
    }

    public boolean isMapping(final String extension) {
        return staticResourceMapping.contains(extension);
    }

}