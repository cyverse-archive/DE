package org.iplantc.de.apps.widgets.client.view.editors.arguments.converters;

import com.google.common.base.Strings;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.Converter;

public class SplittableToDoubleConverter implements Converter<Splittable, Double> {

  @Override
  public Splittable convertFieldValue(Double object) {
    if (object == null)
      return StringQuoter.create("");

    return StringQuoter.split(object.toString());
  }

  @Override
  public Double convertModelValue(Splittable object) {
    if (object == null)
      return null;

    Double dblValue = null;
    if (object.isNumber()) {
      dblValue = Double.valueOf(object.asNumber());
    } else if (object.isString() && !Strings.isNullOrEmpty(object.asString())) {
      try {
        dblValue = Double.valueOf(object.asString());
      } catch (NumberFormatException e) {
        dblValue = null;
      }
    }
    return dblValue;
  }

}
