package org.iplantc.de.tags.client.proxy;

import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.models.tags.IplantTagAutoBeanFactory;
import org.iplantc.de.client.models.tags.IplantTagList;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.TagsView;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.ListLoadResult;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author jstroot
 */
public class TagSuggestionProxyImpl extends RpcProxy<TagSuggestionLoadConfig, ListLoadResult<Tag>> implements TagsView.TagSuggestionProxy {

    private final int LIMIT = 10;
    private final TagsServiceFacade mService;
    IplantTagAutoBeanFactory factory;
    Logger logger = Logger.getLogger(TagSuggestionProxyImpl.class.getName());

    @Inject
    TagSuggestionProxyImpl(final TagsServiceFacade mService,
                           final IplantTagAutoBeanFactory factory) {
        this.mService = mService;
        this.factory = factory;
    }

    @Override
    public void load(TagSuggestionLoadConfig loadConfig, final AsyncCallback<ListLoadResult<Tag>> callback) {
        if (loadConfig.getQuery() != null) {
            mService.suggestTag(loadConfig.getQuery(), LIMIT, new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(I18N.ERROR.tagRetrieveError(), caught);
                }

                @SuppressWarnings("serial")
                @Override
                public void onSuccess(final String result) {
                    callback.onSuccess(new ListLoadResult<Tag>() {

                        @Override
                        public List<Tag> getData() {
                            AutoBean<IplantTagList> tagListBean = AutoBeanCodex.decode(factory, IplantTagList.class, result);
                            List<Tag> tagList = tagListBean.as().getTagList();
                            logger.fine(tagList.size() + "<--");
                            return tagList;
                        }
                    });

                }
            });
        }

    }

}
