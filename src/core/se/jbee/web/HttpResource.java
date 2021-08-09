package se.jbee.web;

import java.lang.reflect.Method;

import se.jbee.inject.Injectron;

/**
 * A call site for an HTTP resource that is mapped to a {@link Method}.
 */
public class HttpResource {

	public Object controller;
	public Method resource;
	public String[] httpMethods; //TODO => make this part of the path mapping
	public String[] requiredRoles; //TODO => turn this into a check
	private HttpRequestValue<?>[] httpParams;
	private Injectron<?>[] injectronParams;
	private Object[] args;
}
