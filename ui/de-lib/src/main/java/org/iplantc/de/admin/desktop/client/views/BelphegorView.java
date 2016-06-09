package org.iplantc.de.admin.desktop.client.views;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author jstroot
 */
public interface BelphegorView extends IsWidget {

    public interface BelphegorViewAppearance {

        String applications();

        String logout();

        String logoutWindowUrl();

        SafeHtml renderNorthContainer();

        BelphegorStyle style();

        SafeHtml nsfProjectText();

        SafeHtml projectCopyrightStatement();

        String referenceGenomes();

        String toolRequests();

        String systemMessages();

        String toolAdmin();
    }

    public interface Presenter {

        void go(HasWidgets hasWidgets);

    }

    interface BelphegorStyle extends CssResource {

        String iplantcLogo();

        String iplantcHeader();

        String headerMenu();

        String headerMenuBody();

        String nsfText();

        String copyright();

        String footer();
    }

    void doLogout();
}
