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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class SimpleLabel extends FlowPanel implements MouseOverHandler, MouseOutHandler {
	protected PopupPanel toolTip;

	public SimpleLabel(String title) {
		if (title.length() > 70) {
			String[] tempName = title.split(" ");
			String currentName = "";
			int i = 0;
			for (String on : tempName) {
				if ((currentName + on).length() >= 70) {
					Label title2 = new Label(currentName);
					title2.setWordWrap(false);
					add(title2);
					currentName = "";
				}
				currentName += " " + on;
				if (i == tempName.length - 1) {
					Label title2 = new Label(currentName);
					title2.setWordWrap(false);
					add(title2);
				}
				i++;
			}
		} else {
			Label title2 = new Label(title);
			title2.setWordWrap(false);
			add(title2);
		}
		addStyleName("simple-label");
	}

	public void setText(String text) {
		clear();
		add(new Label(text));
	}

	public SimpleLabel setColor(String color) {
		getElement().getStyle().setColor(color);
		return this;
	}

	public void setToolTip(String tip) {
		toolTip = new PopupPanel();
		toolTip.add(new HTML(tip));
		toolTip.setStyleName("tooltip");
		addDomHandler(this, MouseOverEvent.getType());
		addDomHandler(this, MouseOutEvent.getType());
	}

	Timer t = new Timer() {
		@Override
		public void run() {
			toolTip.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop() + getOffsetHeight() + 4);
			toolTip.show();
			toolTip.setAutoHideEnabled(true);
		}
	};

	public void onMouseOver(MouseOverEvent event) {
		if (toolTip != null) {
			t.cancel();
			t.schedule(200);
		}
	}

	public void onMouseOut(MouseOutEvent event) {
		if (toolTip != null) {
			t.cancel();
			toolTip.hide();
		}
	}

	public SimpleLabel setFontSize(int size) {
		getElement().getStyle().setFontSize(size, Unit.PX);
		return this;
	}
}
