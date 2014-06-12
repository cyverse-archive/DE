package org.iplantc.de.commons.client.tags.proxy;

import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.tags.models.IpalntTagAutoBeanFactory;
import org.iplantc.de.commons.client.tags.models.IplantTag;
import org.iplantc.de.commons.client.tags.models.IplantTagList;

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
    private final MetadataServiceFacade mService;
    IpalntTagAutoBeanFactory factory = GWT.create(IpalntTagAutoBeanFactory.class);
    Logger logger = Logger.getLogger("Tag Proxy Logger");

    @Inject
    public TagSuggestionRpcProxy(final MetadataServiceFacade mService) {
        this.mService = mService;
    }

    @Override
    public void load(TagSuggestionLoadConfig loadConfig, final AsyncCallback<ListLoadResult<IplantTag>> callback) {
        if (loadConfig.getQuery() != null && loadConfig.getQuery().length() > 2) {
            mService.suggestTag(loadConfig.getQuery(), LIMIT, new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post("unable to retrieve tags!", caught);

                }

                @SuppressWarnings("serial")
                @Override
                public void onSuccess(final String result) {
                    callback.onSuccess(new ListLoadResult<IplantTag>() {

                        @Override
                        public List<IplantTag> getData() {
                            // String test = "{\n" + "    \"tags\": [\n" + "        {\n" +
                            // "            \"id\": \"tag-id1\",\n" +
                            // "            \"value\": \"thetag1\",\n"
                            // + "            \"description\": \"thedescriptionofthetag\"\n" +
                            // "        },\n" + "        {\n" + "            \"id\": \"tag-id2\",\n"
                            // + "            \"value\": \"thetag2\",\n" +
                            // "            \"description\": \"thedescriptionofthetag\"\n" +
                            // "        },\n" + "        {\n"
                            // + "            \"id\": \"tag-id3\",\n" +
                            // "            \"value\": \"thetag3\",\n" +
                            // "            \"description\": \"thedescriptionofthetag\"\n" +
                            // "        },\n"
                            // + "        {\n" + "            \"id\": \"tag-id4\",\n" +
                            // "            \"value\": \"thetag4\",\n" +
                            // "            \"description\": \"thedescriptionofthetag\"\n"
                            // + "        },\n" + "        {\n" + "            \"id\": \"tag-id5\",\n" +
                            // "            \"value\": \"thetag5\",\n"
                            // + "            \"description\": \"thedescriptionofthetag\"\n" +
                            // "        }\n" + "    ]\n" + "}";
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
