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

import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author cbopp
 *
 */
public class TagsPanel extends HTMLPanel {
    public TagsPanel( String html ) {
        super( UListElement.TAG, null );
        getElement().setInnerHTML( html );
    }

    @Override
    public void add( Widget widget ) {
        Element liWrapper = DOM.createElement( "li" );
        //First widget is the input TextBox, add new widgets between the last tag and the input TextBox
        DOM.insertChild( this.getElement(), liWrapper, this.getWidgetCount() - 1 );
        add( widget, liWrapper );
    }
    
    @Override
    public boolean remove( Widget widget ) {
        // Get the LI to be removed, before calling super.remove(), because
        // super.remove() will detach the child widget's element from its parent.
        Element li = DOM.getParent( widget.getElement() );
        boolean removed = super.remove( widget );
        if( removed )
            this.getElement().removeChild( li );

        return removed;
    }
}
