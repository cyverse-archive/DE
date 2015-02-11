package org.iplantc.de.diskResource.client.views.sharing.dialogs;

import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.GridView;

import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class ShareResourceLinkDialog extends IPlantDialog {

    private final TextField textBox;

    @Inject
    ShareResourceLinkDialog(final GridView.Presenter.Appearance appearance) {
        setHeadingText(appearance.copy());
        setHideOnButtonClick(true);
        setResizable(false);
        setSize(appearance.shareLinkDialogWidth(), appearance.shareLinkDialogHeight());
        textBox = new TextField();
        textBox.setWidth(appearance.shareLinkDialogTextBoxWidth());
        textBox.setReadOnly(true);
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.add(textBox);
        container.add(new Label(appearance.copyPasteInstructions()));

        setWidget(container);
        setFocusWidget(textBox);
    }

    public void show(final String link) {
        textBox.setValue(link);
        textBox.selectAll();
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. " +
                                                    "Use show(List<DiskResource>) instead.");
    }
}
