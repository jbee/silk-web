package se.jbee.web;

import java.lang.reflect.Method;

import se.jbee.inject.Type;

/**
 * A {@link HttpRequestMapping} is a strategy to extract a mapping information from a {@link Method}.
 */
@FunctionalInterface
public interface HttpRequestMapping {

	/**
	 *
	 * @param impl a candidate for a mapped HTTP resource (controller method)
	 * @param paramIndex which method parameter is mapped
	 * @return null in case this mapping does not apply, or a instance of a {@link HttpRequestValue} that can extract the target value for the parameter
	 */
	<T> HttpRequestValue<T> mapToValue(Method impl, int paramIndex, Type<T> paramType, Converters converters);
}
