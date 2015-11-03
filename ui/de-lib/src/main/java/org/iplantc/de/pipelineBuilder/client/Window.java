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
package org.iplantc.de.pipelineBuilder.client;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Window {
	Label title;
	HTML modalMask = new HTML();
	FlowPanel outside;
	HorizontalPanel bar;
	private boolean isDraging = false;
	private int x_start = 0;
	private int y_start = 0;
	private MouseMoveHandler moveHandler;

	public Window(String title, Widget content) {
		this.title = new Label(title);
		this.title.setWordWrap(false);
		bar = new HorizontalPanel();
		bar.setWidth("100%");
		bar.setStyleName("window-bar");
		modalMask.setStyleName("modal-mask");
		VerticalPanel master = new VerticalPanel();
		outside = new FlowPanel() {
			@Override
			protected void onLoad() {
				getElement().getStyle().setLeft((com.google.gwt.user.client.Window.getClientWidth() / 2) - (getOffsetWidth() / 2), Unit.PX);
				getElement().getStyle().setTop((com.google.gwt.user.client.Window.getClientHeight() / 2) - (getOffsetHeight() / 2), Unit.PX);
				super.onLoad();
			}
		};
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				outside.getElement().getStyle().setLeft((com.google.gwt.user.client.Window.getClientWidth() / 2) - (outside.getOffsetWidth() / 2), Unit.PX);
				outside.getElement().getStyle().setTop((com.google.gwt.user.client.Window.getClientHeight() / 2) - (outside.getOffsetHeight() / 2), Unit.PX);
			}
		});
		outside.setStyleName("eta-window");
		HorizontalPanel header = new HorizontalPanel();
		this.title.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				isDraging = true;
				x_start = event.getX();
				y_start = event.getY();
			}
		});

		moveHandler = new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if(!isDraging)return;
				int left = event.getClientX() - x_start;
				int top = event.getClientY() - y_start;
				if (left > 0 && left + outside.getOffsetWidth() < com.google.gwt.user.client.Window.getClientWidth())
					outside.getElement().getStyle().setLeft(left-16,Unit.PX);
				if (top > 0 && top + outside.getOffsetHeight() < com.google.gwt.user.client.Window.getClientHeight())
					outside.getElement().getStyle().setTop(top-16, Unit.PX);
			}
		};
		modalMask.addMouseMoveHandler(moveHandler);
		this.title.addMouseMoveHandler(moveHandler);
		outside.addDomHandler(moveHandler,MouseMoveEvent.getType());
		master.addDomHandler(moveHandler,MouseMoveEvent.getType());
		
		this.title.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				isDraging=false;
			}
		});
		this.title.getElement().getStyle().setCursor(Cursor.MOVE);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		header.setHeight("25px");
		master.add(header);
		header.setStyleName("header");
		header.add(this.title);
		Label close = new Label("x");
		close.setStyleName("close");
		FlowPanel tempContent = new FlowPanel() {
			@Override
			protected void onLoad() {
				super.onLoad();
				if (com.google.gwt.user.client.Window.getClientHeight() < getOffsetHeight()) {
					setHeight(com.google.gwt.user.client.Window.getClientHeight() - 100 + "px");
					getElement().getStyle().setOverflow(Overflow.AUTO);
				} else if (com.google.gwt.user.client.Window.getClientWidth() < getOffsetWidth()) {
					setWidth(com.google.gwt.user.client.Window.getClientWidth() - 300 + "px");
					getElement().getStyle().setOverflow(Overflow.AUTO);
				}
			}
		};
		tempContent.getElement().getStyle().setPadding(10, Unit.PX);
		tempContent.add(content);
		content.setWidth("100%");
		master.add(bar);
		master.add(tempContent);
		header.add(close);
		header.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_RIGHT);
		header.setCellWidth(close, "16px");
		header.setCellHeight(close, "16px");
		header.setWidth("100%");
		master.setWidth("100%");
		master.setCellHeight(header, "25px");
		master.setStyleName("inside");
		outside.add(master);

		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				destroy();
			}
		});

	}

	public void setWidth(String width) {
		outside.setWidth(width);
	}

	public void destroy() {
		RootPanel.get().remove(outside);
		RootPanel.get().remove(modalMask);
	}

	public void showWindow() {
		RootPanel.get().add(modalMask);
		RootPanel.get().add(outside);
	}

	public void addBar(Widget wid) {
		bar.add(wid);
	}
}
