package org.iplantc.de.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author jstroot
 */
@Controller
public class KeepAliveController {

    @RequestMapping("**/empty")
    @ResponseStatus(value = HttpStatus.OK)
    public void keepAlive() {
    }
}
