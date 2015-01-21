package org.iplantc.de.fileViewers.client.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.TreeUrlAutoBeanFactory;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.models.viewer.VizUrlList;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author jstroot
 */
public class TreeUrlCallback implements AsyncCallback<String> {

    public interface TreeUrlCallbackAppearance {

        String unableToRetrieveTreeUrls(String fileName);
    }

    private final static TreeUrlAutoBeanFactory factory = GWT.create(TreeUrlAutoBeanFactory.class);
    private final TreeUrlCallbackAppearance appearance = GWT.create(TreeUrlCallbackAppearance.class);
    private final IsMaskable container;
    private final File file;
    private final FileViewer viewer;

    public TreeUrlCallback(File file, IsMaskable container, FileViewer viewer) {
        this.file = file;
        this.container = container;
        this.viewer = viewer;
    }

    public static List<VizUrl> getTreeUrls(String urls) {
        if (urls == null) {
            return null;
        }

        AutoBean<VizUrlList> bean = AutoBeanCodex.decode(factory, VizUrlList.class, urls);
        return bean.as().getUrls();
    }

    @Override
    public void onFailure(Throwable caught) {
        if (container != null) {
            container.unmask();
        }

        String errMsg = appearance.unableToRetrieveTreeUrls(file.getName());
        ErrorHandler.post(errMsg, caught);
    }

    @Override
    public void onSuccess(String result) {
        if (result != null && !result.isEmpty()) {
            List<VizUrl> urlsList = getTreeUrls(result);
            if (urlsList != null) {
                viewer.setData(urlsList);

            } else {
                // couldn't find any tree URLs in the response, so display an error.
                onFailure(new Exception(result));
            }
        } else {
            // couldn't find any tree URLs in the response, so display an error.
            onFailure(new Exception(result));
        }

        if (container != null) {
            container.unmask();
        }

    }

}
