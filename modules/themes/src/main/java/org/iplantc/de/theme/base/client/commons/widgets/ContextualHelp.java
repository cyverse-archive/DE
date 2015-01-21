package org.iplantc.de.theme.base.client.commons.widgets;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by jstroot on 1/20/15.
 * @author jstroot
 */
public interface ContextualHelp {
    /**
     * FIXME The 'contextualHelp' method is a clone and own of IplantContextualHelpAccessStyle.contextualHelp.
     * Eventually, need to consolidate that class
     */
    public interface Style extends CssResource {
        String help();

        String contextualHelp();
    }

    public interface Resources extends ClientBundle {

        @Source("help.png")
        ImageResource help();

        @Source("ContextualHelp.css")
        Style css();
    }
}
