package webserver.http.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.request.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private static final String DELIMITER_QUERY = "&";
    private static final String DELIMITER_QUERY_PAIR = "=";
    private static final String DELIMITER_HEADER = ": ";
    private static final String DELIMITER_COOKIE = "; ";
    private static final String DELIMITER_COOKIE_PAIR = "=";

    private HttpUtils() {
    }

    public static Map<String, String> parseQueryString(final String queryParamsText) {
        if (StringUtils.isEmpty(queryParamsText)) {
            return new HashMap<>();
        }

        try {
            final String queryParams = URLDecoder.decode(queryParamsText, StandardCharsets.UTF_8.toString());
            return Arrays.stream(queryParams.split(DELIMITER_QUERY))
                    .map(queryParam -> parseKeyValue(queryParam, DELIMITER_QUERY_PAIR))
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static Pair parseHeader(final String header) {
        return parseKeyValue(header, DELIMITER_HEADER);
    }

    public static Map<String, String> parseCookie(final String cookieText) {
        if (StringUtils.isEmpty(cookieText)) {
            return new HashMap<>();
        }

        return Arrays.stream(cookieText.split(DELIMITER_COOKIE))
                .map(cookie -> parseKeyValue(cookie, DELIMITER_COOKIE_PAIR))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private static Pair parseKeyValue(final String text, final String delimiter) {
        String[] split;
        if (StringUtils.isEmpty(text) || (split = text.split(delimiter)).length != 2) {
            log.error(text);
            throw new IllegalArgumentException(text);
        }
        return new Pair(split[0], split[1]);
    }
}
