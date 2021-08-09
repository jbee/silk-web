package se.jbee.web;

import static se.jbee.inject.Dependency.dependency;
import static se.jbee.inject.Dependency.pluginsFor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import se.jbee.inject.Injector;
import se.jbee.inject.Name;
import se.jbee.inject.UnresolvableDependency;
import se.jbee.inject.bootstrap.Bootstrap;
import se.jbee.inject.bootstrap.Bundle;
import se.jbee.web.api.RootModule;

public class ServletInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> handlesTypes, ServletContext ctx) throws ServletException {
		try {
			Class<?> web = Class.forName("web");
			Class<? extends Bundle>[] rootBundles = web.getAnnotation(RootModule.class).value();
			for (Class<? extends Bundle> rootBundle : rootBundles) {
				addServlet(ctx, rootBundle);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private static void addServlet(ServletContext ctx, Class<? extends Bundle> rootBundle) {
		Injector injector = Bootstrap.injector(rootBundle);
		Map<String, HttpResource> methods = new HashMap<>();
		Class<?>[] controllers = injector.resolve(pluginsFor(WebModule.class));
		Map<Class<?>, HttpRequestMapping> pathMappings = mappingsByOrigin(HttpRequestMapping.PATH, injector);
		Map<Class<?>, HttpRequestMapping> pathVarMappings = mappingsByOrigin(HttpRequestMapping.PATH_VAR, injector);
		Map<Class<?>, HttpRequestMapping> paramMappings = mappingsByOrigin(HttpRequestMapping.PARAM, injector);
		Map<Class<?>, HttpRequestMapping> headerMappings = mappingsByOrigin(HttpRequestMapping.HEADER, injector);
		Map<Class<?>, HttpRequestMapping> cookieMappings = mappingsByOrigin(HttpRequestMapping.COOKIE, injector);
		Map<Class<?>, HttpRequestMapping> attrMapping = mappingsByOrigin(HttpRequestMapping.ATTRIBUTE, injector);
		Map<Class<?>, HttpRequestMapping> rolesMapping = mappingsByOrigin(HttpRequestMapping.ROLES, injector);
		Map<Class<?>, HttpRequestMapping> methodsMapping = mappingsByOrigin(HttpRequestMapping.METHODS, injector);
		for (Class<?> controllerType : controllers) {
			Object controller = injector.resolve(controllerType);
			for (Method impl : controllerType.getDeclaredMethods()) {
				String[] path = mappedValues(impl, impl.getDeclaredAnnotations(), pathMappings);
				if (path.length > 0) {
					//TODO this is a mapped method, create a mapping for each path
				}
			}
		}
		String servletName = injector.resolve(dependency(WebModule.SERVLET_NAME).injectingInto(ServletInitializer.class));
		String contextPath = injector.resolve(dependency(WebModule.CONTEXT_PATH).injectingInto(ServletInitializer.class));

		HttpRequestRouter mapper = resolveOrDefault(injector, Name.WILDCARD, HttpRequestRouter.class, null);
		if (mapper == null) {
			mapper = new PatternBasedHttpRequestRouter(methods.keySet().toArray(new String[0]));
		}
		ctx.getContext(contextPath).addServlet(servletName, new DispatcherServlet(mapper, methods));
	}

	private static String[] mappedValues(Method impl, Annotation[] annotations, Map<Class<?>, HttpRequestMapping> properties) {
		for (Entry<Class<?>, HttpRequestMapping> e : properties.entrySet()) {
			if (e.getKey().isAnnotation()) {
				String[] values = e.getValue().extract(impl, annotations);
				if (values != null)
					return values;
			}
		}
		HttpRequestMapping generic = properties.get(Method.class);
		return generic != null ? generic.extract(impl, annotations) : new String[0];
	}

	private static Map<Class<?>, HttpRequestMapping> mappingsByOrigin(String property, Injector injector) {
		Map<Class<?>, HttpRequestMapping> res = new IdentityHashMap<>();
		for (Class<?> a : injector.resolve(pluginsFor(Annotation.class, property)))
			res.put(a, injector.resolve(a.getCanonicalName(), HttpRequestMapping.class));
		HttpRequestMapping generic = resolveOrDefault(injector, property, HttpRequestMapping.class, null);
		if (generic != null) {
			res.put(Method.class, generic); // this one is not annotation based
		}
		return res;
	}

	private static <T> T resolveOrDefault(Injector injector, String name, Class<T> type, T defaultValue) {
		try {
			return injector.resolve(name, type);
		} catch (UnresolvableDependency e) {
			return defaultValue;
		}
	}

}
