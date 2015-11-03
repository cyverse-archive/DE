package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.List;

/**
 * @author jstroot
 */
public class AppTemplateCallbackConverter extends AsyncCallbackConverter<String, AppTemplate> {

    private final AppTemplateAutoBeanFactory factory;
    private final AppTemplateUtils appTemplateUtils;

    public AppTemplateCallbackConverter(AppTemplateAutoBeanFactory factory,
                                        AsyncCallback<AppTemplate> callback) {
        super(callback);
        this.factory = factory;
        appTemplateUtils = AppTemplateUtils.getInstance();
    }

    @Override
    public void onSuccess(String result) {
        final Splittable split = StringQuoter.split(result);
        super.onSuccess(split.getPayload());
    }

    @Override
    public AppTemplate convertFrom(String object) {
        return convertFrom(object, true);
    }

    public AppTemplate convertFrom(String object, boolean forwardDefaults) {
        Splittable split = StringQuoter.split(object);
        AutoBean<AppTemplate> atAb = AutoBeanCodex.decode(factory, AppTemplate.class, split);

        /*
         * JDS Grab TreeSelection argument type's original selectionItems, decode them as
         * SelectionItemGroup, and place them back in the Argument's selection items.
         */
        Splittable atGroups = split.get(AppTemplate.GROUPS_KEY);
        for (int i = 0; i < atGroups.size(); i++) {
            Splittable grp = atGroups.get(i);
            Splittable properties = grp.get(ArgumentGroup.ARGUMENTS_KEY);
            if (properties == null) {
                continue;
            }
            for (int j = 0; j < properties.size(); j++) {
                Splittable arg = properties.get(j);
                Splittable type = arg.get(Argument.TYPE_KEY);
                if (type.asString().equals(ArgumentType.TreeSelection.name())) {
                    Splittable arguments = arg.get(Argument.SELECTION_ITEMS_KEY);
                    if ((arguments != null) && (arguments.isIndexed()) && (arguments.size() > 0)) {
                        SelectionItemGroup sig = AutoBeanCodex.decode(factory,
                                                                      SelectionItemGroup.class,
                                                                      arguments.get(0)).as();
                        atAb.as()
                            .getArgumentGroups()
                            .get(i)
                            .getArguments()
                            .get(j)
                            .setSelectionItems(Lists.<SelectionItem> newArrayList(sig));
                    }
                }
            }
        }

        if (forwardDefaults) {
            forwardDefaults(atAb);
        }
        setSelectionItemAutoBeanId(atAb.as());
        setArgumentValidatorUniqueAutoBeanId(atAb.as());

        return atAb.as();
    }

    /**
     * Adds context unique IDs (only unique within the argument) to any incoming ArgumentValidators.
     */
    private void setArgumentValidatorUniqueAutoBeanId(AppTemplate at) {
        for (ArgumentGroup ag : at.getArgumentGroups()) {
            int uniqueIdNum = 0;
            for (Argument arg : ag.getArguments()) {
                if (arg.getValidators() == null) {
                    continue;
                }
                for (ArgumentValidator av : arg.getValidators()) {
                    AutoBean<ArgumentValidator> autoBean = AutoBeanUtils.getAutoBean(av);
                    autoBean.setTag(ArgumentValidator.TMP_ID_TAG, "tmpId-" + uniqueIdNum++);
                }
            }
        }
    }

    /**
     * Sets a unique AutoBean tag from a given SelectionItem's ID.
     */
    private void setSelectionItemAutoBeanId(AppTemplate at) {
        for (ArgumentGroup ag : at.getArgumentGroups()) {
            for (Argument arg : ag.getArguments()) {
                if (appTemplateUtils.isSelectionArgumentType(arg.getType())) {
                    tagSelectionItemListItems(arg.getSelectionItems());
                }
            }
        }
    }

    void tagSelectionItemListItems(final List<? extends SelectionItem> selectionItems) {
        if (selectionItems == null) {
            return;
        }
        for (SelectionItem si : selectionItems) {
            final AutoBean<SelectionItem> autoBean = AutoBeanUtils.getAutoBean(si);
            autoBean.setTag(SelectionItem.TMP_ID_TAG, si.getId());

            if (si instanceof SelectionItemGroup) {
                final SelectionItemGroup selectionItemGroup = (SelectionItemGroup)si;
                tagSelectionItemListItems(selectionItemGroup.getArguments());
                tagSelectionItemListItems(selectionItemGroup.getGroups());
            }
        }
    }

    /**
     * Forwards items in the "defaultValue" key to the "value" key.
     * 
     * @param atAb the bean to be operated on.
     */
    private void forwardDefaults(AutoBean<AppTemplate> atAb) {
        /*
         * JDS If any argument has a "defaultValue", forward it to the "value" field
         */
        for (ArgumentGroup ag : atAb.as().getArgumentGroups()) {
            for (Argument arg : ag.getArguments()) {
                if (arg.getDefaultValue() != null) {
                    arg.setValue(arg.getDefaultValue());
                }
                // Check for null 'isRequired' flag
                if (arg.getRequired() == null) {
                    arg.setRequired(false);
                }
                // Check for null 'omitIfBlank' flag
                if (arg.isOmitIfBlank() == null) {
                    arg.setOmitIfBlank(false);
                }
            }
        }
    }
}
