package se.jbee.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class DispatcherServlet extends GenericServlet {

	private final HttpRequestRouter router;
	private final Map<String, HttpResource> implsByID;

	public DispatcherServlet(HttpRequestRouter router, Map<String, HttpResource> implsByID) {
		this.router = router;
		this.implsByID = implsByID;
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpServletRequest r = (HttpServletRequest) request;
		String id = router.route(r);
		HttpResource impl = implsByID.get(id);

	}

}
