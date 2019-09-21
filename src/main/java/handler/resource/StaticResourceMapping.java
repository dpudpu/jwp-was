package handler.resource;

import http.MimeType;

import java.util.HashMap;
import java.util.Map;

public class StaticResourceMapping {
    public final Map<String, StaticResource> resources;

    private StaticResourceMapping() {
        resources = new HashMap<>();

        for (final String extension : MimeType.getExtensions()) {
            resources.put(extension, StaticResource.of(extension));
        }
        resources.put("htm", StaticResource.of("htm", "/templates"));
        resources.put("html", StaticResource.of("html", "/templates"));
    }

    public void addStaticResource(final String extension, final String location) {
        resources.put(extension, StaticResource.of(extension, location));
    }

    public boolean contains(final String extension) {
        return resources.containsKey(extension);
    }

    public void setAllLocations(final String location) {
        for (final String key : resources.keySet()) {
            resources.get(key).changeLocation(location);
        }
    }

    public String getLocation(final String extension) {
        final StaticResource staticResource = resources.get(extension);
        return staticResource.getLocation();
    }

    public static StaticResourceMapping getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static class LazyHolder {
        public static final StaticResourceMapping INSTANCE = new StaticResourceMapping();
    }
}
