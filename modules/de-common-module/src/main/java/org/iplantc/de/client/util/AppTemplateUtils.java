package org.iplantc.de.client.util;

import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;
import org.iplantc.de.client.services.converters.AppTemplateCallbackConverter;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsDisplayMessages;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.Collections;
import java.util.List;

/**
 * FIXME Convert methods to non-static methods.
 * @author jstroot
 */
public class AppTemplateUtils {
    private static final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
    private static final AppsWidgetsDisplayMessages displayMessages = I18N.APPS_MESSAGES;

    private static Argument EMPTY_GROUP_ARG;

    public static final String EMPTY_GROUP_ARG_ID = "emptyArgumentGroupInfoArgumentId"; //$NON-NLS-1$
    public static final String NEW_ENV_VAR_NAME = "NEW_ENV_VAR"; //$NON-NLS-1$

    public static Argument getEmptyGroupArgument() {
        if (EMPTY_GROUP_ARG == null) {
            EMPTY_GROUP_ARG = factory.argument().as();
            EMPTY_GROUP_ARG.setId(EMPTY_GROUP_ARG_ID);
            EMPTY_GROUP_ARG.setType(ArgumentType.Info);
            EMPTY_GROUP_ARG.setLabel(displayMessages.emptyArgumentGroupBgText());
            EMPTY_GROUP_ARG.setDescription(displayMessages.emptyArgumentGroupBgText());
            EMPTY_GROUP_ARG.setRequired(false);
            EMPTY_GROUP_ARG.setOmitIfBlank(false);
        }
        return EMPTY_GROUP_ARG;
    }

    public static AppTemplate removeEmptyGroupArguments(final AppTemplate at) {
        AppTemplate copy = copyAppTemplate(at);
        for (ArgumentGroup ag : copy.getArgumentGroups()) {
            List<Argument> arguments = ag.getArguments();
            if ((arguments.size() == 1) && (arguments.get(0).getId() != null) && (arguments.get(0).getId().equals(EMPTY_GROUP_ARG_ID))) {
                arguments.clear();
            }
        }
        return copy;
    }

    public static AppTemplate copyAppTemplate(AppTemplate value) {
        AutoBean<AppTemplate> argAb = AutoBeanUtils.getAutoBean(value);

        final String payload = AutoBeanCodex.encode(argAb).getPayload();
        return new AppTemplateCallbackConverter(factory, null)
                .convertFrom(payload, false);
    }

    public static ArgumentGroup copyArgumentGroup(ArgumentGroup value) {
        AutoBean<ArgumentGroup> argGrpAb = AutoBeanUtils.getAutoBean(value);
        Splittable splitCopy = AutoBeanCodex.encode(argGrpAb);

        ArgumentGroup ret = AutoBeanCodex.decode(factory, ArgumentGroup.class, splitCopy).as();
        if (ret.getArguments() == null) {
            ret.setArguments(Collections.<Argument> emptyList());
        }
        return ret;
    }

    /**
     * Determines if the given items are equal by serializing them and comparing their
     * <code>Splittable</code> payloads.
     */
    public static boolean areEqual(SelectionItem a, SelectionItem b) {
        Splittable aSplit = getSplittable(AutoBeanUtils.getAutoBean(a));
        Splittable bSplit = getSplittable(AutoBeanUtils.getAutoBean(b));
        return aSplit.getPayload().equals(bSplit.getPayload());
    }
    
    public static boolean isSelectionArgumentType(ArgumentType type) {
        return isSimpleSelectionArgumentType(type) || type.equals(ArgumentType.TreeSelection);
    }
    
    public static boolean isSimpleSelectionArgumentType(ArgumentType t) {
        return t.equals(ArgumentType.TextSelection)
                || t.equals(ArgumentType.IntegerSelection)
                || t.equals(ArgumentType.DoubleSelection)
                || t.equals(ArgumentType.Selection)
                || t.equals(ArgumentType.ValueSelection);
    }

    private static Splittable getSplittable(AutoBean<?> autoBean) {
        return AutoBeanCodex.encode(autoBean);
    }

    public static boolean isDiskResourceArgumentType(ArgumentType type) {
        return type.equals(ArgumentType.FileInput)
                   || type.equals(ArgumentType.FolderInput)
                   || type.equals(ArgumentType.MultiFileSelector)
                   || type.equals(ArgumentType.FileFolderInput);
    }

    public static boolean isDiskResourceOutputType(ArgumentType type) {
        return type.equals(ArgumentType.FileOutput)
                   || type.equals(ArgumentType.FolderOutput)
                   || type.equals(ArgumentType.MultiFileOutput)
                   || type.equals(ArgumentType.FileFolderInput);
    }

    public static boolean isTextType(ArgumentType type) {
        return type.equals(ArgumentType.Text)
                   || type.equals(ArgumentType.MultiLineText)
                   || type.equals(ArgumentType.EnvironmentVariable)
                   || type.equals(ArgumentType.Output)
                   || type.equals(ArgumentType.Number)
                   || type.equals(ArgumentType.Integer)
                   || type.equals(ArgumentType.Double);
    }

    public static boolean typeSupportsValidators(ArgumentType type) {
        return type.equals(ArgumentType.Text)
                   || type.equals(ArgumentType.Double)
                   || type.equals(ArgumentType.Integer);
    }

    public static List<SelectionItem> getSelectedTreeItems(SelectionItemGroup sig) {
        if ((sig == null) || (sig.getArguments() == null)) {
            return Collections.emptyList();
        }
        List<SelectionItem> ret = Lists.newArrayList();
        for (SelectionItem si : sig.getArguments()) {
            if (si.isDefault()) {
                ret.add(si);
            }
        }

        if (sig.getGroups() != null) {
            for (SelectionItemGroup subSig : sig.getGroups()) {
                ret.addAll(getSelectedTreeItems(subSig));
            }
        }

        return ret;
    }

    public static Splittable getSelectedTreeItemsAsSplittable(SelectionItemGroup sig) {
        List<SelectionItem> selectedItems = getSelectedTreeItems(sig);
        Splittable splitArr = StringQuoter.createIndexed();
        int i = 0;
        for (SelectionItem si : selectedItems) {
            Splittable siSplit = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(si));
            siSplit.assign(splitArr, i++);
        }

        return splitArr;
    }

    public static SelectionItemGroup selectionItemToSelectionItemGroup(SelectionItem selectionItem) {
        Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(selectionItem));
        return AutoBeanCodex.decode(factory, SelectionItemGroup.class, split).as();
    }

    public static AppTemplate removeDateFields(AppTemplate at) {
        at.setEditedDate(null);
        at.setPublishedDate(null);
        return at;
    }

    public static <M extends SelectionItem> M addSelectionItemAutoBeanIdTag(M model, String id){
        AutoBean<M> autoBean = AutoBeanUtils.getAutoBean(model);
        autoBean.setTag(SelectionItem.TMP_ID_TAG, id);
        return autoBean.as();
    }

}
