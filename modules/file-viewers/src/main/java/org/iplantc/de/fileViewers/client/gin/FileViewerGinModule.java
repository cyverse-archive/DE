package org.iplantc.de.fileViewers.client.gin;

import org.iplantc.de.fileViewers.client.presenter.FileViewerPresenterImpl;
import org.iplantc.de.fileViewers.client.presenter.MimeTypeViewerResolverFactory;
import org.iplantc.de.fileViewers.client.views.FileViewer;

import com.google.gwt.inject.client.AbstractGinModule;

public class FileViewerGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(FileViewer.Presenter.class).to(FileViewerPresenterImpl.class);
        bind(MimeTypeViewerResolverFactory.class);
    }

}
