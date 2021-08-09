package se.jbee.web;

public interface Converter<A, B> {

	B convert(A from, Converters converters);
}
