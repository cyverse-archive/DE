package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.SearchServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.List;

public class SearchServiceFacadeStub implements SearchServiceFacade {
    @Override
    public List<DiskResourceQueryTemplate> createFrozenList(List<DiskResourceQueryTemplate> queryTemplates) {
        return null;
    }

    @Override
    public void getSavedQueryTemplates(AsyncCallback<List<DiskResourceQueryTemplate>> callback) {

    }

    @Override
    public void saveQueryTemplates(List<DiskResourceQueryTemplate> queryTemplates, AsyncCallback<List<DiskResourceQueryTemplate>> callback) {

    }

    @Override
    public void submitSearchFromQueryTemplate(DiskResourceQueryTemplate queryTemplate, FilterPagingLoadConfigBean loadConfig, TYPE searchType, AsyncCallback<List<DiskResource>> callback) {

    }

    @Override
    public void deleteQueryTemplates(List<DiskResourceQueryTemplate> queryTemplates,
                                     AsyncCallback<List<DiskResourceQueryTemplate>> callback) {


    }
}
