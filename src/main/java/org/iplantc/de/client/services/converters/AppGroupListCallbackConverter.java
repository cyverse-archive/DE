package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.apps.AppGroupList;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class AppGroupListCallbackConverter extends AsyncCallbackConverter<String, List<AppGroup>> {

    public class AppGroupListLoadException extends Exception {
        private static final long serialVersionUID = -9221968252788551910L;

        public AppGroupListLoadException(IplantErrorStrings errorStrings, Throwable caught) {
            super(errorStrings.analysisGroupsLoadFailure(), caught);
        }
        
    }
    private final IplantErrorStrings errorStrings;

    private final AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);

    public AppGroupListCallbackConverter(AsyncCallback<List<AppGroup>> callback, final IplantErrorStrings errorStrings) {
        super(callback);
        this.errorStrings = errorStrings;
    }

    @Override
    protected List<AppGroup> convertFrom(String object) {
        AutoBean<AppGroupList> bean = AutoBeanCodex.decode(factory, AppGroupList.class, object);
        AppGroupList as = bean.as();
        List<AppGroup> groups = as.getGroups();
        return groups;
    }
    
    @Override
    public void onFailure(Throwable caught) {
        super.onFailure(new AppGroupListLoadException(errorStrings, caught));
    }

}
