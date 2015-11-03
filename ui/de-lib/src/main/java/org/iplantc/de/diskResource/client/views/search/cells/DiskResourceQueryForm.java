package org.iplantc.de.diskResource.client.views.search.cells;

import org.iplantc.de.client.models.search.DateInterval;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.FileSizeRange.FileSizeUnit;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;
import org.iplantc.de.client.models.search.SearchModelUtils;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.events.search.SaveDiskResourceQueryClickedEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.events.TagAddedEvent;
import org.iplantc.de.tags.client.events.selection.RemoveTagSelected;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This form is used to construct, edit and/or save "search filters".
 * <p/>
 * <p/>
 * This form may be constructed with or without an existing query template. If a query template is
 * supplied to the constructor, the form will be initialized with given query template. If the default
 * constructor is used, a new template will be created.
 * <p/>
 * <p/>
 * When the user clicks the "Search" button;
 * <ol>
 * <li>The form will be validated
 * <ol>
 * <li>If the form is <b>invalid</b>, the validation errors will appear in the form and no other action
 * will occur.</li>
 * <li>Else, a {@link SubmitDiskResourceQueryEvent} will be fired with the form's current query template,
 * and this form will be hidden.</li>
 * </ol>
 * </li>
 * </ol>
 * <p/>
 * <p/>
 * When the user clicks the "" hyperlink;
 * <ol>
 * <li>The form will be validated
 * <ol>
 * <li>If the form is <b>invalid</b>, the validation errors will appear in the form and not other action
 * will occur.</li>
 * <li>Else, the user will be presented with a text field allowing them to set a name. Then, if the user
 * clicks "Save", a {@link org.iplantc.de.diskResource.client.events.search.SaveDiskResourceQueryClickedEvent}
 * will be fired with the form's current query template and this form will be hidden.</li>
 * </ol>
 * </li>
 * </ol>
 * FIXME Re-implement with UI-binder
 * @author jstroot
 */
