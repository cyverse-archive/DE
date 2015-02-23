package org.iplantc.de.diskResource.client.views.search.cells;

import org.iplantc.de.client.models.search.DateInterval;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.FileSizeRange.FileSizeUnit;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.views.TagSearchFieldImpl;

import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Element;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeSimpleBeanEditorDriverProvider;

import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Date;
import java.util.List;

/**
 * This test verifies the functionality of the {@link org.iplantc.de.diskResource.client.views.search.cells.DiskResourceQueryForm} class when there are editor
 * errors.
 * 
 * @author jstroot
 * 
 */
@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceQueryFormTest_WithEditorErrors {

    @Mock DiskResourceQueryFormNamePrompt namePrompt;
    @Mock DiskResourceQueryTemplate mockedTemplate;
    @Mock TagsView.Presenter tagsViewPresenterMock;
    @Mock TagsView tagsViewMock;

    private DiskResourceQueryForm form;

    @Before public void setUp() {
        when(tagsViewPresenterMock.getView()).thenReturn(tagsViewMock);
        GwtMockito.useProviderForType(SimpleBeanEditorDriver.class, new FakeSimpleBeanEditorDriverProvider(true));
        form = new DiskResourceQueryForm(tagsViewPresenterMock, mockedTemplate) {

            @Override
            DateInterval createDateInterval(Date from, Date to, String label) {
                DateInterval ret = mock(DateInterval.class);
                ret.setFrom(from);
                ret.setTo(to);
                ret.setLabel(label);
                return ret;
            }

            @Override
            List<FileSizeUnit> createFileSizeUnits() {
                return Lists.newArrayList();
            }

            @Override
            void addTrashAndFilter() {
                VerticalPanel vp = mock(VerticalPanel.class);
                vp.add(includeTrashItems);
                vp.add(createFilterLink);
                vp.setSpacing(5);
            }

            @Override
            void initSearchButton() {
                searchButton = mock(TextButton.class);
                searchButton.addSelectHandler(new SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                        onSearchButtonSelect();

                    }
                });
                Label betaLbl = mock(Label.class);
                HorizontalPanel hp = new HorizontalPanel();
                hp.add(searchButton);
                hp.add(betaLbl);
                hp.setSpacing(2);
            }

            @Override
            void initSizeFilterFields() {
                HorizontalPanel hp1 = mock(HorizontalPanel.class);
                hp1.add(fileSizeGreaterThan);
                hp1.add(greaterThanComboBox);
                hp1.setSpacing(3);

                HorizontalPanel hp2 = new HorizontalPanel();
                hp2.add(fileSizeLessThan);
                hp2.add(lessThanComboBox);
                hp2.setSpacing(3);

            }

            @Override
            void initCreateFilter() {
                createFilterLink = mock(IPlantAnchor.class);
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

            @Override
            void initExcludeTrashField() {
                includeTrashItems = mock(CheckBox.class);
                includeTrashItems.setBoxLabel("Include items in Trash");
            }

            @Override
            void initFileQuery() {
                fileQuery = mock(TextField.class);
                fileQuery.setWidth(cw);
                fileQuery.setEmptyText("Enter values...");
            }

            @Override
            void initNegatedFileQuery() {
                negatedFileQuery = mock(TextField.class);
                negatedFileQuery.setEmptyText("Enter values...");
                negatedFileQuery.setWidth(cw);
            }

            @Override
            void initMetadataSearchFields() {
                metadataAttributeQuery = mock(TextField.class);
                metadataAttributeQuery.setEmptyText("Enter values...");
                metadataAttributeQuery.setWidth(cw);

                metadataValueQuery = mock(TextField.class);
                metadataValueQuery.setEmptyText("Enter values...");
                metadataValueQuery.setWidth(cw);

            }

            @Override
            void initOwnerSharedSearchField() {
                ownedBy = mock(TextField.class);
                ownedBy.setEmptyText("Enter iPlant user name");
                ownedBy.setWidth(cw);

                sharedWith = mock(TextField.class);
                sharedWith.setEmptyText("Enter iPlant user name");
                sharedWith.setWidth(cw);
            }

            @Override
            void initDateRangeCombos() {
                List<DateInterval> timeIntervals = Lists.newArrayList();

                DateInterval interval = createDateInterval(null, null, "---");
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

            }

            @Override
            void initTagField() {
                final TagSearchFieldImpl tagSearchField = mock(TagSearchFieldImpl.class);

                tagSearchField.addValueChangeHandler(new ValueChangeHandler<Tag>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<Tag> event) {
                        tagSearchField.clear();
                        tagSearchField.asWidget().getElement().focus();
                    }
                });
            }

        };
        form.namePrompt = namePrompt;
    }

    /**
     * Verify the following when {@link DiskResourceQueryForm#createFilterLink} is selected;<br/>
     */
    @Test public void testOnCreateQueryTemplateClicked_withErrors() {
        form.onCreateQueryTemplateClicked(mock(ClickEvent.class));

        // Verify that name prompt is not shown
        verify(namePrompt, never()).show(any(DiskResourceQueryTemplate.class), any(Element.class), any(AnchorAlignment.class));
    }

    /**
     * Verify the following when {@link DiskResourceQueryForm#searchButton} is clicked;<br/>
     */
    @Test public void testOnSearchBtnSelected_withErrors() {
        DiskResourceQueryForm spy = spy(form);
        spy.onSearchButtonSelect();

        // Verify no events are fired
        verify(spy, never()).fireEvent(any(GwtEvent.class));

        // Verify that the form is not hidden
        verify(spy, never()).hide();
    }
}
