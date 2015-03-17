package org.iplantc.de.analysis.client.gin;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.inject.Provider;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author jstroot
 */
public class AnalysisModuleListStoreProvider implements Provider<ListStore<Analysis>> {
    private static class AnalysisModelKeyProvider implements ModelKeyProvider<Analysis> {
        @Override
        public String getKey(Analysis item) {
            return item.getId();
        }
    }

    @Override
    public ListStore<Analysis> get() {
        return new ListStore<>(new AnalysisModelKeyProvider());
    }
}
