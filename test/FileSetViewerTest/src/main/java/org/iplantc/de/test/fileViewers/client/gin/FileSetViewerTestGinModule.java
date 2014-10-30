package org.iplantc.de.test.fileViewers.client.gin;

import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.test.fileViewers.client.serviceStubs.FileEditorServiceFacadeStub;

import com.google.gwt.inject.client.AbstractGinModule;

public class FileSetViewerTestGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(FileEditorServiceFacade.class).to(FileEditorServiceFacadeStub.class);
    }
}
