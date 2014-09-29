package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppCategoryList;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class AppCategoryListCallbackConverter extends AsyncCallbackConverter<String, List<AppCategory>> {

    public class AppCategoryListLoadException extends Exception {
        private static final long serialVersionUID = -9221968252788551910L;

        public AppCategoryListLoadException() { }

        public AppCategoryListLoadException(IplantErrorStrings errorStrings, Throwable caught) {
            super(errorStrings.appCategoriesLoadFailure(), caught);
        }
    }

    private final IplantErrorStrings errorStrings;

    private final AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);

    public AppCategoryListCallbackConverter(AsyncCallback<List<AppCategory>> callback,
                                            final IplantErrorStrings errorStrings) {
        super(callback);
        this.errorStrings = errorStrings;
    }

    @Override
    public void onFailure(Throwable caught) {
        super.onFailure(new AppCategoryListLoadException(errorStrings, caught));
    }

    @Override
    protected List<AppCategory> convertFrom(String object) {
        AutoBean<AppCategoryList> bean = AutoBeanCodex.decode(factory, AppCategoryList.class, object);
        AppCategoryList as = bean.as();
        return as.getCategories();
    }

}
