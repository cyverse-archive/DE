package org.iplantc.de.admin.desktop.client.refGenome;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author jstroot
 */
public interface RefGenomeView extends IsWidget, IsMaskable {

    public interface RefGenomeAppearance {

        String add();

        String addReferenceGenomeDialogHeading();

        ImageResource categoryIcon();

        String createdByColumn();

        String createdOnColumn();

        String editReferenceGenomeDialogHeading(String refGenomeName);

        String filterDataListEmptyText();

        String createdBy();

        String createdOn();

        String lastModified();

        String lastModBy();

        String nameColumn();

        String pathColumn();

        String refDeletePrompt();

        SafeHtml requiredNameLabel();

        SafeHtml requiredPathLabel();

        String saveBtnText();
    }

    public interface Presenter {
        public interface RefGenomePresenterAppearance{

            String addReferenceGenomeSuccess();

            String getReferenceGenomesLoadingMask();

            String updateReferenceGenomeSuccess();
        }

        void go(HasOneWidget container);

        void addReferenceGenome(ReferenceGenome referenceGenome);

        void editReferenceGenome(ReferenceGenome referenceGenome);

        void setViewDebugId(String baseId);

    }

    void setReferenceGenomes(List<ReferenceGenome> refGenomes);

    void addReferenceGenome(ReferenceGenome referenceGenome);

    void updateReferenceGenome(ReferenceGenome referenceGenome);

    void setPresenter(RefGenomeView.Presenter presenter);

    void editReferenceGenome(ReferenceGenome refGenome);

}
