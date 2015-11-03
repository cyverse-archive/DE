package org.iplantc.de.server;

import static org.iplantc.de.server.util.ServletUtils.loadResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.util.Assert;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to present a landing page to an unauthenticated DE user.
 *
 * TODO Convert to jsp.
 * @see org.springframework.web.servlet.View#render(Map, HttpServletRequest, HttpServletResponse)
 *
 * @author jstroot
 */
public class DeLandingPage implements LandingPage, InitializingBean {

    private static final String ENCODING = "UTF-8";

    private enum Templates {
        BOUNDED_MAINTENANCE_DIV(loadResource("bounded-maintenance-div-template.html")),
        UNBOUNDED_MAINTENANCE_DIV(loadResource("unbounded-maintenance-div-template.html")),
        LOGIN_DIV(loadResource("login-div-template.html")),
        LANDING_PAGE(loadResource("landing-page-template.html"));

        private final String text;

        Templates(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final Logger LOG = LoggerFactory.getLogger(DeLandingPage.class);
    private String deMaintenanceFile;
    private String loginUrl;
    private ServiceProperties casService;

    public void setDeMaintenanceFile(String deMaintenanceFile) {
        this.deMaintenanceFile = deMaintenanceFile;
    }

    public void setLoginUrl(String loginUrl) {
        LOG.info("LoginUrl = {}", loginUrl);
        this.loginUrl = loginUrl;
    }


    public void setCasService(ServiceProperties casService) {
        this.casService = casService;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(deMaintenanceFile, "the path to the DE maintenance file must be specified");
        Assert.hasLength(loginUrl, "the CAS login URL must be specified");
        Assert.notNull(casService, "the CAS service properties must be specified");
        Assert.hasLength(casService.getService(), "the CAS service name must be specified");
    }

    public void display(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String contextPath = req.getContextPath();
        String loginDiv = buildLoginDiv(req);
        ST st = new ST(Templates.LANDING_PAGE.toString(), '$', '$');
        st.add("context_path", contextPath);
        st.add("login_div", loginDiv);
        res.setContentType("text/html");
        res.getWriter().print(st.render());
    }

    private DiscoveryEnvironmentMaintenance getDeMaintenance() {
        return new DiscoveryEnvironmentMaintenance(deMaintenanceFile);
    }

    private String buildLoginDiv(HttpServletRequest req) throws IOException {
        DiscoveryEnvironmentMaintenance deMaintenance = getDeMaintenance();
        if (deMaintenance.hasMaintenanceTimes()) {
            return buildBoundedMaintenanceDiv(deMaintenance);
        } else if (deMaintenance.isUnderMaintenance()) {
            return buildUnboundedMaintenanceDiv();
        } else {
            return buildLoginButtonDiv(req);
        }
    }

    private String buildBoundedMaintenanceDiv(DiscoveryEnvironmentMaintenance deMaintenance) {
        ST st = new ST(Templates.BOUNDED_MAINTENANCE_DIV.toString(), '$', '$');
        st.add("start_time", deMaintenance.getStartTime());
        st.add("end_time", deMaintenance.getEndTime());
        return st.render();
    }

    private String buildUnboundedMaintenanceDiv() {
        return Templates.UNBOUNDED_MAINTENANCE_DIV.toString();
    }

    private String buildLoginButtonDiv(HttpServletRequest req) throws IOException {
        ST st = new ST(Templates.LOGIN_DIV.toString(), '$', '$');
        st.add("extra_params", buildExtraParams(req));
        st.add("login_url", loginUrl);
        st.add("service_url", casService.getService());
        return st.render();
    }

    private String objectToString(Object object) {
        if (object instanceof String[]) {
            return ((String[]) object)[0];
        }
        else return object.toString();
    }

    private String buildExtraParams(HttpServletRequest req) throws IOException {
        StringBuilder extraParams = new StringBuilder();
        for (Object paramName : req.getParameterMap().keySet()) {
            Object paramValue = req.getParameterMap().get(paramName);
            extraParams.append("\n        <input type=\"hidden\" name=\"")
                    .append(URLEncoder.encode(objectToString(paramName), ENCODING))
                    .append("\" value=\"")
                    .append(URLEncoder.encode(objectToString(paramValue), ENCODING))
                    .append("\" />");
        }
        return extraParams.toString();
    }
}
