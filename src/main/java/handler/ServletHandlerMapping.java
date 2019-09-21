package handler;

import http.Servlet;
import servlet.IndexServlet;
import servlet.SignupServlet;

import java.util.HashMap;
import java.util.Map;

public class ServletHandlerMapping {
    private static final Map<String, Servlet> MAP = new HashMap<>();

    static {
        MAP.put("/", new IndexServlet());
        MAP.put("/signup", new SignupServlet());
    }

    public Servlet getServlet(String path) {
        return MAP.get(path);
    }

    public static ServletHandlerMapping getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static class LazyHolder {
        private static final ServletHandlerMapping INSTANCE = new ServletHandlerMapping();
    }
}
