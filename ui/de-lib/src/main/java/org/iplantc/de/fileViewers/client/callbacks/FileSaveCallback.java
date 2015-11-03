package org.iplantc.de.fileViewers.client.callbacks;

import static org.iplantc.de.resources.client.messages.I18N.ERROR;
import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.events.DefaultUploadCompleteHandler;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

/**
 * @author jstroot
 */
public class FileSaveCallback implements AsyncCallback<File> {

    private final IplantErrorStrings errorStrings;
    private final HasHandlers hasHandlers;
    private final String parentFolder;
    private final String fileName;
    private final IsMaskable maskingContainer;
    private final boolean newFile;
    private UserSessionServiceFacade userSessionService;

    public FileSaveCallback(final UserSessionServiceFacade userSessionService,
                            final String path,
                            final boolean newFile,
                            final IsMaskable container,
                            final HasHandlers hasHandlers) {
        this.userSessionService = userSessionService;
        this.hasHandlers = hasHandlers;
        this.fileName = DiskResourceUtil.getInstance().parseNameFromPath(path);
        this.parentFolder = DiskResourceUtil.getInstance().parseParent(path);
        this.maskingContainer = container;
        this.newFile = newFile;
        errorStrings = ERROR;
    }

    @Override
    public void onSuccess(File result) {
        DefaultUploadCompleteHandler uch = new DefaultUploadCompleteHandler(userSessionService,
                                                                            parentFolder);
        String fileJson = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(result)).getPayload();
        if (newFile) {
            uch.onCompletion(fileName, fileJson);
        }

        hasHandlers.fireEvent(new FileSavedEvent(result));
    }

    @Override
    public void onFailure(Throwable caught) {
        maskingContainer.unmask();
        ErrorHandler.post(errorStrings.fileUploadsFailed(Lists.newArrayList(fileName)),
                          caught);
    }
}
