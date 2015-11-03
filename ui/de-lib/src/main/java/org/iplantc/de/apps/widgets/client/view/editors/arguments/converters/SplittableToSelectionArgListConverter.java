package org.iplantc.de.apps.widgets.client.view.editors.arguments.converters;

import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.Converter;

import java.util.Collections;
import java.util.List;

public class SplittableToSelectionArgListConverter implements Converter<Splittable, List<SelectionItem>> {

    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);

    @Override
    public Splittable convertFieldValue(List<SelectionItem> object) {
        Splittable ret = StringQuoter.createIndexed();
        for(SelectionItem si : object){
            final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(si));
            encode.assign(ret, ret.size());
        }
        return ret;
    }

    @Override
    public List<SelectionItem> convertModelValue(Splittable object) {
        if(object == null || !object.isIndexed()){
            return Collections.emptyList();
        }
        List<SelectionItem> ret = Lists.newArrayList();
        int size = object.size();
        for (int i = 0; i < size; i++) {

            final Splittable splittable = object.get(i);
            final AutoBean<SelectionItem> decode = AutoBeanCodex.decode(factory, SelectionItem.class, splittable);
            ret.add(decode.as());
        }

        return ret;
    }
}
