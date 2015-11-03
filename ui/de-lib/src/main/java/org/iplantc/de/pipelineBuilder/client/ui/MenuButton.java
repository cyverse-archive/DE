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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Vector;

public class MenuButton extends VerticalPanel implements MouseDownHandler, MouseUpHandler, ClickHandler, MouseOverHandler, MouseOutHandler {
	private ClickHandler handler;
	protected VerticalPanel menu;
	protected HorizontalPanel temp;
	private Label title;

	public MenuButton(String name) {
		temp = new HorizontalPanel();
		temp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		temp.addStyleName("eta-button");
		title = new Label(name);
		title.setWordWrap(false);
		Label arrow = new Label();
		arrow.setStyleName("gbma");
		temp.add(title);
		temp.add(arrow);
		add(temp);
		setWidth("1px");
		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
		addDomHandler(this, ClickEvent.getType());
		addDomHandler(this, MouseOutEvent.getType());
		addDomHandler(this, MouseOverEvent.getType());
		menu = new VerticalPanel();
		menu.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		addStyleName("eta-button-menu");
		add(menu);
		menu.setStyleName("sub-menu");
		menu.setVisible(false);

	}

	public Vector<Widget> getMenuItems() {
		Vector<Widget> ret = new Vector<Widget>();
		for (int i = 0; i < menu.getWidgetCount(); i++)
			ret.add(menu.getWidget(i));
		return ret;
	}

	public void setRight(int pixles) {
		menu.getElement().getStyle().setRight(pixles, Unit.PX);
	}

	public MenuButton() {
		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
		addDomHandler(this, ClickEvent.getType());
		addDomHandler(this, MouseOutEvent.getType());
		addDomHandler(this, MouseOverEvent.getType());
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void clearMenu() {
		menu.clear();
		menu.setVisible(false);
	}

	protected boolean lock = false;

	public void forceVisible() {
		lock = true;
	}

	public void forceHide() {
		menu.setVisible(false);
		lock = false;
	}

	public void addButton(int before, Button butt) {
		butt.setStyleDependentName("-menu2", true);
		butt.setWidth("100%");
		menu.insert(butt, before);
		butt.setParent(this);
	}

	@Override
	public void clear() {
		menu.clear();
	}

	public void addButton(Button butt) {
		butt.setStyleDependentName("-menu2", true);
		butt.setWidth("100%");
		menu.add(butt);
		butt.setParent(this);
	}

	public void onMouseUp(MouseUpEvent event) {
		// removeStyleName("button-down");
		// addStyleName("eta-button");
	}

	public void onMouseDown(MouseDownEvent event) {
		// addStyleName("button-down");
		// removeStyleName("eta-button");
	}

	public void onClick(ClickEvent event) {
		// removeStyleName("button-down");
		// addStyleName("eta-button");
		event.stopPropagation();
		if (!lock) {
			menu.setVisible(false);
			temp.addStyleName("eta-button");
			temp.removeStyleName("button-hover2");
		}
	}

	Timer t = new Timer() {

		@Override
		public void run() {
			menu.setVisible(false);
			temp.addStyleName("eta-button");
			temp.removeStyleName("button-hover2");
		}
	};

	public void onMouseOut(MouseOutEvent event) {
		if (!lock) {
			t.schedule(2);
		}
	}

	public void setClickHandler(ClickHandler handler) {
		title.addDomHandler(this.handler, ClickEvent.getType());
		this.handler = handler;
	}

	public void onMouseOver(MouseOverEvent event) {
		t.cancel();
		menu.setVisible(true);
		lock = false;
		temp.addStyleName("button-hover2");
		temp.removeStyleName("eta-button");
	}

}
