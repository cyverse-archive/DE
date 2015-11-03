package org.iplantc.de.server.rpc;

import org.iplantc.de.conf.WebMvcConfig;
import org.iplantc.de.server.services.HasHttpServletRequest;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works on the principle that it is both a RemoteServiceServlet AND a Spring MVC
 * Controller (don’t you just love polymorphism?) meaning that it can not only be used as a target
 * by the DispatcherServlet for handling incoming RPC requests, but also to decode the incoming RPC
 * requests. Remember I previously mentioned the Gang of Four? Well you’re looking right at the
 * strategy pattern baby. Pretty cool huh?
 *
 * Upon receiving a request we delegate handling of this to the Controller’s superclass
 * (the RemoteServiceServlet) to handle the unmarshalling of the request. We then intercept the
 * processing of the message by overriding RemoteServiceServlet.processCall method and delegate the
 * processing to the RemoteService POJO.
 *
 * Where did we get this magical POJO from you ask? Well we injected it into the class via the
 * setRemoteService setter back in the {@link WebMvcConfig} config file.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Strategy_pattern">Strategy Pattern</a>
 * @see <a href="https://technophiliac.wordpress.com/2008/08/24/giving-gwt-a-spring-in-its-step/">Giving GWT a Spring in its step</a>
 *
 * @author jstroot
 */
public class GwtRpcController extends RemoteServiceServlet implements Controller,
                                                                      ServletContextAware {

    private ServletContext servletContext;

    private RemoteService remoteService;

    private Class remoteServiceClass;

    public GwtRpcController(RemoteService remoteService) {
        setRemoteService(remoteService);
    }

    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        // Set request on service if required.
        if(this.remoteService instanceof HasHttpServletRequest){
            ((HasHttpServletRequest)this.remoteService).setRequest(request);
        }
        super.doPost(request, response);
        return null;
    }

    @Override
    public String processCall(String payload) throws SerializationException {
        try {

            RPCRequest rpcRequest = RPC.decodeRequest(payload,
                                                      this.remoteServiceClass);

            // delegate work to the spring injected service
            return RPC.invokeAndEncodeResponse(this.remoteService, rpcRequest
                                                                       .getMethod(), rpcRequest.getParameters());
        } catch (IncompatibleRemoteServiceException ex) {
            getServletContext().log("An IncompatibleRemoteServiceException was thrown while processing this call.",
                                       ex);
            return RPC.encodeResponseForFailure(null, ex);
        }
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setRemoteService(RemoteService remoteService) {
        this.remoteService = remoteService;
        this.remoteServiceClass = this.remoteService.getClass();
    }

}

