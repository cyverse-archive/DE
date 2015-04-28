package org.iplantc.de.server.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jstroot
 */
@Controller
public class AccessDeniedController {

    @Value("${org.iplantc.discoveryenvironment.cas.logout-url}")
    private String logoutUrl;

    @RequestMapping("/access-denied")
    public ModelAndView onAccessDenied() {
        ModelAndView modelAndView = new ModelAndView("access-denied");
        modelAndView.addObject("logout_url", "/belphegor" + logoutUrl);
        return modelAndView;
    }
}
