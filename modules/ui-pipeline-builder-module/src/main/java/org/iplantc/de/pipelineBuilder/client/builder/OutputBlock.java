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
package org.iplantc.de.pipelineBuilder.client.builder;


import org.iplantc.de.pipelineBuilder.client.dnd.DragCreator;
import org.iplantc.de.pipelineBuilder.client.dnd.DragListener;
import org.iplantc.de.pipelineBuilder.client.json.IPCType;
import org.iplantc.de.pipelineBuilder.client.json.Output;
import org.iplantc.de.pipelineBuilder.client.json.PipeComponent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class OutputBlock extends Composite implements DragListener, MouseOverHandler, MouseOutHandler {

	private PopupPanel toolTip;
	private Output output;
	public OutputBlock(Output output,PipeComponent parent) {
		HTML name = new HTML();
		output.setParent(parent);
		name.setStyleName("output-block");
		name.setHTML(output.getName());
		initWidget(name);
		getElement().setAttribute("draggable", "true");
		DragCreator.addDrag(getElement(), output, this);
		toolTip = new PopupPanel();
		toolTip.add(new HTML("Type: " + output.getType() + "<br>Description:" + output.getDescription()));
		toolTip.setStyleName("tooltip-small");
		addDomHandler(this, MouseOutEvent.getType());
		addDomHandler(this, MouseOverEvent.getType());
		this.output=output;
	}

	public void dragStart(IPCType record) {
		getElement().getStyle().setOpacity(.9);
	}
	public Output getOutput(){
		return output;
	}
	

	public boolean dragEnter(IPCType record) {
		return false;
	}

	public boolean dragOver(IPCType record) {
		return false;
	}

	public void dragLeave(IPCType record) {
	}

	public void drop(IPCType record) {
	}

	public void dragEnd(IPCType record) {
		getElement().getStyle().setOpacity(1);
	}

	public Element getDragImage(IPCType record) {
		return getElement();
	}

	public void onMouseOut(MouseOutEvent event) {
		toolTip.hide();
	}

	public void onMouseOver(MouseOverEvent event) {
		toolTip.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop() + getOffsetHeight() + 4);
		toolTip.show();
	}
}
