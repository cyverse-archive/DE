package org.iplantc.de.server.controllers;

import org.iplantc.de.server.DiscoveryEnvironmentMaintenance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author jstroot
 */
@Controller
public class DeController {

    private final Logger LOG = LoggerFactory.getLogger(DeController.class);

    @Value("${org.iplantc.discoveryenvironment.maintenance-file}")
    private String maintFile;

    @Value("&{org.iplantc.discoveryenvironment.environment.prod-deployment}")
    private String isProduction;

    @RequestMapping("/de")
    public String showDe(final HttpSession session,
                         final Model model) {
        DiscoveryEnvironmentMaintenance maintenance = new DiscoveryEnvironmentMaintenance(maintFile);
        if(maintenance.isUnderMaintenance()){
            session.invalidate();
            LOG.info("Invalidating session");
            return "redirect:/login";
        }
        model.addAttribute("isProduction", Boolean.parseBoolean(isProduction));

        LOG.info("Serving DE view");
        return "de";
    }
}
