package com.github.shyiko.dotenv.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.github.shyiko.dotenv.DotEnv;

import java.util.Map;

public class DotEnvModule extends AbstractModule {

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    public static final TriConsumer<Binder, DotEnvValue, String> DEFAULT_BINDER =
        (binder, annotation, value) -> {
            binder.bind(Key.get(String.class, annotation)).toInstance(value);
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                binder.bind(Key.get(Boolean.class, annotation)).toInstance(Boolean.parseBoolean(value));
            }
            try {
                int v = Integer.parseInt(value);
                binder.bind(Key.get(Integer.class, annotation)).toInstance(v);
            } catch (NumberFormatException e) { /* it's fine */ }
            try {
                long v = Long.parseLong(value);
                binder.bind(Key.get(Long.class, annotation)).toInstance(v);
            } catch (NumberFormatException e) { /* it's fine */ }
        };

    private final Map<String, String> env;
    private final TriConsumer<Binder, DotEnvValue, String> binder;

    public DotEnvModule() {
        this(DotEnv.load(), DEFAULT_BINDER);
    }

    public DotEnvModule(TriConsumer<Binder, DotEnvValue, String> binder) {
        this(DotEnv.load(), binder);
    }

    public DotEnvModule(Map<String, String> env) {
        this(env, DEFAULT_BINDER);
    }

    public DotEnvModule(Map<String, String> env, TriConsumer<Binder, DotEnvValue, String> binder) {
        this.env = env;
        this.binder = binder;
    }

    @Override
    protected void configure() {
        this.env.forEach((key, value) -> binder.accept(this.binder(), new DotEnvValueImpl(key), value));
    }
}
