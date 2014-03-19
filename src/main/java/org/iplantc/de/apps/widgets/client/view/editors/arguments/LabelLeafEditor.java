package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import static org.iplantc.de.client.models.apps.integration.ArgumentType.Flag;
import static org.iplantc.de.client.models.apps.integration.ArgumentType.Info;
import static org.iplantc.de.client.util.AppTemplateUtils.EMPTY_GROUP_ARG_ID;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.ArgumentType;

import com.google.common.base.Strings;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class LabelLeafEditor<T> implements LeafValueEditor<T> {
    protected final ArgumentEditor argEditor;

    private final HasSafeHtml hasHtml;
    private T lleModel;

    private final AppTemplateWizardAppearance appearance;

    public LabelLeafEditor(HasSafeHtml hasHtml, ArgumentEditor argEditor, final AppTemplateWizardAppearance appearance) {
        this.hasHtml = hasHtml;
        this.argEditor = argEditor;
        this.appearance = appearance;
    }

    @Override
    public T getValue() {
        return lleModel;
    }

    @Override
    public void setValue(T value) {
        this.lleModel = value;
        SafeHtml createArgumentLabel = createArgumentLabel(argEditor);
        hasHtml.setHTML(createArgumentLabel);
    }

    private SafeHtml createArgumentLabel(ArgumentEditor argEditor) {
        SafeHtmlBuilder labelText = new SafeHtmlBuilder();
        Boolean isRequired = argEditor.requiredEditor().getValue();
        if ((isRequired != null) && isRequired) {
            // If the field is required, it needs to be marked as such.
            labelText.append(appearance.getRequiredFieldLabel());
        }
        // JDS Remove the trailing colon. The FieldLabels will apply it automatically.
        String label = Strings.nullToEmpty(argEditor.labelEditor().getValue());
        SafeHtml safeHtmlLabel = SafeHtmlUtils.fromString(label.replaceFirst(":$", ""));
        ArgumentType argumentType = argEditor.typeEditor().getValue();
        if (Info.equals(argumentType)) {
            labelText.append(appearance.sanitizeHtml(label));
        } else {
            if (Flag.equals(argumentType)) {
                labelText.append(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").toSafeHtml());
            }
            String id = argEditor.idEditor().getValue();
            String description = argEditor.descriptionEditor().getValue();

            if (Strings.isNullOrEmpty(description) || EMPTY_GROUP_ARG_ID.equals(id)) {
                labelText.append(safeHtmlLabel);
            } else {
                labelText.append(appearance.getContextualHelpLabel(safeHtmlLabel, description));
            }
        }
        return labelText.toSafeHtml();
    }

}