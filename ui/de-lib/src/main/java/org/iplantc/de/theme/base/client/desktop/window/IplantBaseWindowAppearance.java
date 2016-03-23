package org.iplantc.de.theme.base.client.desktop.window;

import org.iplantc.de.desktop.client.views.windows.IplantWindowBase;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.widget.core.client.Header;
import com.sencha.gxt.widget.core.client.button.IconButton;

public class IplantBaseWindowAppearance implements IplantWindowBase.IplantWindowAppearance {

    interface Styles extends CssResource {

        String closeBtn();

        String headerText();

        String layoutBtn();

        String maximizeBtn();

        String minimizeBtn();

        String restoreBtn();
    }

    interface IplantWindowResources extends ClientBundle {
        @Source("org/iplantc/de/theme/base/client/desktop/window/IplantWindowStyles.css")
        Styles css();

        @Source("org/iplantc/de/theme/base/client/desktop/window/cyverse_tiny.png")
        ImageResource headerIcon();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_exit.png")
        ImageResource closeBtnImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/window_layout.png")
        ImageResource layoutBtnImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_maximize.png")
        ImageResource maximizeBtnImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_minimize.png")
        ImageResource minimizeBtnImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_restore.png")
        ImageResource restoreBtnImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_exit_hover.png")
        ImageResource closeBtnHoverImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/window_layout_hover.png")
        ImageResource layoutBtnHoverImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_maximize_hover.png")
        ImageResource maximizeBtnHoverImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_minimize_hover.png")
        ImageResource minimizeBtnHoverImage();

        @Source("org/iplantc/de/theme/base/client/desktop/window/button_restore_hover.png")
        ImageResource restoreBtnHoverImage();

        @DataResource.MimeType("font/opentype")
        @Source("org/iplantc/de/theme/base/client/desktop/Texta_Font/Texta-Bold.otf")
        DataResource textaBold();

        @DataResource.MimeType("font/opentype")
        @Source("org/iplantc/de/theme/base/client/desktop/Texta_Font/Texta-Regular.otf")
        DataResource textaRegular();
    }

    private final IplantWindowResources resources;
    private final IplantWindowStrings strings;

    public IplantBaseWindowAppearance() {
        this(GWT.<IplantWindowResources> create(IplantWindowResources.class),
             GWT.<IplantWindowStrings> create(IplantWindowStrings.class));
    }

    public IplantBaseWindowAppearance(final IplantWindowResources resources,
                                      final IplantWindowStrings strings){
        this.resources = resources;
        this.resources.css().ensureInjected();
        this.strings = strings;
    }

    @Override
    public IconButton.IconConfig closeBtnConfig() {
        return new IconButton.IconConfig(resources.css().closeBtn());
    }

    @Override
    public String closeBtnToolTip() {
        return strings.closeBtnToolTip();
    }

    @Override
    public IconButton.IconConfig layoutBtnConfig() {
        return new IconButton.IconConfig(resources.css().layoutBtn());
    }

    @Override
    public String layoutBtnToolTip() {
        return strings.layoutBtnToolTip();
    }

    @Override
    public IconButton.IconConfig maximizeBtnConfig() {
        return new IconButton.IconConfig(resources.css().maximizeBtn());
    }

    @Override
    public String maximizeBtnToolTip() {
        return strings.maximizeBtnToolTip();
    }

    @Override
    public IconButton.IconConfig minimizeBtnConfig() {
        return new IconButton.IconConfig(resources.css().minimizeBtn());
    }

    @Override
    public String minimizeBtnToolTip() {
        return strings.minimizeBtnToolTip();
    }

    @Override
    public IconButton.IconConfig restoreBtnConfig() {
        return new IconButton.IconConfig(resources.css().restoreBtn());
    }

    @Override
    public String restoreBtnToolTip() {
        return strings.restoreBtnToolTip();
    }

    @Override
    public void setHeaderStyle(Header header) {
        header.setIcon(resources.headerIcon());
        header.getAppearance().getTextElem(header.getElement()).addClassName(resources.css().headerText());
    }

    @Override
    public String snapLeftMenuItem() {
        return strings.snapLeft();
    }

    @Override
    public String snapRightMenuItem() {
        return strings.snapRight();
    }
}
