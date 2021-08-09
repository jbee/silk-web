package se.jbee.web;

import javax.servlet.http.HttpServletRequest;

public interface HttpRequestPrecondition {

	void check(HttpServletRequest request) throws Exception;
}
