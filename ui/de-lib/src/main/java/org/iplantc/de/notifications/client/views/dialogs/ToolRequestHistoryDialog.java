package org.iplantc.de.notifications.client.views.dialogs;

import org.iplantc.de.client.models.toolRequest.ToolRequestHistory;
import org.iplantc.de.client.models.toolRequest.ToolRequestStatus;
import org.iplantc.de.commons.client.widgets.ContextualHelpPopup;
import org.iplantc.de.notifications.client.views.ToolRequestHistoryProperties;
import org.iplantc.de.notifications.client.views.cells.ToolRequestStatusCell;
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
public class ToolRequestHistoryDialog extends Dialog {

    private static ToolRequestHistoryPanelUiBinder uiBinder = GWT
            .create(ToolRequestHistoryPanelUiBinder.class);
    private static ToolRequestHistoryProperties historyProperties = GWT
            .create(ToolRequestHistoryProperties.class);

    @UiTemplate("ToolRequestHistoryPanel.ui.xml")
    interface ToolRequestHistoryPanelUiBinder extends UiBinder<Widget, ToolRequestHistoryDialog> {
    }

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<li class='{2}'>{0}<blockquote class='{3}'>{1}</blockquote></li>")
        SafeHtml statusHelp(String status, String helpText, String statusClassName,
                String helpTextClassName);
    }

    private static Templates templates = GWT.create(Templates.class);

    @UiField
    Grid<ToolRequestHistory> grid;

    @UiField
    ColumnModel<ToolRequestHistory> cm;

    @UiField
    ListStore<ToolRequestHistory> listStore;

    @UiField
    GridView<ToolRequestHistory> gridView;

    @UiFactory
    ColumnModel<ToolRequestHistory> createColumnModel() {
        List<ColumnConfig<ToolRequestHistory, ?>> list = Lists.newArrayList();

        ColumnConfig<ToolRequestHistory, String> status = new ColumnConfig<ToolRequestHistory, String>(
                historyProperties.status(), 50, I18N.DISPLAY.status());
        status.setCell(new ToolRequestStatusCell());

        ColumnConfig<ToolRequestHistory, Date> statusDate = new ColumnConfig<ToolRequestHistory, Date>(
                historyProperties.statusDate(), 100, I18N.DISPLAY.date());
        PredefinedFormat statusDateFormat = DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM;
        statusDate.setCell(new DateCell(DateTimeFormat.getFormat(statusDateFormat)));

        ColumnConfig<ToolRequestHistory, String> comments = new ColumnConfig<ToolRequestHistory, String>(
                historyProperties.comments(), 100, I18N.DISPLAY.comments());

        list.add(status);
        list.add(statusDate);
        list.add(comments);

        return new ColumnModel<ToolRequestHistory>(list);
    }

    @UiFactory
    ListStore<ToolRequestHistory> createListStore() {
        ListStore<ToolRequestHistory> store = new ListStore<ToolRequestHistory>(
                new ModelKeyProvider<ToolRequestHistory>() {

                    @Override
                    public String getKey(ToolRequestHistory item) {
                        return item.getStatus().toString() + item.getStatusDate();
                    }
                });

        return store;
    }

    private ContextualHelpPopup helpPopup;

    public ToolRequestHistoryDialog(String name, List<ToolRequestHistory> history) {
        setHeadingText(name + " - " + I18N.DISPLAY.toolRequestStatus()); //$NON-NLS-1$
        setSize("480", "320"); //$NON-NLS-1$ //$NON-NLS-2$
        setResizable(true);
        setHideOnButtonClick(true);

        add(uiBinder.createAndBindUi(this));

        StoreSortInfo<ToolRequestHistory> sortInfo = new StoreSortInfo<ToolRequestHistory>(
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
