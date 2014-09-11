package org.iplantc.admin.belphegor.client.gin;

import org.iplantc.admin.belphegor.client.BelphegorResources;
import org.iplantc.admin.belphegor.client.apps.presenter.BelphegorAppsViewPresenterImpl;
import org.iplantc.admin.belphegor.client.refGenome.RefGenomeView;
import org.iplantc.admin.belphegor.client.systemMessage.SystemMessageView;
import org.iplantc.admin.belphegor.client.toolRequest.ToolRequestView;
import org.iplantc.admin.belphegor.client.views.BelphegorView;
import org.iplantc.de.shared.services.DiscEnvApiService;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(BelphegorAppsGinModule.class)
public interface BelphegorAppInjector extends Ginjector {

    BelphegorView.Presenter getBelphegorPresenter();

    BelphegorAppsViewPresenterImpl getAppsViewPresenter();

    RefGenomeView.Presenter getReferenceGenomePresenter();

    ToolRequestView.Presenter getToolRequestPresenter();

    SystemMessageView.Presenter getSystemMessagePresenter();

    DiscEnvApiService getApiService();

    BelphegorResources getResources();
}
