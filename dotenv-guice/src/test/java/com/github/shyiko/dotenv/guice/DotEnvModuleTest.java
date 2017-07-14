package com.github.shyiko.dotenv.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class DotEnvModuleTest {

    static class S {
        private final String scheme;
        private final String host;
        private final int port;

        @Inject
        S(
            @DotEnvValue("SCHEME") String scheme,
            @DotEnvValue("HOST") String host,
            @DotEnvValue("PORT") int port
        ) {
            this.scheme = scheme;
            this.host = host;
            this.port = port;
        }

        String run() {
            return scheme + "://" + host + ":" + port;
        }
    }

    @Test
    public void testInjection() {
        Injector injector = Guice.createInjector(new DotEnvModule());
        S s = injector.getInstance(S.class);
        assertThat(s.run()).isEqualTo("http://localhost:8080");
    }

}
