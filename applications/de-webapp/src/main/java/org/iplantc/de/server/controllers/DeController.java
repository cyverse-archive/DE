package org.iplantc.de.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jstroot
 */
@Controller
public class DeController {

    @RequestMapping("/de")
    public ModelAndView showDe() {
        ModelAndView modelAndView = new ModelAndView("de");

        return modelAndView;
    }
}
