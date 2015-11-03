package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.TextViewToolBar;

public class TextViewToolBarDefaultAppearance extends AbstractToolBarDefaultAppearance implements TextViewToolBar.TextViewToolBarAppearance {

    @Override
    public String cbxWrapBoxLabel() {
        return fileViewerStrings.wrap();
    }

    @Override
    public String previewMarkdownBtnText() {
        return fileViewerStrings.previewMarkdownLabel();
    }
}
