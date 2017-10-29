# dotenv [![Build Status](https://travis-ci.org/shyiko/dotenv.svg?branch=master)](https://travis-ci.org/shyiko/dotenv) [![Maven Central](https://img.shields.io/maven-central/v/com.github.shyiko.dotenv/dotenv.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.shyiko.dotenv%22%20AND%20a%3A%22dotenv%22)

A [twelve-factor configuration](https://12factor.net/config) library for Java 8+.

Features:
- [seamless integration with Guice](https://github.com/shyiko/dotenv#integration-with-guice) (prefer Spring? see [here](https://github.com/shyiko/dotenv#integration-with-spring));
- zero dependencies;
- available in Maven Central.

## Usage

```xml
<dependency>
    <groupId>com.github.shyiko.dotenv</groupId>
    <artifactId>dotenv</artifactId>
    <version>0.1.1</version>
</dependency>
```

ENV resolution order (sources higher in the list take precedence over those located lower):
- System.getenv() 
- `.env` file in current working directory (might not exist)
- `.env` file on the classpath

> (java example)

Let's assume you have a jar file (say `app.jar`) which contains .env file (e.g. `printf "SCHEME=http\nHOST=localhost\n# comment\nPORT=8080" > src/main/resources/.env`) and Main.java with the following `public static void main(...)`:   

```java
Map<String, String> dotEnv = DotEnv.load();
System.out.println(dotEnv.get("SCHEME") + "://" + dotEnv.get("HOST") + ":" + dotEnv.get("PORT"))
```

Executing the following

```sh
printf "HOST=0.0.0.0" > .env
PORT=5050 app.jar
```

will then output `http://0.0.0.0:5050`

### Integration with [Guice](https://github.com/google/guice)

```xml
<dependency>
    <groupId>com.github.shyiko.dotenv</groupId>
    <artifactId>dotenv-guice</artifactId>
    <version>0.1.1</version>
</dependency>
```

> (Main.java)

```java
static class S {
    private final String scheme;
    private final String host;
    private final int port;

    @Inject
    public S(
        @DotEnvValue("SCHEME") String scheme, 
        @DotEnvValue("HOST") String host, 
        @DotEnvValue("PORT") int port
    ) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
    }

    public void run() {
        System.out.println(scheme + "://" + host + ":" + port);
    }
}

public static void main(String[] args) {
    Injector injector = Guice.createInjector(new DotEnvModule()/*, ... */);
    S s = injector.getInstance(S.class);
    s.run();
}
```

### Integration with [Spring](https://spring.io/)

> (Main.java)

```java
static class S {
    private final String scheme;
    private final String host;
    private final int port;

    @Autowired
    public S(
        @Value("${SCHEME}") String scheme, 
        @Value("${HOST}") String host, 
        @Value("${PORT}") int port
    ) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
    }

    public void run() {
        System.out.println(scheme + "://" + host + ":" + port);
    }
}

public static void main(String[] args) {
    ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.getEnvironment().getPropertySources().addFirst(
        new MapPropertySource("dotenv", new HashMap<>(DotEnv.load()))
    );
    ctx.refresh();
    S s = ctx.getBeanFactory().createBean(S.class);
    s.run();
}
```

## Development

```sh
git clone https://github.com/shyiko/dotenv && cd dotenv
./mvnw # shows how to build, test, etc. project
```

## Legal

All code, unless specified otherwise, is licensed under the [MIT](https://opensource.org/licenses/MIT) license.  
Copyright (c) 2017 Stanley Shyiko.
