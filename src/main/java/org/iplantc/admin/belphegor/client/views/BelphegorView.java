package org.iplantc.admin.belphegor.client.views;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface BelphegorView extends IsWidget {

    public interface Presenter {

        void go(HasWidgets hasWidgets);

    }

}
