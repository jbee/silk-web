package se.jbee.web;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

/**
 * Finds the longest path ID that matches a request path.
 */
public final class PatternBasedHttpRequestRouter implements HttpRequestRouter {

	private final String[] patterns;

	public PatternBasedHttpRequestRouter(String... pathPatterns) {
		this.patterns = pathPatterns;
		Arrays.sort(pathPatterns, (a,b) -> {
			int res = a.length() - b.length();
			return res != 0 ? res : a.compareTo(b);
		});
	}

	@Override
	public String identify(Method impl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String route(HttpServletRequest request) {
		for (String pattern : patterns) {
			if (matches(pattern, request.getServletPath()))
				return pattern;
		}
		return null;
	}

	private static boolean matches(String pattern, String path) {
		int i = 0; // pattern pos
		int j = 0; // path pos
		int ilen = pattern.length();
		int jlen = path.length();
		while (i>= 0 && j >= 0 && i < ilen && j < jlen) {
			if (pattern.charAt(i) != path.charAt(j)) {
				if (pattern.charAt(i) != '{')
					return false;
				i = pattern.indexOf('}', i)+1;
				j = path.indexOf(pattern.charAt(i), j);
			}
		}
		return i == ilen && j == jlen;
	}

}
