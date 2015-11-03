package org.iplantc.de.tools.requests.client.presenter;

import org.iplantc.de.tools.requests.client.views.Uploader;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.Callback;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;
import com.google.web.bindery.event.shared.HandlerRegistration;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;

import java.util.List;

// TODO move this to ui-commons

/**
 * This class manages a bunch of Uploader uploads happening in parallel. It provides a single class
 * method, startUpload, that manages the uploads.
 */
public final class UploadMux {

    private static final class Upload {
        private final Callback<Void, Uploader> onComplete;
        private final Uploader uploader;

        private HandlerRegistration handlerReg = null;

        Upload(final Uploader uploader, final Callback<Void, Uploader> onComplete) {
            this.uploader = uploader;
            this.onComplete = onComplete;
        }

        boolean isActive() {
            return handlerReg != null;
        }

        void start() {
            handlerReg = uploader.addSubmitCompleteHandler(new SubmitCompleteHandler() {
                @Override
                public void onSubmitComplete(final SubmitCompleteEvent event) {
                    complete(event.getResults());
                }
            });
            uploader.submit();
        }

        private void complete(final String jsonResults) {
            handlerReg.removeHandler();
            handlerReg = null;

            final Splittable split = StringQuoter.split(Format.stripTags(jsonResults));
            if (split.isNull("file")) {
                onComplete.onFailure(uploader);
            } else {
                onComplete.onSuccess(null);
            }
        }
    }

    /**
     * Given a collection of Uploader objects, it calls submit on each one. When all of the uploads
     * are complete, it executes the provided callback, passing a list of the Uploaders that failed
     * back if any of them failed.
     * 
     * @param uploaders The list of uploaders to call submit on.
     * @param onAllComplete the callback to execute upon completion.
     */
    public static void startUploads(final Iterable<Uploader> uploaders, final Callback<Void, Iterable<Uploader>> onAllComplete) {
        final UploadMux mux = new UploadMux(uploaders, onAllComplete);
        mux.startUploads();
    }

    private final Callback<Void, Iterable<Uploader>> onAllComplete;
    private final List<Uploader> failedUploaders;
    private final List<Upload> uploads;

    private UploadMux(final Iterable<Uploader> uploaders, final Callback<Void, Iterable<Uploader>> onAllComplete) {
        this.onAllComplete = onAllComplete;
        failedUploaders = Lists.newArrayList();
        uploads = Lists.newArrayList();
        for (Uploader uploader : Sets.newHashSet(uploaders)) {
            uploads.add(new Upload(uploader, new Callback<Void, Uploader>() {
                @Override
                public void onFailure(final Uploader failure) {
                    failedUploaders.add(failure);
                    handleUploadComplete();
                }
                @Override
                public void onSuccess(Void unused) {
                    handleUploadComplete();
                }
            }));
        }
    }

    private void startUploads() {
        failedUploaders.clear();
        if (uploads.isEmpty()) {
            onAllComplete.onSuccess(null);
        } else {
            for (Upload upload : uploads) {
                upload.start();
            }
        }
    }

    private void handleUploadComplete() {
        for (Upload upload : uploads) {
            if (upload.isActive()) {
                return;
            }
        }
        if (failedUploaders.isEmpty()) {
            onAllComplete.onSuccess(null);
        } else {
            onAllComplete.onFailure(failedUploaders);
        }
    }

}
