/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.virilis_software.gwt.taglist.client.comp.tag;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.virilis_software.gwt.taglist.client.TagList.InsertionPoint;
import com.virilis_software.gwt.taglist.client.comp.TagListHandlers;
import com.virilis_software.gwt.taglist.client.resource.Resources;
import com.virilis_software.gwt.taglist.client.tag.Tag;

/**
 * 
 * @author cbopp
 *
 */
public class TagView extends Composite {
    interface Binder extends UiBinder<Widget, TagView> {}
    private static Binder uiBinder = GWT.create( Binder.class );
    
    @UiField HTMLPanel tagPanel;
    @UiField DivElement tag;
    @UiField Label caption;
    @UiField HTMLPanel deletePanel;
    
    private TagListHandlers uiHandlers;
    
    private HandlerRegistration handlerRegistration;
    private List<HandlerRegistration> dndHandlers = new ArrayList<HandlerRegistration>();

    private boolean editable;
    private static TagView draggedElement;
    

    public void setUiHandlers( TagListHandlers tagListHandlers ) {
        this.uiHandlers = tagListHandlers;
    }

    public TagView( Resources resources, Tag<?> tag ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        
        this.tag.setAttribute( "class", resources.style().tag() );
        this.caption.setStylePrimaryName( resources.style().tagCaption() );
        this.deletePanel.setStylePrimaryName( resources.style().tagDelete() );

        this.caption.setText( tag.getCaption() );
        
        this.deletePanel.setVisible( false );
    }

    public void setEditable( boolean editable ) {
        if( this.editable != editable ) {
            this.editable = editable;

            if( editable ) {
        	    this.activateDeleteButton();
        	    this.activateDnD();
        	
        	} else { 
        	    this.deactivateDeleteButton();
        	    this.deactivateDnD();
        	}
        }
    }
    
    private void activateDeleteButton() {
        this.caption.addStyleName( Resources.INSTANCE.style().tagCaptionEditable() ); //Reduce width to include delete button
        
        this.handlerRegistration = this.deletePanel.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                uiHandlers.onRemoveTag( TagView.this );
            }
            
        }, ClickEvent.getType() );
        
        this.deletePanel.setVisible( true );
    }

    private void deactivateDeleteButton() {
        this.caption.removeStyleName( Resources.INSTANCE.style().tagCaptionEditable() );
        
        if( this.handlerRegistration != null ) {
            this.handlerRegistration.removeHandler();
            this.handlerRegistration = null;
        }
        this.deletePanel.setVisible( false );
    }

    private void activateDnD() {
        this.tagPanel.addStyleName( Resources.INSTANCE.style().tagEditable() ); //Change cursor on hover
        
        //DnD
        this.getElement().setDraggable( Element.DRAGGABLE_TRUE );
        this.dndHandlers.add( this.addDomHandler( new DragStartHandler() {
            @Override
            public void onDragStart( DragStartEvent event ) {
                event.setData( "text", "" );
                draggedElement = TagView.this;
                uiHandlers.onFocus();
            }
          }, DragStartEvent.getType() ) );
        
        
        this.dndHandlers.add( this.addDomHandler( new DragEnterHandler() {
            @Override
            public void onDragEnter( DragEnterEvent event ) {
                if( draggedElement.equals( TagView.this ) )
                    return;
                
                //Calculate the mouse's percentage X position relative to the drag over element
                //0 = left border; 100 = right border
                int percentagePositionX = ( event.getNativeEvent().getClientX() - getAbsoluteLeft() ) * 100 / getElement().getClientWidth(); 
                if( percentagePositionX < 50 ) {
                    tagPanel.addStyleName( Resources.INSTANCE.style().previewLeft() );
                    tagPanel.removeStyleName( Resources.INSTANCE.style().previewRight() );
                    
                } else {
                    tagPanel.addStyleName( Resources.INSTANCE.style().previewRight() );
                    tagPanel.removeStyleName( Resources.INSTANCE.style().previewLeft() );
                }
            }
        }, DragEnterEvent.getType() ) );
        
        this.dndHandlers.add( this.addDomHandler( new DragOverHandler() {
            @Override
            public void onDragOver( DragOverEvent event ) {
                if( draggedElement.equals( TagView.this ) )
                    return;
                
                //Calculate the mouse's percentage X position relative to the drag over element
                //0 = left border; 100 = right border
                int percentagePositionX = ( event.getNativeEvent().getClientX() - getAbsoluteLeft() ) * 100 / getElement().getClientWidth(); 
                if( percentagePositionX < 50 ) {
                    tagPanel.addStyleName( Resources.INSTANCE.style().previewLeft() );
                    tagPanel.removeStyleName( Resources.INSTANCE.style().previewRight() );
                    
                } else {
                    tagPanel.addStyleName( Resources.INSTANCE.style().previewRight() );
                    tagPanel.removeStyleName( Resources.INSTANCE.style().previewLeft() );
                }
            }
        }, DragOverEvent.getType() ) );
        
        this.dndHandlers.add( this.addDomHandler( new DragLeaveHandler() {
            @Override
            public void onDragLeave( DragLeaveEvent event ) {
                if( draggedElement.equals( TagView.this ) )
                    return;
                
                tagPanel.removeStyleName( Resources.INSTANCE.style().previewLeft() );
                tagPanel.removeStyleName( Resources.INSTANCE.style().previewRight() );
            }
        }, DragLeaveEvent.getType() ) );
        
        this.dndHandlers.add( this.addDomHandler( new DropHandler() {
            @Override
            public void onDrop( DropEvent event ) {
                if( draggedElement.equals( TagView.this ) )
                    return;
                
                event.preventDefault();
                uiHandlers.onRelocateTag( draggedElement, TagView.this,
                        tagPanel.getStyleName().contains( Resources.INSTANCE.style().previewLeft() ) ?
                                InsertionPoint.BEFORE : InsertionPoint.AFTER );
                tagPanel.removeStyleName( Resources.INSTANCE.style().previewLeft() );
                tagPanel.removeStyleName( Resources.INSTANCE.style().previewRight() );
            }
        }, DropEvent.getType() ) );
        
        this.dndHandlers.add( this.addDomHandler( new DragEndHandler() {
            @Override
            public void onDragEnd( DragEndEvent event ) {
                uiHandlers.onBlur();
            }
        }, DragEndEvent.getType() ) );
    }
    
    private void deactivateDnD() {
        this.tagPanel.removeStyleName( Resources.INSTANCE.style().tagEditable() );
        
        for( HandlerRegistration dndHandler : this.dndHandlers )
            dndHandler.removeHandler();
        
        this.dndHandlers.clear();
    }
}
