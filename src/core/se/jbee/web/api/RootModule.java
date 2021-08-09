package se.jbee.web.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import se.jbee.inject.bootstrap.Bundle;

@Retention(RUNTIME)
@Target(TYPE)
public @interface RootModule {

	Class<? extends Bundle>[] value();
}
