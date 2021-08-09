package se.jbee.web;

import static se.jbee.inject.Instance.instance;
import static se.jbee.inject.Name.named;
import static se.jbee.inject.Source.source;
import static se.jbee.inject.Type.raw;
import static se.jbee.inject.container.Scoped.APPLICATION;

import java.lang.annotation.Annotation;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import se.jbee.inject.Instance;
import se.jbee.inject.Name;
import se.jbee.inject.bind.Bind;
import se.jbee.inject.bind.Binder;
import se.jbee.inject.bind.Binder.RootBinder;
import se.jbee.inject.bind.Binder.TargetedBinder;
import se.jbee.inject.bind.Binder.TypedBinder;
import se.jbee.inject.bind.BinderModule;
import se.jbee.inject.bootstrap.Bindings;
import se.jbee.inject.bootstrap.Bootstrapper;
import se.jbee.inject.bootstrap.Bundle;
import se.jbee.inject.bootstrap.Module;

/**
 * Provides convenience methods to configure the dispatch and mapping of web
 * methods.
 *
 * The contract is based on conventions - this means normal
 * {@link BinderModule}s can likewise bind these.
 */
public abstract class WebModule implements Bundle, Module {

	private static final String DEFAULT_VALUE_PREFIX = "defaultValue@";
	public static final Instance<String> CONTEXT_PATH = property("CONTEXT_PATH");
	public static final Instance<String> SERVLET_NAME = property("SERVLET_NAME");

	private RootBinder binder;
	private TargetedBinder webConfig;

	@Override
	public final void bootstrap( Bootstrapper bootstrap ) {
		bootstrap.install( this );
	}

	@Override
	public final void declare(Bindings bindings) {
		binder = Binder.create(Bind.create(bindings, source(getClass()), APPLICATION));
		webConfig = binder.injectingInto(ServletInitializer.class);
		configure();
	}

	/**
	 * Declare the web configuration in this method by using the utility method
	 * {@link #controller(Class...)} to specify the classes that implement a
	 * controller and further methods {@link #path(Class, Function)},
	 * {@link #param(Class, Function)}, {@link #header(Class, Function)} etcetera to
	 * describe how to read annotations to extract hints where to use what HTTP
	 * request information.
	 */
	protected abstract void configure();

	protected final void controller(Class<?>... controllers) {
		for (Class<?> c : controllers)
			binder.plug(c).into(WebModule.class);
	}

	protected final <T extends Annotation> void path(Class<T> annotation, Function<T, String> path) {
		annotatedMapping(annotation, path, (p, request) -> request.getServletPath());
	}

	protected final <T extends Annotation> void pathVariable(Class<T> annotation, Function<T, String> name) {
		annotatedMapping(HttpRequestMapping.PATH_VAR, annotation, name);
	}

	protected final <T extends Annotation> void param(Class<T> annotation, Function<T, String> name) {
		annotatedMapping(annotation, name, (param, req) -> req.getParameter(param));
	}

	protected final <T extends Annotation> void header(Class<T> annotation, Function<T, String> name) {
		annotatedMapping(annotation, name, (header, req) -> req.getHeader(header));
	}

	protected final <T extends Annotation> void cookie(Class<T> annotation, Function<T, String> name) {
		annotatedMapping(annotation, name, (cookie, req) -> cookieValue(req.getCookies(), cookie));
	}

	static String cookieValue(Cookie[] cookies, String name) {
		for (Cookie c : cookies)
			if (name.equals(c.getName()))
				return c.getValue();
		return null;
	}

	protected final <T extends Annotation> void attr(Class<T> annotation, Function<T, String> name) {
		annotatedMapping(annotation, name, (attr, req) -> req.getAttribute(attr));
	}

	protected final <T extends Annotation> void body(Class<T> annotation, Function<T, String> name) {
		annotatedMapping(HttpRequestMapping.ATTRIBUTE, annotation, name);
	}

	/**
	 * Configures the supported HTTP method via the given annotation.
	 *
	 * @param annotation annotation type used
	 * @param method function that extracts the supported HTTP method name from an annotation instance
	 */
	protected final <T extends Annotation> void method(Class<T> annotation, Function<T, String> method) {
		annotatedMapping(HttpRequestMapping.METHODS, annotation, method);
	}

	/**
	 * Configures the supported HTTP methods via the given annotation.
	 *
	 * @param annotation annotation type used
	 * @param methods function that extracts the supported HTTP method names from an annotation instance
	 */
	protected final <T extends Annotation> void methods(Class<T> annotation, Function<T, String[]> methods) {
		annotatedSetMapping(HttpRequestMapping.METHODS, annotation, methods);
	}

	protected final <T extends Annotation> void role(Class<T> annotation, Function<T, String> role) {
		annotatedMapping(annotation, role, (r, req) -> "");
	}

	protected final <T extends Annotation> void roles(Class<T> annotation, Function<T, String[]> roles) {
		annotatedSetMapping(HttpRequestMapping.ROLES, annotation, roles);
	}

	// param (and others) could also be derived from path + method signature by using vars in path and same argument (by index)

	protected final <T> TypedBinder<T> defaultValue(String param, Class<T> type) {
		return binder.bind(defaultValue(param), type);
	}

	protected final <T> TypedBinder<T> defaultValue(Class<?> controller, String param, Class<T> type) {
		return binder.injectingInto(controller).bind(defaultValue(param), type);
	}

	private final <A extends Annotation, T> void annotatedMapping(Class<A> type, Function<A, T> field, BiFunction<T, HttpServletRequest, String> value) {
//		annotatedSetMapping(property, type, m -> {
//			String value = extractor.apply(m);
//			return value == null ? new String[0] : new String[] { value };
//		});
	}

	@SuppressWarnings("unchecked")
	private final <T extends Annotation> void annotatedSetMapping(String property, Class<T> type, Function<T, String[]> extractor) {
//		binder.plug(type).into(Annotation.class, property);
//		mappingFor(type.getCanonicalName(), (__, annotations) -> {
//			if (annotations.length == 0)
//				return null;
//			List<String> res = new ArrayList<>();
//			for (Annotation a : annotations) {
//				if (a.getClass() == type)
//					for (String v : extractor.apply((T)a))
//						res.add(v);
//			}
//			return res.size() == 0 ? null : res.toArray(new String[0]);
//		});
	}

	private final void mappingFor(String name, HttpRequestMapping func) {
		binder.bind(named(name), HttpRequestMapping.class).to(func);
	}

	protected final void servletName(String name) {
		webConfig.bind(SERVLET_NAME).to(name);
	}

	protected final void contextPath(String path) {
		webConfig.bind(CONTEXT_PATH).to(path);
	}

	public static Instance<String> property(String name) {
		return instance(named(name), raw(String.class));
	}

	public static Name defaultValue(String param) {
		return named(DEFAULT_VALUE_PREFIX + param);
	}

}
