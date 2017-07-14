package com.github.shyiko.dotenv;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public final class DotEnv {

    private DotEnv() {}

    public static Map<String, String> load() { return load(DotEnv.class.getClassLoader()); }

    public static Map<String, String> load(ClassLoader classLoader) {
        final Map<String, String> map = new HashMap<>();
        map.putAll(parseBufferedIfNotNull(classLoader.getResourceAsStream(".env")));
        try {
            map.putAll(parseBufferedIfNotNull(new FileInputStream(".env")));
        } catch (FileNotFoundException e) { /* perfectly normal */ }
        map.putAll(System.getenv());
        return Collections.unmodifiableMap(map);
    }

    private static Map<String, String> parseBufferedIfNotNull(InputStream inputStream) {
        return inputStream != null ? parse(new BufferedInputStream(inputStream)) : Collections.emptyMap();
    }

    private static Map<String, String> parse(InputStream inputStream) {
        Properties properties = new Properties();
        try { properties.load(inputStream); } catch (IOException e) { throw new UncheckedIOException(e); }
        return properties.entrySet().stream().collect(Collectors.toMap(
            entry -> entry.getKey().toString(), entry -> entry.getValue().toString()
        ));
    }

}
