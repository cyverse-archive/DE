package org.iplantc.de.apps.widgets.client.view.editors.arguments.converters;

import com.google.common.base.Strings;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.Converter;

public class SplittableToBooleanConverter implements Converter<Splittable, Boolean> {

    @Override
    public Splittable convertFieldValue(Boolean object) {
        if(object == null){
            object = Boolean.FALSE;
        }
        return StringQuoter.split(String.valueOf(object));
    }

    @Override
    public Boolean convertModelValue(Splittable object) {
        if (object == null) {
            return Boolean.FALSE;
        }
        Boolean b = null;
        if (object.isBoolean()) {
            b = object.asBoolean();
        } else if (object.isString() && !Strings.isNullOrEmpty(object.asString())) {
            b = Boolean.valueOf(object.asString());
        }
        return b;
    }

}
