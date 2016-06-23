package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by jstroot on 2/10/15.
 *
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

        String newUnit();

        String paramValue();

        SafeHtml renderComboBoxHtml(MetadataTemplateInfo object);

        void renderMetadataCell(SafeHtmlBuilder sb, String value);

        String loadingMask();

        String userMetadata();

        String add();

        ImageResource addIcon();

        String delete();

        String metadataTermGuide();

        ImageResource deleteIcon();

        String additionalMetadata();

        String paramUnit();

        String selectTemplate();

        String importMd();

        String panelWidth();

        String panelHeight();
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        final String AVU_BEAN_TAG_MODEL_KEY = "model-key";

        interface Appearance {

            String templateListingError();

            String loadMetadataError();

            String saveMetadataError();

            String templateinfoError();

            String selectTemplate();

            String templates();

            String error();

            String incomplete();
        }

        Avu setAvuModelKey(Avu avu);

        DiskResource getSelectedResource();

        void onTemplateSelected(String templateId);

        void setDiskResourceMetadata(DiskResourceMetadataUpdateCallback callback);

        void onSelectTemplate();

        void onImport(List<Avu> selectedItems);

        boolean isDirty();
    }


    boolean isDirty();

    boolean isValid();

    List<Avu> getAvus();

    void loadMetadata(List<Avu> metadataList);

    void loadUserMetadata(List<Avu> metadataList);

    void setPresenter(Presenter p);

    void mask();

    void unmask();

    void updateMetadataFromTemplateView(List<Avu> metadataList);

    List<Avu> getUserMetadata();

    void addToUserMetadata(List<Avu> umd);

    void removeImportedMetadataFromStore(List<Avu> umd);

}
