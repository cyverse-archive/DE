/* * Copyright 2012 Oregon State University.
 * 
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
 * 
 */
package org.iplantc.de.pipelineBuilder.client.ui;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class SimpleButton extends HorizontalPanel implements MouseDownHandler, MouseUpHandler, ClickHandler {
	String dependantStyle = "";
	ClickHandler handler;
	Label label;

	public SimpleButton(String title) {
		setHeight("20px");
		label = new Label(title);
		label.setWordWrap(false);
		add(label);
		addStyleName("simple-button");
		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
		addDomHandler(this, ClickEvent.getType());
	}

	public SimpleButton() {
	}

	public SimpleButton addClickHandler(ClickHandler handle) {
		this.handler = handle;
		return this;
	}

	@Override
	public void setStyleDependentName(String styleSuffix, boolean add) {
		if (add) {
			removeStyleName("simple-button" + dependantStyle);
			dependantStyle = styleSuffix;
			addStyleName("simple-button" + dependantStyle);
		} else {
			removeStyleName("simple-button" + dependantStyle);
			dependantStyle = "";
			addStyleName("simple-button" + dependantStyle);
		}
	}

	public void onClick(ClickEvent event) {
	}

	public void onMouseUp(MouseUpEvent event) {
		removeStyleName("simple-button" + dependantStyle + "-down");
		addStyleName("simple-button" + dependantStyle);
		if (handler != null)
			handler.onClick(null);
	}

	public void onMouseDown(MouseDownEvent event) {
		removeStyleName("simple-button" + dependantStyle);
		addStyleName("simple-button" + dependantStyle + "-down");
	}
}
