package org.iplantc.de.diskResource.client.search.views;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;
import org.iplantc.de.commons.client.events.SubmitTextSearchEvent;
import org.iplantc.de.commons.client.events.SubmitTextSearchEvent.SubmitTextSearchEventHandler;
import org.iplantc.de.commons.client.widgets.search.SearchFieldDecorator;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;
import org.iplantc.de.diskResource.client.search.views.cells.DiskResourceSearchCell;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;

import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.form.TriggerField;

import java.text.ParseException;

/**
 * This class is a clone-and-own of {@link DateField}.
 * 
 * @author jstroot
 * 
 */
public class DiskResourceSearchField extends TriggerField<String> implements HasExpandHandlers, HasCollapseHandlers, SaveDiskResourceQueryEvent.HasSaveDiskResourceQueryEventHandlers, HasSubmitDiskResourceQueryEventHandlers,
        SubmitDiskResourceQueryEventHandler, SubmitTextSearchEventHandler {

    public final class QueryStringPropertyEditor extends PropertyEditor<String> {
        private final SearchAutoBeanFactory factory = GWT.create(SearchAutoBeanFactory.class);
        @Override
        public String parse(CharSequence text) throws ParseException {
            clearInvalid();

            DiskResourceQueryTemplate qt = factory.dataSearchFilter().as();
            qt.setFileQuery(text.toString());
            getCell().fireEvent(new SubmitDiskResourceQueryEvent(qt));
            return text.toString();
        }

        @Override
        public String render(String object) {
            return object;
        }
    }

    /**
     * Creates a new iPlant Search field.
     */
    public DiskResourceSearchField() {
        super(new DiskResourceSearchCell());

        setPropertyEditor(new QueryStringPropertyEditor());
        getCell().addSubmitDiskResourceQueryEventHandler(this);

        // Add search field decorator to enable "auto-search"
        new SearchFieldDecorator<TriggerField<String>>(this).addSubmitTextSearchEventHandler(this);
    }

    @Override
    public HandlerRegistration addCollapseHandler(CollapseHandler handler) {
        return getCell().addCollapseHandler(handler);
    }

    @Override
    public HandlerRegistration addExpandHandler(ExpandHandler handler) {
        return getCell().addExpandHandler(handler);
    }

    @Override
    public HandlerRegistration addSaveDiskResourceQueryEventHandler(SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler handler) {
        return getCell().addSaveDiskResourceQueryEventHandler(handler);
    }

    @Override
    public HandlerRegistration addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEventHandler handler) {
        return getCell().addSubmitDiskResourceQueryEventHandler(handler);
    }

    public void updateSearch(DiskResourceQueryTemplate query) {
        getCell().fireEvent(new SubmitDiskResourceQueryEvent(query));
    }

    public void clearSearch() {
        // Forward clear call to searchForm
        getCell().getSearchForm().clearSearch();
        clearInvalid();
        clear();
    }

    public void edit(DiskResourceQueryTemplate queryTemplate) {
        // Forward edit call to searchForm
        getCell().getSearchForm().edit(queryTemplate);
        clear();
    }

    @Override
    public DiskResourceSearchCell getCell() {
        return (DiskResourceSearchCell)super.getCell();
    }

    protected void expand() {
        getCell().expand(createContext(), getElement(), getValue(), valueUpdater);
    }

    @Override
    protected void onCellParseError(ParseErrorEvent event) {
        super.onCellParseError(event);
        /*
         * String value = event.getException().getMessage();
         * String f = getPropertyEditor().getFormat().getPattern();
         * String msg = DefaultMessages.getMessages().dateField_invalidText(value, f);
         * parseError = msg;
         */
        // TODO Update parse error message
        String msg = "Default message";
        forceInvalid(msg);
    }

    @Override
    public void doSubmitDiskResourceQuery(SubmitDiskResourceQueryEvent event) {
        DiskResourceQueryTemplate query = event.getQueryTemplate();
        if (query == null || Strings.isNullOrEmpty(query.getFileQuery())) {
            clear();
            return;
        }

        String fileQuery = query.getFileQuery();
        if (!fileQuery.equals(getText())) {
            setText(fileQuery);
        }
    }

    @Override
    public void onSubmitTextSearch(SubmitTextSearchEvent event) {
        // Finish editing to fire search event.
        finishEditing();
    }
}
