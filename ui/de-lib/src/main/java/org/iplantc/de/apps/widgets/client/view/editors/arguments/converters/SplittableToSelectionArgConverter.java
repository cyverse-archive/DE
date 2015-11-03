package org.iplantc.de.apps.widgets.client.view.editors.arguments.converters;

import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.Converter;

public final class SplittableToSelectionArgConverter implements Converter<Splittable, SelectionItem> {
    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);

    @Override
    public Splittable convertFieldValue(SelectionItem object) {
        return AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(object));
    }

    @Override
    public SelectionItem convertModelValue(Splittable object) {
        if (object == null)
            return null;
        if (!object.isKeyed()) {
            return null;
        }
        if (object.isUndefined("display") || object.isUndefined("id")) {
            return null;
        }
        AutoBean<SelectionItem> ab = AutoBeanCodex.decode(factory, SelectionItem.class, object);
        return ab.as();
    }
}