package org.iplantc.de.server.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jstroot
 */
@Controller
public class LogoutController {

    @Value("${org.iplantc.discoveryenvironment.cas.app-list}") private String appList;
    @Value("${org.iplantc.discoveryenvironment.cas.app-name}") private String appName;
    @Value("${org.iplantc.discoveryenvironment.cas.login-url}") private String loginUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.logout-url}") private String logoutUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.no-logout-url}") private String noLogoutUrl;

    @RequestMapping("/de/logout")
    public ModelAndView logoutDe() {
        return doLogout("/de");
    }

    @RequestMapping("/belphegor/logout")
    public ModelAndView logoutBelphegor() {
        return doLogout("/belphegor");
    }

    public ModelAndView doLogout(final String appNamePathPart){
        ModelAndView modelAndView = new ModelAndView("logout");
        modelAndView.addObject("app_name", appName);
        modelAndView.addObject("app_list", appList);

        modelAndView.addObject("logout_url", appNamePathPart + logoutUrl);
        modelAndView.addObject("no_logout_url", noLogoutUrl);
        modelAndView.addObject("login_url", appNamePathPart);
        return modelAndView;
    }

}
