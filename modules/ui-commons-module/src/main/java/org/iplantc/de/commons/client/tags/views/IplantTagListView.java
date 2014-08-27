package org.iplantc.de.commons.client.tags.views;

import org.iplantc.de.client.models.tags.IplantTagAutoBeanFactory;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.commons.client.gin.CommonsInjector;
import org.iplantc.de.commons.client.tags.presenter.TagListHandlers;
import org.iplantc.de.commons.client.tags.resources.CustomIplantTagResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import java.util.logging.Logger;

public class IplantTagListView extends Composite implements IsWidget {

    private static IplantTagListViewUiBinder uiBinder = GWT.create(IplantTagListViewUiBinder.class);

    interface IplantTagListViewUiBinder extends UiBinder<Widget, IplantTagListView> {
    }

    interface TagTemplate extends XTemplates {
        @XTemplate("<div style='font-size:.75em;color:#DB6619;font-weight:bold;'>{tag.value}</div>")
        SafeHtml render(IplantTag tag);
    }

    private TagListHandlers uiHandlers;

    @UiField
    VerticalLayoutContainer tagListPanel;

    @UiField(provided = true)
    TagSearchField tagSearchField;

    @UiField
    TagsPanel tagsPanel;

    @UiField
    FieldLabel taglbl;

    Logger logger = Logger.getLogger("list view logger");

    IplantTagAutoBeanFactory factory = GWT.create(IplantTagAutoBeanFactory.class);

    @Inject
    public IplantTagListView() {
        tagSearchField = CommonsInjector.INSTANCE.getTagSearchField();
        initWidget(uiBinder.createAndBindUi(this));
        taglbl.setHTML("<span style='font-size:10px; font-weight:bold;'>" + I18N.DISPLAY.tags()
                + "</span>");
        tagsPanel.setStyleName(CustomIplantTagResources.INSTANCE.style().tagPanel());
        tagSearchField.addSelectionHandler(new SelectionHandler<IplantTag>() {

            @Override
            public void onSelection(SelectionEvent<IplantTag> event) {
                tagSearchField.setValue(event.getSelectedItem());

            }
        });

        tagSearchField.addValueChangeHandler(new ValueChangeHandler<IplantTag>() {

            @Override
            public void onValueChange(ValueChangeEvent<IplantTag> event) {
                processCurrentValue(event.getValue());
            }
        });

        tagSearchField.setCreateTagCommand(new Command() {
            @Override
            public void execute() {
                createTag();
            }
        });
    }

    public void setUiHandlers(TagListHandlers tagListHandlers) {
        this.uiHandlers = tagListHandlers;
    }

    /**
     * @return the tagsPanel
     */
    public TagsPanel getTagsPanel() {
        return tagsPanel;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    public void setEditable(boolean editable) {
        this.tagListPanel.getElement().setPropertyBoolean("disabled", !editable);

        if (editable) {
            this.tagListPanel.addStyleName(CustomIplantTagResources.INSTANCE.style().tagListEditable());
        } else {
            this.tagListPanel.removeStyleName(CustomIplantTagResources.INSTANCE.style()
                                                                               .tagListEditable());
        }

    }
    
    private void createTag() {
        String text = tagSearchField.getText();
        if (!Strings.isNullOrEmpty(text)) {
            AutoBean<IplantTag> tagBean = AutoBeanCodex.decode(factory, IplantTag.class, "{}");
            IplantTag tag = tagBean.as();
            tag.setValue(text);
            uiHandlers.onCreateTag(tag);
            tagSearchField.clear();
        }
    }

    private void processCurrentValue(IplantTag tag) {
        uiHandlers.onAddTag(tag);
        tagSearchField.clear();
    }

}
