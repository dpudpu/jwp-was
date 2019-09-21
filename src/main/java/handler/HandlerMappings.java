package handler;

import java.util.HashMap;
import java.util.Map;

public class HandlerMappings {
    public static Map<String, Handler> MAP = new HashMap<>();

    public Handler getHandler(){
        return MAP.get("");
    }
}
