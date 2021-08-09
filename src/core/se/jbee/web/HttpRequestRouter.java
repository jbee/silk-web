package se.jbee.web;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

public interface HttpRequestRouter {

	String identify(Method impl);

	/**
	 * Determines which {@link HttpResource} is used by returning its ID.
	 * Usually a path or path pattern is used as ID.
	 *
	 * @param request
	 *            never null
	 * @return the ID for a {@link HttpResource} that should handle the
	 *         request (as given by via a path annotation)
	 */
	String route(HttpServletRequest request);

}
