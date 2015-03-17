/**
 * 
 */
package org.iplantc.de.desktop.client.idroplite.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;

/**
 * @author sriram
 * 
 */
public interface IDropLiteView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void buildUploadApplet();

        void buildDownloadApplet();

        void onSimpleUploadClick();

        void onSimpleDownloadClick();
    }

    void setPresenter(Presenter p);

    void setApplet(HtmlLayoutContainer container);

    int getViewHeight();

    int getViewWidth();

    void setToolBarButton(int mode);

    void mask();

    void unmask();

    void disableSimpleDownload();

}
