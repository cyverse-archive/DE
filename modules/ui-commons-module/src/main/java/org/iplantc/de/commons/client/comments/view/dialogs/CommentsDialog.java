package org.iplantc.de.commons.client.comments.view.dialogs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.commons.client.comments.CommentsView;
import org.iplantc.de.commons.client.comments.gin.factory.CommentsPresenterFactory;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Window;

/**
 * @author jstroot
 */
public class CommentsDialog extends Window {

    @Inject CommentsPresenterFactory commentsPresenterFactory;

    @Inject
    CommentsDialog(final IplantDisplayStrings displayStrings){
        setHeadingText(displayStrings.comments());
        remove(this.getButtonBar());
        setSize("600px", "450px");
    }

    public void show(final HasId hasId,
                     final boolean isOwner,
                     final MetadataServiceFacade metadataServiceFacade){
        CommentsView.Presenter cp = commentsPresenterFactory.createCommentsPresenter(hasId.getId(), isOwner);
        cp.go(this, metadataServiceFacade);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. " +
                                                    "Use show(HasId, boolean, MetadataServiceFacade) instead.");
    }
}
