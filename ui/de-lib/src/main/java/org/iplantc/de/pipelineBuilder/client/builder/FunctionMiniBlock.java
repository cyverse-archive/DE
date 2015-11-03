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
import org.iplantc.de.pipelineBuilder.client.json.PipelineApp;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class FunctionMiniBlock extends Composite implements DragListener, MouseOverHandler, MouseOutHandler {
	private PopupPanel toolTip;
	private Block block;
	
	public FunctionMiniBlock(PipelineApp pipeline){
		this.block=new Block(pipeline, null,false);
		HTML name = new HTML();
		name.setStyleName("function-block");
		name.setHTML(pipeline.getName());
		initWidget(name);
		getElement().setAttribute("draggable", "true");
		DragCreator.addDrag(getElement(), pipeline, this);
		toolTip = new PopupPanel();
		toolTip.add(new HTML("Description:" + pipeline.getPipeline().getDescription()));
		toolTip.setStyleName("tooltip-small");
		addDomHandler(this, MouseOutEvent.getType());
		addDomHandler(this, MouseOverEvent.getType());
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
	public void dragStart(IPCType record) {
		getElement().getStyle().setOpacity(.5);
	}

	@Override
	public boolean dragEnter(IPCType record) {
		return false;
	}

	@Override
	public boolean dragOver(IPCType record) {
		return false;
	}

	@Override
	public void dragLeave(IPCType record) {
	}

	@Override
	public void drop(IPCType record) {
	}

	@Override
	public void dragEnd(IPCType record) {
		getElement().getStyle().setOpacity(1);
	}

	@Override
	public Element getDragImage(IPCType record) {
		return block.getElement();
	}

}
