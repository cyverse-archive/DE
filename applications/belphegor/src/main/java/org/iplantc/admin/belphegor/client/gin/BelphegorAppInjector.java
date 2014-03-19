package org.iplantc.admin.belphegor.client.gin;

import org.iplantc.admin.belphegor.client.BelphegorResources;
import org.iplantc.admin.belphegor.client.apps.presenter.BelphegorAppsViewPresenter;
import org.iplantc.admin.belphegor.client.refGenome.RefGenomeView;
import org.iplantc.admin.belphegor.client.systemMessage.SystemMessageView;
import org.iplantc.admin.belphegor.client.toolRequest.ToolRequestView;
import org.iplantc.admin.belphegor.client.views.BelphegorView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(BelphegorAppsGinModule.class)
public interface BelphegorAppInjector extends Ginjector {

    public static final BelphegorAppInjector INSTANCE = GWT.create(BelphegorAppInjector.class);

    BelphegorView.Presenter getBelphegorPresenter();

    BelphegorAppsViewPresenter getAppsViewPresenter();

    RefGenomeView.Presenter getReferenceGenomePresenter();

    ToolRequestView.Presenter getToolRequestPresenter();

    SystemMessageView.Presenter getSystemMessagePresenter();

    BelphegorResources getResources();
}
