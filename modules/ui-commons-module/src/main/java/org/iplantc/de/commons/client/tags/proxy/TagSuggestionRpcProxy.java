package org.iplantc.de.commons.client.tags.proxy;

import org.iplantc.de.client.models.tags.IplantTagAutoBeanFactory;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.models.tags.IplantTagList;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.ListLoadResult;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagSuggestionRpcProxy extends RpcProxy<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> {

    private final int LIMIT = 10;
    private final TagsServiceFacade mService;
    IplantTagAutoBeanFactory factory = GWT.create(IplantTagAutoBeanFactory.class);
    Logger logger = Logger.getLogger("Tag Proxy Logger");

    @Inject
    public TagSuggestionRpcProxy(final TagsServiceFacade mService) {
        this.mService = mService;
    }

    @Override
    public void load(TagSuggestionLoadConfig loadConfig, final AsyncCallback<ListLoadResult<IplantTag>> callback) {
        if (loadConfig.getQuery() != null) {
            mService.suggestTag(loadConfig.getQuery(), LIMIT, new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(I18N.ERROR.tagRetrieveError(), caught);

                }

                @SuppressWarnings("serial")
                @Override
                public void onSuccess(final String result) {
                    callback.onSuccess(new ListLoadResult<IplantTag>() {

                        @Override
                        public List<IplantTag> getData() {
                            AutoBean<IplantTagList> tagListBean = AutoBeanCodex.decode(factory, IplantTagList.class, result);
                            List<IplantTag> tagList = tagListBean.as().getTagList();
                            logger.log(Level.SEVERE, tagList.size() + "<--");
                            return tagList;
                        }
                    });

                }
            });
        }

    }

}
