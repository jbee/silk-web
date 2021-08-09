package se.jbee.web;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface HttpRequestValue<T> {

	/**
	 *
	 * @param from the {@link HttpServletRequest} to extract the mapped value from
	 * @return the extracted and converted value to pass as parameter to the implementing method
	 */
	T extractValue(HttpServletRequest from);

}
