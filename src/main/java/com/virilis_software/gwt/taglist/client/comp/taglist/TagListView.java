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
package com.virilis_software.gwt.taglist.client.comp.taglist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.virilis_software.gwt.taglist.client.comp.TagListHandlers;
import com.virilis_software.gwt.taglist.client.resource.Resources;
import com.virilis_software.gwt.taglist.client.tag.StringTag;

/**
 * 
 * @author cbopp
 *
 */
public class TagListView extends Composite {
    interface Binder extends UiBinder<Widget, TagListView> {}
    private static Binder uiBinder = GWT.create( Binder.class );
    
    @UiField HTMLPanel tagListPanel;
    @UiField TagsPanel tagsPanel;
    @UiField LIElement inputListItem;
    @UiField TextBox inputTextBox;
    @UiField SpanElement textHandler;
    
    private TagListHandlers uiHandlers;
    

    public void setUiHandlers( TagListHandlers tagListHandlers ) {
        this.uiHandlers = tagListHandlers;
    }
    
    /**
     * @return the tagsPanel
     */
    public TagsPanel getTagsPanel() {
        return this.tagsPanel;
    }

    public TagListView( Resources resources ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        
        this.tagListPanel.setStylePrimaryName( resources.style().tagList() );
        this.inputListItem.setAttribute( "class", resources.style().inputListItem() );
        this.inputTextBox.setStylePrimaryName( resources.style().inputField() );
        
        this.initTagList();
        this.initInputText();
    }

    private void initTagList() {
        // TagList focus
        this.tagListPanel.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                inputTextBox.setFocus( true );
            }
        }, ClickEvent.getType() );
    }

    private void initInputText() {
        this.inputTextBox.setVisible( false );
        this.inputTextBox.setEnabled( false );
        
        this.inputTextBox.addDomHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( KeyPressEvent event ) {
                if( isAlfaNumericKey( event.getCharCode() ) ) {
                    handleInputText();
                }
            }
        }, KeyPressEvent.getType() );
        
        this.inputTextBox.addDomHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER
                        && !inputTextBox.getText().trim().isEmpty() ) {
                    uiHandlers.onAddTag( new StringTag( inputTextBox.getText() ) );
                    inputTextBox.setText( null );
                    handleInputText();
                    
                } else if( event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE
                        || event.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
                    handleInputText();
                }
                
            }
        }, KeyUpEvent.getType() );
        
        this.inputTextBox.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( FocusEvent event ) {
                uiHandlers.onFocus();
            }
        } );
        
        this.inputTextBox.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( BlurEvent event ) {
                uiHandlers.onBlur();
            }
        } );
    }

    private void handleInputText() {
        //Write input text into a hidden span element to get its actual size 
        this.textHandler.setInnerText( this.inputTextBox.getText() );
        this.inputListItem.getStyle().setWidth( this.textHandler.getOffsetWidth() + 30, Unit.PX );
    }
    
    private static boolean isAlfaNumericKey( int key ) {
        return !isSystemKey( key );
    }

    private static boolean isSystemKey( int keyCode ) {
        return keyCode == KeyCodes.KEY_ALT
                || keyCode == KeyCodes.KEY_BACKSPACE
                || keyCode == KeyCodes.KEY_CTRL
                || keyCode == KeyCodes.KEY_DELETE
                || keyCode == KeyCodes.KEY_DOWN
                || keyCode == KeyCodes.KEY_END
                || keyCode == KeyCodes.KEY_ENTER
                || keyCode == KeyCodes.KEY_ESCAPE
                || keyCode == KeyCodes.KEY_HOME
                || keyCode == KeyCodes.KEY_LEFT
                || keyCode == KeyCodes.KEY_PAGEDOWN
                || keyCode == KeyCodes.KEY_PAGEUP
                || keyCode == KeyCodes.KEY_RIGHT
                || keyCode == KeyCodes.KEY_SHIFT
                || keyCode == KeyCodes.KEY_TAB
                || keyCode == KeyCodes.KEY_UP;
    }
    
    public void setEditable( boolean editable ) {
        this.inputTextBox.setVisible( editable );
        this.inputTextBox.setEnabled( editable );
        
        this.tagListPanel.getElement().setPropertyBoolean( "disabled", !editable );
        
        if( editable )
            this.tagListPanel.addStyleName( Resources.INSTANCE.style().tagListEditable() );
        else
            this.tagListPanel.removeStyleName( Resources.INSTANCE.style().tagListEditable() );
    }
}
