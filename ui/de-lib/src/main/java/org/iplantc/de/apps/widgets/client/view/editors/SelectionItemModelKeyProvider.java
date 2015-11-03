package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author jstroot
 */
public class SelectionItemModelKeyProvider implements ModelKeyProvider<SelectionItem> {

    @Override
    public String getKey(SelectionItem item) {
        final AutoBean<SelectionItem> autoBean = AutoBeanUtils.getAutoBean(item);
        final Object tag = autoBean.getTag(SelectionItem.TMP_ID_TAG);
        return tag.toString();
    }
}
