package se.jbee.web;

import se.jbee.inject.Type;

public interface Converters {

	<A, B> Converter<A, B> yield(Type<A> from, Type<B> to);

}
