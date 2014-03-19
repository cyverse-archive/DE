package org.iplantc.de.commons.client.widgets;

import org.iplantc.de.commons.client.widgets.search.SearchFieldDecorator;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.sencha.gxt.cell.core.client.form.TextInputCell;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * A TextField used for fetching filtered results with a given PagingLoader and a FilterPagingLoadConfig.
 * If the given PagingLoader does not already reuse a FilterPagingLoadConfig, this class will create one
 * before loading the Loader.
 * 
 * @author psarando
 * @deprecated use {@link SearchFieldDecorator} instead.
 * 
 */
@Deprecated
public class SearchField<T> extends TextField {
    protected final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<T>> loader;
    private int minChars = 3;
    private final DelayedTask dqTask;
    private final SearchFieldKeyUpHandler searchQueryHandler;

    public SearchField(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<T>> loader) {
        super(new SearchFieldCell());

        this.loader = loader;

        dqTask = new DelayedTask() {
            @Override
            public void onExecute() {
                doQuery(getCurrentValue());
            }
        };

        ((SearchFieldCell)getCell()).setEnterKeyHandler(dqTask);
        searchQueryHandler = new SearchFieldKeyUpHandler(dqTask);

        addKeyUpHandler(searchQueryHandler);

        initSize();
    }

    /**
     * Set max input field size.
     */
    private void initSize() {
        setWidth(255);
        InputElement inputField = getInputEl().cast();
        inputField.setMaxLength(255);
    }

    /**
     * Returns the min characters used for activating filtering.
     * 
     * @return the minimum number of characters
     */
    public int getMinChars() {
        return minChars;
    }

    /**
     * Sets the minimum number of characters the user must type before filtering activates (defaults to
     * 3).
     * 
     * @param minChars
     */
    public void setMinChars(int minChars) {
        this.minChars = minChars;
    }

    /**
     * Returns the query delay.
     * 
     * @return the query delay
     */
    public int getQueryDelay() {
        return searchQueryHandler.getQueryDelay();
    }

    /**
     * The length of time in milliseconds to delay between the start of typing and sending the query to
     * the Loader.
     * 
     * @param queryDelay the query delay
     */
    public void setQueryDelay(int queryDelay) {
        searchQueryHandler.setQueryDelay(queryDelay);
    }

    /**
     * Loads the Loader with the given value set in its Filters, if the value meets the min-chars limit.
     * If the value is empty, then calls {@link SearchField#clearSearchField()} is called.
     * 
     * @param filterValue
     */
    public void doQuery(String filterValue) {
        if (Strings.isNullOrEmpty(filterValue)) {
            clearSearchField();
        } else {
            if (filterValue.length() >= minChars) {
                loader.load(getParams(filterValue));
            }
        }
    }

    /**
     * Clears the value in the text box, then calls {@link SearchField#clearFilter()}.
     */
    protected void clearSearchField() {
        setValue(null);
        clearFilter();
    }

    /**
     * Tells the loader to load with its Filter values cleared.
     */
    protected void clearFilter() {
        loader.load(getParams(null));
    }

    protected FilterPagingLoadConfig getParams(String query) {
        FilterPagingLoadConfig config = getLoaderConfig();

        List<FilterConfig> filters = getConfigFilters(config);

        if (filters.isEmpty()) {
            FilterConfigBean filter = new FilterConfigBean();
            filters.add(filter);
        }

        for (FilterConfig filter : filters) {
            filter.setValue(query);
        }

        config.setOffset(0);

        return config;
    }

    protected FilterPagingLoadConfig getLoaderConfig() {
        FilterPagingLoadConfig config;
        if (loader.isReuseLoadConfig()) {
            config = loader.getLastLoadConfig();
        } else {
            config = new FilterPagingLoadConfigBean();
        }

        return config;
    }

    protected List<FilterConfig> getConfigFilters(FilterPagingLoadConfig config) {
        List<FilterConfig> filters = config.getFilters();
        if (filters == null) {
            filters = new ArrayList<FilterConfig>();
            config.setFilters(filters);
        }

        return filters;
    }

    /**
     * A KeyUpHandler that calls the given DelayedTask if the KeyUpEvent key is not a modifier key.
     * 
     * @author psarando
     * 
     */
    private class SearchFieldKeyUpHandler implements KeyUpHandler {
        private int queryDelay = 500;
        private final DelayedTask dqTask;

        public SearchFieldKeyUpHandler(DelayedTask dqTask) {
            this.dqTask = dqTask;
        }

        public int getQueryDelay() {
            return queryDelay;
        }

        public void setQueryDelay(int queryDelay) {
            this.queryDelay = queryDelay;
        }

        private boolean isModifierKey(int keyCode) {
            switch (keyCode) {
                case KeyCodes.KEY_ENTER:
                    // Enter is special cased by TextInputCells, so it's handled by SearchFieldCell.
                case KeyCodes.KEY_ALT:
                case KeyCodes.KEY_CTRL:
                case KeyCodes.KEY_END:
                case KeyCodes.KEY_ESCAPE:
                case KeyCodes.KEY_HOME:
                case KeyCodes.KEY_PAGEDOWN:
                case KeyCodes.KEY_PAGEUP:
                case KeyCodes.KEY_SHIFT:
                case KeyCodes.KEY_TAB:
                    return true;
                default:
                    return KeyCodeEvent.isArrow(keyCode);
            }
        }

        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (!isReadOnly() && !isModifierKey(event.getNativeKeyCode())) {
                // Delay triggering the query.
                if (dqTask != null) {
                    dqTask.delay(queryDelay);
                }
            }
        }
    }

    /**
     * A TextInputCell that calls a given DelayedTask in its onEnterKeyDown method.
     * 
     * @author psarando
     * 
     */
    private static class SearchFieldCell extends TextInputCell {
        private DelayedTask handler;

        public void setEnterKeyHandler(DelayedTask handler) {
            this.handler = handler;
        }

        @Override
        protected void onEnterKeyDown(Context context, Element parent, String value, NativeEvent event,
                ValueUpdater<String> valueUpdater) {
            if (handler != null) {
                handler.delay(0);
            }
        }
    }
}
