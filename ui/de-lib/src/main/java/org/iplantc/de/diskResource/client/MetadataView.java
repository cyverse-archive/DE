package org.iplantc.de.diskResource.client;

import java.util.List;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by jstroot on 2/10/15.
 * @author jstroot
 */
public interface MetadataView extends IsWidget {

    interface Appearance {

        String attribute();

        SafeHtml boldHeader(String name);

        SafeHtml buildLabelWithDescription(String label, String description, boolean allowBlank);

        String confirmAction();

        String metadataTemplateConfirmRemove();

        String metadataTemplateRemove();

        String metadataTemplateSelect();

        String newAttribute();

        String newValue();

        String paramValue();

        SafeHtml renderComboBoxHtml(MetadataTemplateInfo object);

        void renderMetadataCell(SafeHtmlBuilder sb, String value);

        String templateSelectedLoadingMask();

        String userMetadata();

        String add();

        ImageResource addIcon();

        String delete();

        String metadataTermGuide();

        ImageResource deleteIcon();

		String additionalMetadata();
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
    	
    	interface Appearance {
    		
    		String templateListingError();
    		
    		String loadMetadataError();
    		
    		String saveMetadataError();
    		
    		String templateinfoError();

			String selectTemplate();

			String templates();
    	}

        DiskResource getSelectedResource();

        void onTemplateSelected(String templateId);

        void setDiskResourceMetadata(DiskResourceMetadataUpdateCallback callback);

        void onSelectTemplate();
    }


    boolean isValid();
    
    List<DiskResourceMetadata> getAvus();

    void loadMetadata(List<DiskResourceMetadata> metadataList);

    void loadUserMetadata(List<DiskResourceMetadata> metadataList);

    void setPresenter(Presenter p);
    
    void mask();
    
    void unmask();
    
    void updateMetadataFromTemplateView(List<DiskResourceMetadata> metadataList);

 }
