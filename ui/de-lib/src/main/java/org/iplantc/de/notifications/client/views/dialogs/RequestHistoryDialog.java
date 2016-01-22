package org.iplantc.de.notifications.client.views.dialogs;

import org.iplantc.de.client.models.requestStatus.RequestHistory;
import org.iplantc.de.client.models.requestStatus.RequestHistoryProperties;
import org.iplantc.de.client.models.toolRequest.ToolRequestStatus;
import org.iplantc.de.commons.client.widgets.ContextualHelpPopup;
import org.iplantc.de.notifications.client.views.cells.RequestStatusCell;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.ToolRequestStatusHelpStyle;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import java.util.Date;
import java.util.List;

/**
 * A Dialog for displaying Tool Request Status history in a grid.
 * 
 * @author psarando
 * 
 */
public class RequestHistoryDialog extends Dialog {

    private static ToolRequestHistoryPanelUiBinder uiBinder = GWT
            .create(ToolRequestHistoryPanelUiBinder.class);
    private static RequestHistoryProperties historyProperties = GWT
            .create(RequestHistoryProperties.class);

    @UiTemplate("RequestHistoryPanel.ui.xml")
    interface ToolRequestHistoryPanelUiBinder extends UiBinder<Widget, RequestHistoryDialog> {
    }

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<li class='{2}'>{0}<blockquote class='{3}'>{1}</blockquote></li>")
        SafeHtml statusHelp(String status, String helpText, String statusClassName,
                String helpTextClassName);
    }

    private static Templates templates = GWT.create(Templates.class);

    @UiField
    Grid<RequestHistory> grid;

    @UiField
    ColumnModel<RequestHistory> cm;

    @UiField
    ListStore<RequestHistory> listStore;

    @UiField
    GridView<RequestHistory> gridView;

    @UiFactory
    ColumnModel<RequestHistory> createColumnModel() {
        List<ColumnConfig<RequestHistory, ?>> list = Lists.newArrayList();

        ColumnConfig<RequestHistory, String> status = new ColumnConfig<RequestHistory, String>(
                historyProperties.status(), 50, I18N.DISPLAY.status());
        status.setCell(new RequestStatusCell());

        ColumnConfig<RequestHistory, Date> statusDate = new ColumnConfig<RequestHistory, Date>(
                historyProperties.statusDate(), 100, I18N.DISPLAY.date());
        PredefinedFormat statusDateFormat = DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM;
        statusDate.setCell(new DateCell(DateTimeFormat.getFormat(statusDateFormat)));

        ColumnConfig<RequestHistory, String> comments = new ColumnConfig<RequestHistory, String>(
                historyProperties.comments(), 100, I18N.DISPLAY.comments());

        list.add(status);
        list.add(statusDate);
        list.add(comments);

        return new ColumnModel<RequestHistory>(list);
    }

    @UiFactory
    ListStore<RequestHistory> createListStore() {
        ListStore<RequestHistory> store = new ListStore<RequestHistory>(
                new ModelKeyProvider<RequestHistory>() {

                    @Override
                    public String getKey(RequestHistory item) {
                        return item.getStatus().toString() + item.getStatusDate();
                    }
                });

        return store;
    }

    private ContextualHelpPopup helpPopup;

    public RequestHistoryDialog(String name, List<RequestHistory> history) {
        setHeadingText(name + " - " + I18N.DISPLAY.requestStatus()); //$NON-NLS-1$
        setSize("480", "320"); //$NON-NLS-1$ //$NON-NLS-2$
        setResizable(true);
        setHideOnButtonClick(true);

        add(uiBinder.createAndBindUi(this));

        StoreSortInfo<RequestHistory> sortInfo = new StoreSortInfo<RequestHistory>(
                historyProperties.statusDate(), SortDir.DESC);
        grid.getStore().addSortInfo(sortInfo);
        grid.getStore().addAll(history);

        // Contextual Help
        new QuickTip(grid);
        initHelpPopup();
        initHelpButton();
    }

    private void initHelpPopup() {
        helpPopup = new ContextualHelpPopup();
        helpPopup.add(new HTML(getToolRequestStatusHelpText()));
    }

    private SafeHtml getToolRequestStatusHelpText() {
        ToolRequestStatusHelpStyle statusHelpStyles = IplantResources.RESOURCES
                .getToolRequestStatusHelpCss();
        statusHelpStyles.ensureInjected();

        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendEscaped(I18N.HELP.toolRequestStatusHelp());

        for (ToolRequestStatus status : ToolRequestStatus.values()) {
            sb.append(templates.statusHelp(status.toString(), status.getHelpText(),
                    statusHelpStyles.statusListItem(), statusHelpStyles.statusHelpText()));
        }

        return sb.toSafeHtml();
    }

    private void initHelpButton() {
        final ToolButton help = new ToolButton(ToolButton.QUESTION);

        help.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                helpPopup.showAt(help.getAbsoluteLeft(), help.getAbsoluteTop() + help.getOffsetHeight());
            }
        });

        getHeader().addTool(help);
    }
}
