package com.github.shyiko.dotenv.guice;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
class DotEnvValueImpl implements DotEnvValue {

    private final String value;

    DotEnvValueImpl(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public int hashCode() {
        return (127 * "value".hashCode()) ^ value.hashCode(); // per java.lang.Annotation::hashCode contract
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DotEnvValue && value.equals(((DotEnvValue) o).value());
    }

    @Override
    public String toString() {
        return "@" + DotEnvValue.class.getName() + "(value=" + value + ")";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return DotEnvValue.class;
    }
}
