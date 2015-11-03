package org.iplantc.de.server.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jstroot
 */
@Controller
public class LoggedOutController {

    @Value("${org.iplantc.discoveryenvironment.cas.app-name}") private String appName;
    @Value("${org.iplantc.discoveryenvironment.cas.login-url}") private String loginUrl;

    @RequestMapping("/de/logged-out")
    public ModelAndView loggedOutDe() {
        return doLoggedOut("/de");
    }

    @RequestMapping("/belphegor/logged-out")
    public ModelAndView loggedOutBelphegor() {
        return doLoggedOut("/belphegor");
    }

    private ModelAndView doLoggedOut(final String appNamePart){
        ModelAndView modelAndView = new ModelAndView("logged-out");
        modelAndView.addObject("app_name", appName);
        modelAndView.addObject("login_url", appNamePart);
        return modelAndView;
    }

}
