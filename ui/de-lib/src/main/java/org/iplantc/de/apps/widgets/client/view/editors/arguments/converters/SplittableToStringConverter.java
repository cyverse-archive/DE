package org.iplantc.de.apps.widgets.client.view.editors.arguments.converters;

import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.Converter;

public class SplittableToStringConverter implements Converter<Splittable, String> {

    @Override
    public Splittable convertFieldValue(String object) {
        return StringQuoter.create(object);
    }

    @Override
    public String convertModelValue(Splittable object) {
        if (object == null)
            return "";

        return object.asString();
    }

}
