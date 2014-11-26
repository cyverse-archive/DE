package org.iplantc.de.apps.widgets.client.view.editors.arguments.converters;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.Converter;

/**
 * @author jstroot
 */
public class SplittableToDiskResourceConverter implements Converter<Splittable, DiskResource> {

    private final DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);

    @Override
    public Splittable convertFieldValue(DiskResource object) {
        if (object == null)
            return StringQuoter.create("");

          return AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(object));
    }

    @Override
    public DiskResource convertModelValue(Splittable object) {
        if(object == null){
            return null;
        }
        if(object.isString()){

            AutoBean<DiskResource> drBean = factory.diskResource();
            DiskResource dr = drBean.as();
            dr.setPath(object.asString());
            return dr;
        }else {
            return null;
        }
    }
}
