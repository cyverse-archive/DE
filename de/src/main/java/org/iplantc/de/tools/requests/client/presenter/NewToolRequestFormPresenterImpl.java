/**
 * 
 */
package org.iplantc.de.tools.requests.client.presenter;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceExistMap;
import org.iplantc.de.client.models.toolRequests.NewToolRequest;
import org.iplantc.de.client.models.toolRequests.RequestedToolDetails;
import org.iplantc.de.client.models.toolRequests.ToolRequestFactory;
import org.iplantc.de.client.models.toolRequests.YesNoMaybe;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.ToolRequestServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tools.requests.client.views.NewToolRequestFormView;
import org.iplantc.de.tools.requests.client.views.NewToolRequestFormView.Presenter;
import org.iplantc.de.tools.requests.client.views.Uploader;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.List;
import java.util.Set;

/**
 * @author sriram
 *
 */
public class NewToolRequestFormPresenterImpl implements Presenter {

    private static final DiskResourceAutoBeanFactory FS_FACTORY = GWT.create(DiskResourceAutoBeanFactory.class);
    private static final ToolRequestFactory REQ_FACTORY = GWT.create(ToolRequestFactory.class);

    private final DiskResourceServiceFacade fsServices = ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
    private final ToolRequestServiceFacade reqServices = ServicesInjector.INSTANCE.getToolRequestServiceProvider();

    private final NewToolRequestFormView view;
    private final Command callback;
    private NewToolRequestFormView.SELECTION_MODE toolSelectionMode;
    private NewToolRequestFormView.SELECTION_MODE testDataSelectionMode;
    private NewToolRequestFormView.SELECTION_MODE otherDataSelectionMode;

    @Inject DiskResourceUtil diskResourceUtil;

