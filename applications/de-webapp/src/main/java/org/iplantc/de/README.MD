This is a [Spring Boot](https://github.com/spring-projects/spring-boot) application. 
The [Application.java](Application.java) class is the entry point for the webapp, which extends
AbstractAnnotationConfigDispatcherServletInitializer. This provides some automated, yet configurable 
application initialization for the Web MVC `DispatcherServlet`.

All GWT RPC calls are routed through the `DispatcherServlet`. Please refer to the documentation in 
the [GwtRpcController](server/rpc/GwtRpcController.java) class. The GWT RPC configuration is 
handled in [GwtRpcConfig.java](conf/GwtRpcConfig.java).

