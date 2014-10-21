package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

public class AppTemplateCallbackConverter extends AsyncCallbackConverter<String, AppTemplate> {

    private final AppTemplateAutoBeanFactory factory;

    public AppTemplateCallbackConverter(AppTemplateAutoBeanFactory factory,
                                        AsyncCallback<AppTemplate> callback) {
        super(callback);
        this.factory = factory;
    }

    @Override
    public void onSuccess(String result) {
        final Splittable split = StringQuoter.split(result);
        super.onSuccess(split.getPayload());
        return;
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
        Splittable atGroups = split.get("groups");
        for (int i = 0; i < atGroups.size(); i++) {
            Splittable grp = atGroups.get(i);
            Splittable properties = grp.get("properties");
            if (properties == null) {
                continue;
            }
            for (int j = 0; j < properties.size(); j++) {
                Splittable arg = properties.get(j);
                Splittable type = arg.get("type");
                if (type.asString().equals(ArgumentType.TreeSelection.name())) {
                    Splittable arguments = arg.get("arguments");
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
            /*
             * JDS If any argument has a "defaultValue", forward it to the "value" field
             */
            for (ArgumentGroup ag : atAb.as().getArgumentGroups()) {
                for (Argument arg : ag.getArguments()) {
                    if (arg.getDefaultValue() != null) {
                        arg.setValue(arg.getDefaultValue());
                    }
                }
            }
        }
        return atAb.as();
    }
}
