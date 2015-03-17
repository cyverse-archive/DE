package org.iplantc.de.tags.client.proxy;

import com.sencha.gxt.data.shared.loader.ListLoadConfigBean;

public class TagSuggestionLoadConfig extends ListLoadConfigBean {

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