    @Inject
    NewToolRequestFormPresenterImpl(@Assisted final NewToolRequestFormView view,
                                    @Assisted final Command callbackCmd) {
        this.view = view;
        this.callback = callbackCmd;
        view.setPresenter(this);
        setToolMode(NewToolRequestFormView.SELECTION_MODE.UPLOAD);
        setTestDataMode(NewToolRequestFormView.SELECTION_MODE.UPLOAD);
        setOtherDataMode(NewToolRequestFormView.SELECTION_MODE.UPLOAD);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.commons.client.presenter.Presenter#go(com.google.gwt.user.client.ui.HasOneWidget)
     */
    @Override
    public void go(final HasOneWidget container) {
        container.setWidget(view);
    }

    /**
     * @see Presenter#onCancelBtnClick()
     */
    @Override
    public void onCancelBtnClick() {
        executeCallback();
    }

    /**
     * @see Presenter#onSubmitBtnClick()
     */
    @Override
    public void onSubmitBtnClick() {
        if (isFormValid()) {
            view.indicateSubmissionStart();
            validateUploadsNew(getUploadersToSubmit(), submitCmd(indicateSuccessCmd));
        } else {
            view.indicateSubmissionFailure(I18N.ERROR.invalidToolRequest());
        }
    }
    
    @Override
    public void setToolMode(NewToolRequestFormView.SELECTION_MODE mode) {
        toolSelectionMode = mode;
    }
    
    
    @Override
    public void setTestDataMode(NewToolRequestFormView.SELECTION_MODE mode) {
       testDataSelectionMode = mode;
    }
    
    @Override
    public void setOtherDataMode(NewToolRequestFormView.SELECTION_MODE mode) {
        otherDataSelectionMode = mode;
    }
    
    @Override
    public void onToolSelectionModeChange() {
        view.setToolSelectionMode();
    }
    
    @Override
    public void onTestDataSelectionModeChange() {
        view.setTestDataSelectMode();
    }
    
    @Override
    public void onOtherDataSeelctionModeChange() {
        view.setOtherDataSelectMode();
    }

    private final Command indicateSuccessCmd = new Command() {
        @Override
        public void execute() {
            view.indicateSubmissionSuccess();
            executeCallback();
        }};

    private Command submitCmd(final Command onSuccess) {
        return new Command() {
            @Override
            public void execute() {
                final List<Uploader> uploaders = getUploadersToSubmit();
                if(uploaders.size() > 0) {
                UploadMux.startUploads(uploaders, new Callback<Void, Iterable<Uploader>>() {
                    @Override
                    public void onFailure(final Iterable<Uploader> failedUploaders) {
                        onUploadFailure(uploaders, failedUploaders);
                    }
                    @Override
                    public void onSuccess(Void unused) {
                        submitRequest(uploaders, onSuccess);
                    }
                });
                } else {
                    submitRequest(uploaders, onSuccess);
                }
        }};
    }

    private void validateUploadsNew(final Iterable<Uploader> uploaders, final Command onValid) {
        final List<String> destFiles = Lists.newArrayList();
        for (Uploader uploader : uploaders) {
            String value = uploader.getValue();
            if(!Strings.isNullOrEmpty(value)) {
                destFiles.add(makeDestinationPath(value));
            }
        }
        if(destFiles.size() > 0) {
            getDiskResourceExistMap(destFiles, checkExistenceCmd(uploaders, onValid));
        } else {
            onValid.execute();
        }
    }

    private Continuation<DiskResourceExistMap> checkExistenceCmd(final Iterable<Uploader> uploaders, final Command onNoneExist) {
        return new Continuation<DiskResourceExistMap>() {
            @Override
            public void execute(final DiskResourceExistMap fsExistMap) {
                boolean allNew = true;
                for (Uploader uploader : uploaders) {
                    if (fsExistMap.get(makeDestinationPath(uploader.getValue()))) {
                        uploader.markInvalid(I18N.ERROR.fileExist());
                        allNew = false;
                    }
                }
                if (allNew) {
                    onNoneExist.execute();
                } else {
                    String dups = "";
                    for (Uploader uploader : uploaders) {
                        dups += " " + uploader.getValue();
                    }
                    view.indicateSubmissionFailure(I18N.ERROR.fileExists(dups));
                }
            }
        };
    }
    
    private void getDiskResourceExistMap(final Iterable<String> files, final Continuation<DiskResourceExistMap> checkExistence) {
        final HasPaths dto = FS_FACTORY.pathsList().as();
        dto.setPaths(Lists.newArrayList(files));
        fsServices.diskResourcesExist(dto, new AsyncCallback<DiskResourceExistMap>() {
            @Override
            public void onFailure(final Throwable caught) {
                view.indicateSubmissionFailure(I18N.ERROR.newToolRequestError());
            }
            @Override
            public void onSuccess(final DiskResourceExistMap fsExistMap) {
                checkExistence.execute(fsExistMap);
            }
        });
    }

    private void submitRequest(final Iterable<Uploader> uploaders, final Command onSuccess) {
        final NewToolRequest req = getToolRequest();
        reqServices.requestInstallation(req, new AsyncCallback<RequestedToolDetails>() {
            @Override
            public void onFailure(final Throwable caught) {
                view.indicateSubmissionFailure(I18N.ERROR.newToolRequestError());
                removeUploads(uploaders);
            }
            @Override
            public void onSuccess(final RequestedToolDetails response) {
                onSuccess.execute();
            }
        });
    }

    private boolean isFormValid() {
        boolean valid = view.isValid();
        if (!valid) {
            return false;
        }
        
        if(toolSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
            if(testDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
                valid = !areUploadsSame(view.getToolBinaryUploader(), view.getTestDataUploader()) && valid;
            }
            
            if(otherDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
                valid = !areUploadsSame(view.getToolBinaryUploader(), view.getOtherDataUploader()) && valid;
            }
        }
      
        if(testDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
            if(toolSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
                valid = !areUploadsSame(view.getToolBinaryUploader(), view.getTestDataUploader()) && valid;
            }
            
            if(otherDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
                valid = !areUploadsSame(view.getTestDataUploader(), view.getOtherDataUploader()) && valid;
            }
        }
        
        if(otherDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
            if(toolSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
                valid = !areUploadsSame(view.getToolBinaryUploader(), view.getOtherDataUploader()) && valid;
            }
            
            if(testDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
                valid = !areUploadsSame(view.getTestDataUploader(), view.getOtherDataUploader()) && valid;
            }
        }
        
        return valid;
    }

    private boolean areUploadsSame(final Uploader lhs, final Uploader rhs) {
        if(Strings.isNullOrEmpty(lhs.getValue()) || Strings.isNullOrEmpty(rhs.getValue())) {
            return false;
        }
        
        if (lhs.getValue().equals(rhs.getValue())) {
            lhs.markInvalid(I18N.ERROR.duplicateUpload());
            rhs.markInvalid(I18N.ERROR.duplicateUpload());
            return true;
        }
        return false;
    }

    private void onUploadFailure(final Iterable<Uploader> allUploaders, final Iterable<Uploader> failedUploaders) {
        final Set<Uploader> succUploaders = Sets.newHashSet(allUploaders);
        final List<String> failedFiles = Lists.newArrayList();
        for (Uploader failure : failedUploaders) {
            failure.markInvalid(I18N.ERROR.fileUploadFailedAnon());
            succUploaders.remove(failure);
            failedFiles.add(failure.getValue());
        }
        view.indicateSubmissionFailure(I18N.ERROR.fileUploadsFailed(failedFiles));
        removeUploads(succUploaders);
    }

    private NewToolRequest getToolRequest() {
        final NewToolRequest req = REQ_FACTORY.makeNewToolRequest().as();
        req.setName(view.getNameField().getValue());
        req.setDescription(view.getDescriptionField().getValue());
        req.setAttribution(view.getAttributionField().getValue());
        if (toolSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
            req.setSourceFile(makeDestinationPath(getToolBinaryName()));
        } else if(toolSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.LINK)) {
            req.setSourceURL(view.getSourceURLField().getValue());
        } else if (toolSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.SELECT)) {
            req.setSourceFile(view.getBinSelectField().getValue().getPath());
        }
        req.setDocURL(view.getDocURLField().getValue());
        req.setVersion(view.getVersionField().getValue());
        req.setArchitecture(view.getArchitectureField().getValue());
        if (view.getMultithreadedField().getValue() != YesNoMaybe.MAYBE) {
            req.setMultithreaded(Boolean.parseBoolean(view.getMultithreadedField().getValue().toString()));
        }
        if(testDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
            req.setTestDataFile(makeDestinationPath(getTestDataName()));
        } else if(testDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.SELECT)) {
            req.setTestDataFile(view.getTestDataSelectField().getValue().getPath());
        }
        req.setInstructions(view.getInstructionsField().getValue());
        if (otherDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD) && !view.getOtherDataUploader().getValue().isEmpty()) {
            req.setAdditionalDataFile(makeDestinationPath(getOtherDataName()));
        } else if (otherDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.SELECT) && !(view.getOtherDataSelectField().getValue() == null)) {
            req.setAdditionalDataFile(view.getOtherDataSelectField().getValue().getPath());
        }    
        req.setAdditionaInfo(view.getAdditionalInfoField().getValue());
        return req;
    }

    private void executeCallback() {
        if (callback != null) {
            callback.execute();
        }
    }

    private String getOtherDataName() {
        return view.getOtherDataUploader().getValue();
    }

    private String getTestDataName() {
        return view.getTestDataUploader().getValue();
    }

    private String getToolBinaryName() {
        return view.getToolBinaryUploader().getValue();
    }

    private String makeDestinationPath(final String file) {
        return diskResourceUtil.appendNameToPath(UserInfo.getInstance().getHomePath(), file);
    }

    private List<Uploader> getUploadersToSubmit() {
        final List<Uploader> uploaders = Lists.newArrayList();
        if (toolSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
            uploaders.add(view.getToolBinaryUploader());
        }
        if(testDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD)) {
            uploaders.add(view.getTestDataUploader());
        }
        if (otherDataSelectionMode.equals(NewToolRequestFormView.SELECTION_MODE.UPLOAD) && !view.getOtherDataUploader().getValue().isEmpty()) {
            uploaders.add(view.getOtherDataUploader());
        }
        return uploaders;
    }

    private void removeUploads(final Iterable<Uploader> uploaders) {
        final List<String> filesToDelete = Lists.newArrayList();
        for (Uploader uploader : uploaders) {
            filesToDelete.add(makeDestinationPath(uploader.getValue()));
        }
        if (!filesToDelete.isEmpty()) {
            final HasPaths dto = FS_FACTORY.pathsList().as();
            dto.setPaths(filesToDelete);
            fsServices.deleteDiskResources(dto, new AsyncCallback<HasPaths>() {
                @Override
                public void onFailure(final Throwable unused) {}
                @Override
                public void onSuccess(final HasPaths unused) {}
            });
        }
    }

}