public class DiskResourceQueryForm extends Composite implements
                                                     Editor<DiskResourceQueryTemplate>,
                                                     SaveDiskResourceQueryClickedEvent.HasSaveDiskResourceQueryClickedEventHandlers,
                                                     HasSubmitDiskResourceQueryEventHandlers,
                                                     SaveDiskResourceQueryClickedEvent.SaveDiskResourceQueryClickedEventHandler,
                                                     TagAddedEvent.TagAddedEventHandler,
                                                     RemoveTagSelected.RemoveTagSelectedHandler {

    public interface HtmlLayoutContainerTemplate extends XTemplates {
        @XTemplate(source = "DiskResourceQueryFormTemplate.html")
        SafeHtml getTemplate();
    }

    interface SearchFormEditorDriver extends SimpleBeanEditorDriver<DiskResourceQueryTemplate, DiskResourceQueryForm> { }

    protected final BaseEventPreview eventPreview;
    static final int COLUMN_FORM_WIDTH = 600;
    static final int cw = ((COLUMN_FORM_WIDTH - 30) / 2) - 12;
    static final Logger LOG = Logger.getLogger(DiskResourceQueryForm.class.getName());
    final SearchFormEditorDriver editorDriver = GWT.create(SearchFormEditorDriver.class);
    IPlantAnchor createFilterLink;
    @Path("createdWithin")
    SimpleComboBox<DateInterval> createdWithinCombo;
    TextField fileQuery;
    @Path("fileSizeRange.min")
    NumberField<Double> fileSizeGreaterThan;
    @Path("fileSizeRange.max")
    NumberField<Double> fileSizeLessThan;
    FieldLabel greaterField;
    @Path("fileSizeRange.minUnit")
    SimpleComboBox<FileSizeUnit> greaterThanComboBox;
    CheckBox includeTrashItems;
    @Path("fileSizeRange.maxUnit")
    SimpleComboBox<FileSizeUnit> lessThanComboBox;
    FieldLabel lesserField;
    TextField metadataAttributeQuery;
    TextField metadataValueQuery;
    @Path("modifiedWithin")
    SimpleComboBox<DateInterval> modifiedWithinCombo;
    @Ignore DiskResourceQueryFormNamePrompt namePrompt;
    TextField negatedFileQuery;
    TextField ownedBy;
    @Ignore
    TextButton searchButton;
    TextField sharedWith;
    final SimpleEditor<Set<Tag>> tagQuery;
    @Ignore
    private final HtmlLayoutContainer con;
    private final SearchAutoBeanFactory factory = GWT.create(SearchAutoBeanFactory.class);
    private final TagsView.Presenter tagsViewPresenter;
    @Ignore
    private boolean showing;

    /**
     * Creates the form with a new filter.
     */
    @Inject
    DiskResourceQueryForm(final TagsView.Presenter tagsViewPresenter) {
        this(tagsViewPresenter,
             SearchModelUtils.createDefaultFilter());
    }

    DiskResourceQueryForm(final TagsView.Presenter tagsViewPresenter,
                          final DiskResourceQueryTemplate filter) {
        this.tagsViewPresenter = tagsViewPresenter;
        this.tagsViewPresenter.setRemovable(true);
        this.tagsViewPresenter.setEditable(true);
        VerticalPanel vp = new VerticalPanel();
        HtmlLayoutContainerTemplate templates = GWT.create(HtmlLayoutContainerTemplate.class);
        con = new HtmlLayoutContainer(templates.getTemplate());
        vp.add(con);
        vp.getElement().getStyle().setBackgroundColor("#fff");
        initWidget(vp);

        this.tagsViewPresenter.getView().addTagAddedEventHandler(this);
        this.tagsViewPresenter.getView().addRemoveTagSelectedHandler(this);
        tagQuery = SimpleEditor.of();
        init(new DiskResourceQueryFormNamePrompt());
        editorDriver.initialize(this);
        editorDriver.edit(filter);
        tagQuery.setValue(new HashSet<Tag>());

        eventPreview = new BaseEventPreview() {

            @Override
            protected boolean onPreview(NativePreviewEvent pe) {
                DiskResourceQueryForm.this.onPreviewEvent(pe);
                return super.onPreview(pe);
            }

            @Override
            protected void onPreviewKeyPress(NativePreviewEvent pe) {
                super.onPreviewKeyPress(pe);
                onEscape(pe);
            }

        };
        eventPreview.getIgnoreList().add(getElement());
        eventPreview.setAutoHide(false);
        addStyleName("x-ignore");
        con.setBorders(true);
        // JDS Small trial to correct placement of form in constrained views.
        this.ensureVisibilityOnSizing = true;

        List<FieldLabel> labels = FormPanelHelper.getFieldLabels(vp);
        for (FieldLabel lbl : labels) {
            lbl.setLabelAlign(LabelAlign.TOP);
        }
    }

    static boolean isEmptyQuery(DiskResourceQueryTemplate template) {
        if (Strings.isNullOrEmpty(template.getOwnedBy())
                && Strings.isNullOrEmpty(template.getFileQuery())
                && Strings.isNullOrEmpty(template.getMetadataAttributeQuery())
                && Strings.isNullOrEmpty(template.getMetadataValueQuery())
                && Strings.isNullOrEmpty(template.getNegatedFileQuery())
                && Strings.isNullOrEmpty(template.getSharedWith())
                && (template.getDateCreated() == null)
                && (template.getLastModified() == null)
                && ((template.getCreatedWithin() == null) || (template.getCreatedWithin().getFrom() == null && template.getCreatedWithin()
                                                                                                                       .getTo() == null))
                && ((template.getModifiedWithin() == null) || (template.getModifiedWithin().getFrom() == null && template.getModifiedWithin()
                                                                                                                         .getTo() == null))
                && ((template.getFileSizeRange() == null) || (template.getFileSizeRange().getMax() == null && template.getFileSizeRange()
                                                                                                                      .getMin() == null))
                && (template.getTagQuery() == null || template.getTagQuery().size() == 0)) {

            LOG.fine("tags size==>" + template.getTagQuery());

            IplantAnnouncer.getInstance().schedule(new ErrorAnnouncementConfig("You must select at least one filter."));
            return true;
        }
        return false;
    }

    @Override
    public HandlerRegistration addSaveDiskResourceQueryClickedEventHandler(SaveDiskResourceQueryClickedEvent.SaveDiskResourceQueryClickedEventHandler handler) {
        return addHandler(handler, SaveDiskResourceQueryClickedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEventHandler handler) {
        return addHandler(handler, SubmitDiskResourceQueryEvent.TYPE);
    }

    /**
     * Clears search form by binding it to a new default query template
     */
    public void clearSearch() {
        tagsViewPresenter.removeAll();
        editorDriver.edit(SearchModelUtils.createDefaultFilter());
    }

    public void edit(DiskResourceQueryTemplate queryTemplate) {
        if (queryTemplate.getTagQuery() == null) {
            tagQuery.setValue(new HashSet<Tag>());
            tagsViewPresenter.removeAll();
        } else {
            for (Tag it : queryTemplate.getTagQuery()) {
                tagsViewPresenter.addTag(it);
            }
        }
        editorDriver.edit(SearchModelUtils.copyDiskResourceQueryTemplate(queryTemplate));
    }

    @Override
    public void hide() {
        if (showing) {
            onHide();
            RootPanel.get().remove(this);
            eventPreview.remove();
            showing = false;
            hidden = true;
            fireEvent(new HideEvent());
        }
    }

    @Override
    public void onRemoveTagSelected(RemoveTagSelected event) {
        final Tag tag = event.getTag();
        tagQuery.getValue().remove(tag);
    }

    @Override
    public void onSaveDiskResourceQueryClicked(SaveDiskResourceQueryClickedEvent event) {
        // Re-fire event
        fireEvent(event);
    }

    @Override
    public void onTagAdded(TagAddedEvent event) {
        final Tag tag = event.getTag();
        tagQuery.getValue().add(tag);
    }

    public void show(Element parent, AnchorAlignment anchorAlignment) {
        getElement().makePositionable(true);
        RootPanel.get().add(this);
        onShow();
        getElement().updateZIndex(0);

        showing = true;

        getElement().alignTo(parent, anchorAlignment, 0, 0);

        getElement().show();
        eventPreview.add();

        focus();
        fireEvent(new ShowEvent());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sencha.gxt.widget.core.client.menu.Menu#onHide()
     *
     * When this container becomes hidden, ensure that save filter container is hidden as well.
     *
     * Additionally, this will perform any desired animations when this form is hidden.
     */
    @Override
    protected void onHide() {
        namePrompt.hide();
        super.onHide();
    }

    protected void onPreviewEvent(NativePreviewEvent pe) {
        int type = pe.getTypeInt();
        switch (type) {
            case Event.ONMOUSEDOWN:
            case Event.ONMOUSEWHEEL:
            case Event.ONSCROLL:
            case Event.ONKEYPRESS:
                XElement target = pe.getNativeEvent().getEventTarget().cast();

                // ignore targets within a parent with x-ignore, such as the listview in
                // a combo
                if (target.findParent(".x-ignore", 10) != null) {
                    return;
                }

                if (!getElement().isOrHasChild(target) && !namePrompt.getElement().isOrHasChild(target)) {
                    hide();
                }
        }
    }

    void addTrashAndFilter() {
        VerticalPanel vp = new VerticalPanel();
        vp.add(includeTrashItems);
        vp.add(createFilterLink);
        vp.setSpacing(5);
        con.add(vp, new HtmlData(".trashandfilter"));
    }

    DateInterval createDateInterval(Date from, Date to, String label) {
        DateInterval ret = factory.dateInterval().as();
        ret.setFrom(from);
        ret.setTo(to);
        ret.setLabel(label);
        return ret;
    }

    List<FileSizeUnit> createFileSizeUnits() {
        return SearchModelUtils.createFileSizeUnits();
    }

    void init(DiskResourceQueryFormNamePrompt namePrompt) {
        this.namePrompt = namePrompt;
        this.namePrompt.addSaveDiskResourceQueryClickedEventHandler(this);
        initFileQuery();
        initNegatedFileQuery();
        initMetadataSearchFields();
        initDateRangeCombos();
        initFileSizeNumberFields();
        initFileSizeComboBoxes();
        initSizeFilterFields();
        initOwnerSharedSearchField();
        initExcludeTrashField();
        initTagField();
        initCreateFilter();
        addTrashAndFilter();
        initSearchButton();
    }

    void initCreateFilter() {
        createFilterLink = new IPlantAnchor("Create filter with this search...", -1);
        createFilterLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // Flush to perform local validations
                DiskResourceQueryTemplate flushedFilter = editorDriver.flush();
                if (editorDriver.hasErrors()) {
                    return;
                }
                showNamePrompt(flushedFilter);

            }
        });
    }

    void initDateRangeCombos() {
        List<DateInterval> timeIntervals = Lists.newArrayList();
        Date now = new Date();

        DateInterval interval = createDateInterval(null, null, "---");
        timeIntervals.add(interval);

        final DateWrapper dateWrapper = new DateWrapper(now).clearTime();
        interval = createDateInterval(dateWrapper.addDays(-1).asDate(), now, "1 day");
        timeIntervals.add(interval);

        interval = createDateInterval(dateWrapper.addDays(-3).asDate(), now, "3 days");
        timeIntervals.add(interval);

        interval = createDateInterval(dateWrapper.addDays(-7).asDate(), now, "1 week");
        timeIntervals.add(interval);

        interval = createDateInterval(dateWrapper.addDays(-14).asDate(), now, "2 weeks");
        timeIntervals.add(interval);

        interval = createDateInterval(dateWrapper.addMonths(-1).asDate(), now, "1 month");
        timeIntervals.add(interval);

        interval = createDateInterval(dateWrapper.addMonths(-2).asDate(), now, "2 months");
        timeIntervals.add(interval);

        interval = createDateInterval(dateWrapper.addMonths(-6).asDate(), now, "6 months");
        timeIntervals.add(interval);

        interval = createDateInterval(dateWrapper.addYears(-1).asDate(), now, "1 year");
        timeIntervals.add(interval);

        // Data range combos
        LabelProvider<DateInterval> dateIntervalLabelProvider = new LabelProvider<DateInterval>() {

            @Override
            public String getLabel(DateInterval item) {
                return item.getLabel();
            }
        };
        createdWithinCombo = new SimpleComboBox<>(dateIntervalLabelProvider);
        modifiedWithinCombo = new SimpleComboBox<>(dateIntervalLabelProvider);
        createdWithinCombo.add(timeIntervals);
        modifiedWithinCombo.add(timeIntervals);

        createdWithinCombo.setEmptyText("---");
        modifiedWithinCombo.setEmptyText("---");

        createdWithinCombo.setWidth(cw);
        modifiedWithinCombo.setWidth(cw);

        con.add(new FieldLabel(createdWithinCombo, "Created within"), new HtmlData(".createwithin"));
        con.add(new FieldLabel(modifiedWithinCombo, "Modified within"), new HtmlData(".modifiedwithin"));

    }

    void initExcludeTrashField() {
        includeTrashItems = new CheckBox();
        includeTrashItems.setBoxLabel("Include items in Trash");
    }

    void initFileQuery() {
        fileQuery = new TextField();
        fileQuery.setWidth(cw);
        fileQuery.setEmptyText("Enter values...");
        con.add(new FieldLabel(fileQuery, "File/Folder name has the words"), new HtmlData(".filename"));
    }

    void initFileSizeComboBoxes() {
        // File Size ComboBoxes
        LabelProvider<FileSizeUnit> fileSizeUnitLabelProvider = new LabelProvider<FileSizeUnit>() {

            @Override
            public String getLabel(FileSizeUnit item) {
                return item.getLabel();
            }

        };
        greaterThanComboBox = new SimpleComboBox<>(fileSizeUnitLabelProvider);
        lessThanComboBox = new SimpleComboBox<>(fileSizeUnitLabelProvider);
        greaterThanComboBox.setWidth("64px");
        lessThanComboBox.setWidth("64px");

        greaterThanComboBox.setTriggerAction(TriggerAction.ALL);
        greaterThanComboBox.setForceSelection(true);

        lessThanComboBox.setTriggerAction(TriggerAction.ALL);
        lessThanComboBox.setForceSelection(true);

        List<FileSizeUnit> fileSizeUnitList = createFileSizeUnits();
        greaterThanComboBox.add(fileSizeUnitList);
        lessThanComboBox.add(fileSizeUnitList);

    }

    void initFileSizeNumberFields() {
        // File Size Number fields
        NumberPropertyEditor.DoublePropertyEditor doublePropertyEditor = new NumberPropertyEditor.DoublePropertyEditor();
        fileSizeGreaterThan = new NumberField<>(doublePropertyEditor);
        fileSizeLessThan = new NumberField<>(doublePropertyEditor);

        fileSizeGreaterThan.setAllowNegative(false);
        fileSizeLessThan.setAllowNegative(false);

    }

    void initMetadataSearchFields() {
        metadataAttributeQuery = new TextField();
        metadataAttributeQuery.setEmptyText("Enter values...");
        metadataAttributeQuery.setWidth(cw);
        con.add(new FieldLabel(metadataAttributeQuery, "Metadata attribute has the words"),
                new HtmlData(".metadataattrib"));

        metadataValueQuery = new TextField();
        metadataValueQuery.setEmptyText("Enter values...");
        metadataValueQuery.setWidth(cw);
        con.add(new FieldLabel(metadataValueQuery, "Metadata value has the words"),
                new HtmlData(".metadataval"));

    }

    void initNegatedFileQuery() {
        negatedFileQuery = new TextField();
        negatedFileQuery.setEmptyText("Enter values...");
        negatedFileQuery.setWidth(cw);
        con.add(new FieldLabel(negatedFileQuery, "File/Folder name doesn't have"),
                new HtmlData(".negatefilename"));
    }

    void initOwnerSharedSearchField() {
        ownedBy = new TextField();
        ownedBy.setEmptyText("Enter iPlant user name");
        ownedBy.setWidth(cw);
        con.add(new FieldLabel(ownedBy, "Owned by"), new HtmlData(".owner"));

        sharedWith = new TextField();
        sharedWith.setEmptyText("Enter iPlant user name");
        sharedWith.setWidth(cw);
        con.add(new FieldLabel(sharedWith, "Shared with"), new HtmlData(".shared"));
    }

    void initSearchButton() {
        searchButton = new TextButton("Search");
        searchButton.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                onSearchButtonSelect();

            }
        });
        Label betaLbl = new Label("(beta)");
        betaLbl.setTitle("Search functionality is currently in beta.");
        betaLbl.getElement().getStyle().setColor("#ff0000");
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(searchButton);
        hp.add(betaLbl);
        hp.setSpacing(2);
        con.add(hp, new HtmlData(".search"));
    }

    void initSizeFilterFields() {
        VerticalPanel vp = new VerticalPanel();
        HorizontalPanel hp1 = new HorizontalPanel();
        hp1.add(fileSizeGreaterThan);
        hp1.add(greaterThanComboBox);
        hp1.setSpacing(3);

        greaterField = new FieldLabel(hp1, "File size is bigger than or equal to");
        vp.add(greaterField);

        HorizontalPanel hp2 = new HorizontalPanel();
        hp2.add(fileSizeLessThan);
        hp2.add(lessThanComboBox);
        hp2.setSpacing(3);

        lesserField = new FieldLabel(hp2, "File size is smaller than or equal to");
        vp.add(lesserField);
        con.add(vp, new HtmlData(".filesize"));

    }

    void initTagField() {

        VerticalPanel vp = new VerticalPanel();
        FieldLabel fl = new FieldLabel();
        fl.setText("Tagged with");

        vp.add(fl);
        vp.add(tagsViewPresenter.getView());

        con.add(vp, new HtmlData(".tags"));

    }

    @UiHandler("createFilterLink")
    void onCreateQueryTemplateClicked(ClickEvent event) {
        // Flush to perform local validations
        DiskResourceQueryTemplate flushedFilter = editorDriver.flush();
        if (editorDriver.hasErrors()) {
            return;
        }
        showNamePrompt(flushedFilter);
    }

    void onEscape(NativePreviewEvent pe) {
        if (pe.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
            pe.getNativeEvent().preventDefault();
            pe.getNativeEvent().stopPropagation();
            hide();
        }
    }

    void onSearchButtonSelect() {
        // Flush to perform local validations
        DiskResourceQueryTemplate flushedQueryTemplate = editorDriver.flush();
        if (editorDriver.hasErrors() || isEmptyQuery(flushedQueryTemplate)) {
            return;
        }

        // Fire event and pass flushed query
        fireEvent(new SubmitDiskResourceQueryEvent(flushedQueryTemplate));
        hide();
    }

    void showNamePrompt(DiskResourceQueryTemplate filter) {
        namePrompt.show(filter, getElement(), new AnchorAlignment(Anchor.BOTTOM_LEFT,
                                                                  Anchor.BOTTOM_LEFT,
                                                                  true));
    }

}
