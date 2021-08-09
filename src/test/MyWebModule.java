import se.jbee.web.WebModule;

public class MyWebModule extends WebModule {


	@Override
	protected void configure() {
		contextPath("/foo");
		servletName("sample-servlet");
		controller(RestController.class);
		defaultValue("time", long.class).to(() -> System.currentTimeMillis());
	}

}
