/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package org.iplantc.de.tags.client.views;

import static org.iplantc.de.tags.client.TagsView.TagListHandlers.InsertionPoint;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.resources.CustomIplantTagResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author cbopp
 */
public class TagViewImpl extends Composite implements TagsView {
    interface Binder extends UiBinder<Widget, TagViewImpl> {
    }

    @UiField DivElement deleteOption;
    @UiField DivElement editOption;
    @UiField DivElement tagDiv;
    @UiField HTMLPanel tagPanel;
    @UiField Label value;

    Logger logger = Logger.getLogger("tags");
    private static TagViewImpl draggedElement;
    private static Binder uiBinder = GWT.create(Binder.class);
    private final List<HandlerRegistration> dndHandlers = new ArrayList<>();
    private final CustomIplantTagResources resources;
    private final IplantTag tag;
    private boolean editable;
    private boolean removable;
    private TagsView.TagListHandlers uiHandlers;

    @Inject
    TagViewImpl(final CustomIplantTagResources resources,
                @Assisted final IplantTag tag) {
        this.resources = resources;
        this.tag = tag;

        initWidget(uiBinder.createAndBindUi(this));

        this.tagDiv.setAttribute("class", resources.style().tag());
        this.value.setStylePrimaryName(resources.style().tagCaption());

        this.value.setText(tag.getValue());

        tagPanel.addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Element e = Element.as(event.getNativeEvent().getEventTarget());
                if (e.getAttribute("name").contains("tagEdit")) {
                    uiHandlers.onEditTag(TagViewImpl.this);
                } else if (e.getAttribute("name").contains("tagDelete")) {
                    uiHandlers.onRemoveTag(TagViewImpl.this);
                } else {
                    uiHandlers.onSelectTag(TagViewImpl.this);
                }

            }
        }, ClickEvent.getType());

        activateDnD();

    }

    public void deactivateDnD() {
        this.tagPanel.removeStyleName(resources.style().tagEditable());

        for (HandlerRegistration dndHandler : this.dndHandlers)
            dndHandler.removeHandler();

        this.dndHandlers.clear();
    }

    @Override
    public IplantTag getTag() {
        return tag;
    }

    @Override
    public void setEditable(boolean editable) {
        if (this.editable != editable) {
            this.editable = editable;

            if (editable) {
                activateEditButton();
                editOption.setTitle(I18N.DISPLAY.edit());
            }
        }

    }

    @Override
    public void setRemovable(boolean removable) {
        if (this.removable != removable) {
            this.removable = removable;

            if (removable) {
                activateDeleteButton();
                deleteOption.setTitle(I18N.DISPLAY.remove());
            }
        }
    }

    @Override
    public void setUiHandlers(TagsView.TagListHandlers tagListHandlers) {
        this.uiHandlers = tagListHandlers;
    }

    private void activateDeleteButton() {

        tagPanel.addDomHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                deleteOption.addClassName(resources.style().tagDelete());

            }
        }, MouseOverEvent.getType());

        tagPanel.addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                deleteOption.removeClassName(resources.style().tagDelete());

            }
        }, MouseOutEvent.getType());

    }

    private void activateDnD() {
        this.tagPanel.addStyleName(resources.style().tagEditable()); // Change cursor on hover

        // DnD
        this.getElement().setDraggable(Element.DRAGGABLE_TRUE);
        this.dndHandlers.add(this.addDomHandler(new DragStartHandler() {
            @Override
            public void onDragStart(DragStartEvent event) {
                event.setData("text", "");
                draggedElement = TagViewImpl.this;
                uiHandlers.onFocus();
            }
        }, DragStartEvent.getType()));

        this.dndHandlers.add(this.addDomHandler(new DragEnterHandler() {
            @Override
            public void onDragEnter(DragEnterEvent event) {
                if (draggedElement.equals(TagViewImpl.this))
                    return;

                // Calculate the mouse's percentage X position relative to the drag over element
                // 0 = left border; 100 = right border
                int percentagePositionX = (event.getNativeEvent().getClientX() - getAbsoluteLeft())
                                              * 100 / getElement().getClientWidth();
                if (percentagePositionX < 50) {
                    tagPanel.addStyleName(resources.style().previewLeft());
                    tagPanel.removeStyleName(resources.style().previewRight());

                } else {
                    tagPanel.addStyleName(resources.style().previewRight());
                    tagPanel.removeStyleName(resources.style().previewLeft());
                }
            }
        }, DragEnterEvent.getType()));

        this.dndHandlers.add(this.addDomHandler(new DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                if (draggedElement.equals(TagViewImpl.this))
                    return;

                // Calculate the mouse's percentage X position relative to the drag over element
                // 0 = left border; 100 = right border
                int percentagePositionX = (event.getNativeEvent().getClientX() - getAbsoluteLeft())
                                              * 100 / getElement().getClientWidth();
                if (percentagePositionX < 50) {
                    tagPanel.addStyleName(resources.style().previewLeft());
                    tagPanel.removeStyleName(resources.style().previewRight());

                } else {
                    tagPanel.addStyleName(resources.style().previewRight());
                    tagPanel.removeStyleName(resources.style().previewLeft());
                }
            }
        }, DragOverEvent.getType()));

        this.dndHandlers.add(this.addDomHandler(new DragLeaveHandler() {
            @Override
            public void onDragLeave(DragLeaveEvent event) {
                if (draggedElement.equals(TagViewImpl.this))
                    return;

                tagPanel.removeStyleName(resources.style().previewLeft());
                tagPanel.removeStyleName(resources.style().previewRight());
            }
        }, DragLeaveEvent.getType()));

        this.dndHandlers.add(this.addDomHandler(new DropHandler() {
                                                    @Override
                                                    public void onDrop(DropEvent event) {
                                                        if (draggedElement.equals(TagViewImpl.this))
                                                            return;

                                                        event.preventDefault();
                                                        uiHandlers.onRelocateTag(draggedElement,
                                                                                 TagViewImpl.this,
                                                                                 tagPanel.getStyleName().contains(resources.style()
                                                                                                                           .previewLeft()) ? InsertionPoint.BEFORE
                                                                                     : InsertionPoint.AFTER);
                                                        tagPanel.removeStyleName(resources.style().previewLeft());
                                                        tagPanel.removeStyleName(resources.style().previewRight());
                                                    }
                                                },
                                                DropEvent.getType()));

        this.dndHandlers.add(this.addDomHandler(new DragEndHandler() {
            @Override
            public void onDragEnd(DragEndEvent event) {
                uiHandlers.onBlur();
            }
        }, DragEndEvent.getType()));
    }

    private void activateEditButton() {

        tagPanel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                editOption.addClassName(resources.style().tagEdit());
            }
        }, MouseOverEvent.getType());

        tagPanel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                editOption.removeClassName(resources.style().tagEdit());
            }
        }, MouseOutEvent.getType());
    }
}
