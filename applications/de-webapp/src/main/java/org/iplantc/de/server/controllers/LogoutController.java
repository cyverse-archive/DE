package org.iplantc.de.server.controllers;

import static org.iplantc.de.server.util.UrlUtils.convertRelativeUrl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jstroot
 */
@Controller
public class LogoutController {

    @Value("${org.iplantc.discoveryenvironment.cas.app-list}")
    private String appList;

    @Value("${org.iplantc.discoveryenvironment.cas.app-name}")
    private String appName;

    @Value("${org.iplantc.discoveryenvironment.cas.login-url}")
    private String loginUrl;

    @Value("${org.iplantc.discoveryenvironment.cas.logout-url}")
    private String logoutUrl;

    @Value("${org.iplantc.discoveryenvironment.cas.no-logout-url}")
    private String noLogoutUrl;

    @RequestMapping("/logout")
    public ModelAndView logout(final HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("logout");
        modelAndView.addObject("app_name", appName);
        modelAndView.addObject("app_list", appList);
        modelAndView.addObject("logout_url", convertRelativeUrl(request.getContextPath(), logoutUrl));
        modelAndView.addObject("no_logout_url", convertRelativeUrl(request.getContextPath(), noLogoutUrl));
        modelAndView.addObject("login_url", convertRelativeUrl(request.getContextPath(), loginUrl));
        return modelAndView;
    }
}
