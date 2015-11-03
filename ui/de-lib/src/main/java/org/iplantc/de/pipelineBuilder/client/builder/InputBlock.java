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
import org.iplantc.de.pipelineBuilder.client.json.Input;
import org.iplantc.de.pipelineBuilder.client.json.Output;
import org.iplantc.de.pipelineBuilder.client.json.PipeComponent;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class InputBlock extends Composite implements MouseOverHandler, MouseOutHandler, DragListener {

	private final Input input;
	private final PopupPanel toolTip;
	FlowPanel top = new FlowPanel();
	HTML name = new HTML();
	PipeComponent parent;

	public InputBlock(Input input, PipeComponent app) {
		this.input = input;
		parent = app;
		FlowPanel pane = new FlowPanel();
		input.setParent(parent);
		initWidget(pane);
		FlowPanel bottom = new FlowPanel();
		setStyleName("input-block");
		top.setStyleName("top");
		bottom.setStyleName("bottom");
		pane.add(top);
		pane.add(bottom);

		if (!input.isRequired()) {
			name.getElement().getStyle().setOpacity(.6);
		}
		
		name.setStyleName("name");
		bottom.add(name);
		name.setHTML(input.getName());
		toolTip = new PopupPanel();
		toolTip.add(new HTML("Type: " + input.getType() + "<br>Description:" + input.getDescription()));
		toolTip.setStyleName("tooltip-small");
		addDomHandler(this, MouseOutEvent.getType());
		addDomHandler(this, MouseOverEvent.getType());
		top.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		DragCreator.addDrag(top.getElement(), input, this);
	}

	@Override
    public void onMouseOut(MouseOutEvent event) {
		toolTip.hide();
	}

	@Override
    public void onMouseOver(MouseOverEvent event) {
		toolTip.setPopupPosition(getAbsoluteLeft(), getAbsoluteTop() + getOffsetHeight() + 4);
		toolTip.show();
	}

	@Override
    public boolean dragEnter(IPCType record2) {
		IPCType record = DragCreator.getDragSource();
		if (record instanceof Output) {
			Output temp = (Output) record;
			if (temp.getParent().getPosition() < input.getParent().getPosition())
				top.getElement().getStyle().setBackgroundColor("#A5CAF4");
			else {
				return false;
			}
		}
		return true;
	}

	@Override
    public boolean dragOver(IPCType record2) {
		IPCType record = DragCreator.getDragSource();
		if (record instanceof Output) {
			Output temp = (Output) record;
			if (temp.getParent().getPosition() < input.getParent().getPosition()) {
				top.getElement().getStyle().setBackgroundColor("#A5CAF4");
				return true;
			} else {
				return false;

			}
		}
		return true;
	}

	@Override
    public void dragLeave(IPCType record2) {
		top.getElement().getStyle().setBackgroundColor("");
	}

	public void setInputValue(Output output) {
		if (output.getParent().getPosition() >= input.getParent().getPosition()) {
			return;
		}

		top.clear();
		input.setMapped(output);
		name.getElement().getStyle().setBackgroundColor("#299C47");
		HTML name = new HTML();
		name.setHTML(output.getName());
		name.setStyleName("output-block");
		top.add(name);
	}

	@Override
    public void drop(IPCType record2) {
		IPCType record = DragCreator.getDragSource();
		if (record instanceof Output) {
			final Output output = (Output) record;
			top.getElement().getStyle().setBackgroundColor("");
			setInputValue(output);
		}
	}

	@Override
    public void dragEnd(IPCType record) {
        if (record.getDragAction() == DragCreator.DELETE) {
            inValidate();
        }
	}

	@Override
    public Element getDragImage(IPCType record) {
		return top.getElement();
	}

	public Input getInput() {
		return input;
	}

	public void inValidate() {
		top.clear();
		input.setMapped(null);
		name.getElement().getStyle().setBackgroundColor("");

	}

	@Override
	public void dragStart(IPCType record) {
	}
}
