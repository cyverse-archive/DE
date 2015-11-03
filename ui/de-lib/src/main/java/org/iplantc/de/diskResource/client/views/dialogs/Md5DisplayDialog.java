package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;

import com.sencha.gxt.widget.core.client.form.Validator;

public class Md5DisplayDialog extends IPlantPromptDialog {

    public Md5DisplayDialog(String caption,
                            String heading,
                            int maxLength,
                            String initialText,
                            Validator<String> validator) {
        super(caption, maxLength, initialText, validator);
        setHeadingText(heading);
        setPredefinedButtons(PredefinedButton.OK);
    }

}
