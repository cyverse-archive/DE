package org.iplantc.de.server.controllers;

import org.iplantc.de.client.DiscoveryEnvironment;
import org.iplantc.de.server.DiscoveryEnvironmentMaintenance;
import org.iplantc.de.server.IpRanges;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author jstroot
 */
@Controller
public class DeController {

    private final Logger LOG = LoggerFactory.getLogger(DeController.class);

    @Value("${org.iplantc.discoveryenvironment.maintenance-file}") private String maintenanceFile;
    @Value("${org.iplantc.discoveryenvironment.environment.prod-deployment}") private String isProduction;
    @Value("${org.iplantc.discoveryenvironment.local-ip-ranges}") private String localIpRanges;

    @RequestMapping("/")
    public String redirectToDe(final HttpServletRequest request) throws MalformedURLException {
        String referer = request.getHeader("referer");
        URL url;
        /*
         * Sometimes a login attempt will redirect to "/".
         * If there is a "referer" header, redirect to its path. Otherwise, default to redirecting
         * to "/de/".
         */
        if(!Strings.isNullOrEmpty(referer)){
                url = new URL(referer);
            if(!Strings.isNullOrEmpty(url.getPath())){
                return "redirect:" + url.getPath();
            }
        }
        return "redirect:de/";
    }

    private DiscoveryEnvironmentMaintenance getDeMaintenance() {
        return new DiscoveryEnvironmentMaintenance(maintenanceFile);
    }

    private boolean isLocalRequest(HttpServletRequest req) {
        IpRanges ipRanges = new IpRanges(localIpRanges);
        String forwardedFor = req.getHeader("X-Forwarded-For");
        if (forwardedFor != null && forwardedFor.length() != 0) {
            return ipRanges.matches(forwardedFor.split("\\s*,\\s*")[0]);
        } else {
            return ipRanges.matches(req.getRemoteAddr());
        }
    }

    private boolean isUnderMaintenance(HttpServletRequest req) {
        return !isLocalRequest(req) && getDeMaintenance().isUnderMaintenance();
    }

    @RequestMapping("/de/")
    public String showDe(final HttpSession session,
                         final Model model,
                         final HttpServletRequest request) {

        if(isUnderMaintenance(request)){
            session.invalidate();
            LOG.info("Invalidating session");
            return "redirect:/de/logout";
        }
        final boolean isProd = Boolean.parseBoolean(isProduction);
        model.addAttribute("isProduction", isProd);

        return "de";
    }
}
