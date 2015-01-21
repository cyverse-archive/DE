package org.iplantc.de.theme.base.client.commons.error;

import org.iplantc.de.commons.client.views.dialogs.ErrorDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class ErrorDialogDefaultAppearance implements ErrorDialog.ErrorDialogAppearance {

    private final IplantDisplayStrings iplantDisplayStrings;
    private final Resources resources;
    private final ErrorStrings errorStrings;

    public interface Style extends CssResource {
        String bgColor();
    }

    public interface Resources extends ClientBundle {

        @Source("icon-error.gif")
        ImageResource errorIcon();

        @Source("ErrorStyle.css")
        Style css();

    }

    public ErrorDialogDefaultAppearance() {
        this(GWT.<ErrorStrings> create(ErrorStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<Resources> create(Resources.class));
    }

    ErrorDialogDefaultAppearance(final ErrorStrings errorStrings,
                                 final IplantDisplayStrings iplantDisplayStrings,
                                 final Resources resources) {
        this.errorStrings = errorStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.resources = resources;
        this.resources.css().ensureInjected();
    }

    @Override
    public String detailsHeading() {
        return iplantDisplayStrings.details();
    }

    @Override
    public String detailsPanelHeight() {
        return "150";
    }

    @Override
    public String detailsPanelWidth() {
        return "350";
    }

    @Override
    public String errorHeading() {
        return errorStrings.error();
    }

    @Override
    public int minHeight() {
        return 300;
    }

    @Override
    public int minWidth() {
        return 350;
    }

    @Override
    public ImageResource errorIcon() {
        return resources.errorIcon();
    }

    @Override
    public String bgColor() {
        return resources.css().bgColor();
    }
}
