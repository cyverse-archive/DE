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
import org.iplantc.de.pipelineBuilder.client.json.App;
import org.iplantc.de.pipelineBuilder.client.json.IPCType;
import org.iplantc.de.pipelineBuilder.client.json.Input;
import org.iplantc.de.pipelineBuilder.client.json.Output;
import org.iplantc.de.pipelineBuilder.client.json.PipeApp;
import org.iplantc.de.pipelineBuilder.client.json.PipeComponent;
import org.iplantc.de.pipelineBuilder.client.json.PipelineApp;
import org.iplantc.de.pipelineBuilder.client.json.UserApp;
import org.iplantc.de.pipelineBuilder.client.ui.Button;
import org.iplantc.de.pipelineBuilder.client.ui.Seprator;
import org.iplantc.de.pipelineBuilder.client.ui.SimpleLabel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import java.util.Vector;

public class Block extends Composite implements DragListener {
	private PipeComponent app;
	private HorizontalPanel inputPanel;
	private HorizontalPanel outputPanel;
	HorizontalPanel startBlock = new HorizontalPanel();
	private BlockChangeListener listener;
	FlowPanel temp = new FlowPanel();
	private Workspace workspace;

	public Block(PipeComponent app, BlockChangeListener listener,boolean expanded) {
		this.listener = listener;
		startBlock.setStyleName("block");
		if (app instanceof PipelineApp)
			startBlock.addStyleName("function");
		inputPanel = new HorizontalPanel();
		outputPanel = new HorizontalPanel();
		outputPanel.setVisible(false);
		boolean hasMapping = false;
		
		for(Input input : app.getInputs()){
			if(input.getMapped()!=null)
				hasMapping = true;
		}
        if (expanded && !hasMapping) {
            inputPanel.setVisible(false);
        }
		inputPanel.setStyleName("input-panel");
		outputPanel.setStyleName("output-panel");
		this.app = app;
		HTML expandInputs = new HTML();
		expandInputs.setStyleName("left-expand");
		expandInputs.setHTML("<div class='arrow2'></div>");
		startBlock.add(expandInputs);
		expandInputs.addClickHandler(new ClickHandler() {
			@Override
            public void onClick(ClickEvent event) {
				inputPanel.setVisible(!inputPanel.isVisible());
			}
		});

		startBlock.add(expandInputs);
		startBlock.add(inputPanel);
		// HTML img = new HTML("<img src='" +
		// Resources.INSTANCE.wrench().getSafeUri().asString() + "'/>");
		// startBlock.add(img);
		// img.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent event) {
		// edit();
		// }
		// });
		// img.setStyleName("center");
		// img.setHeight("30px");
		String name = app.getName();
		if (name.length() > 23) {
			name = name.substring(0, 20) + "...";
		}
		SimpleLabel center = new SimpleLabel(name);
		if (app.getName().length() > 23)
			center.setToolTip(app.getName());
		center.setHeight("30px");
		startBlock.add(center);
		center.setStyleName("center");

		startBlock.add(outputPanel);

		HTML expandOutputs = new HTML();
		expandOutputs.setStyleName("right-expand");
		expandOutputs.setHTML("<div class='arrow2'></div>");
		startBlock.add(expandOutputs);
		expandOutputs.addClickHandler(new ClickHandler() {
			@Override
            public void onClick(ClickEvent event) {
				outputPanel.setVisible(!outputPanel.isVisible());
			}
		});

		temp.add(new Arrow());
		temp.add(startBlock);
		initWidget(temp);
		center.getElement().setAttribute("draggable", "true");

		DragCreator.addDrag(center.getElement(), app, this);
		Vector<Input> inputs = app.getInputs();
		if (listener == null)
			return;
		for (Input input : inputs) {
			if (input.getType().startsWith("File")) {
				InputBlock block = new InputBlock(input, app);
				inputPanel.add(block);
				if (input.getMapped() != null) {
					block.setInputValue(input.getMapped());
				}
			}
		}
		for (Output output : app.getOutputs()) {
			outputPanel.add(new OutputBlock(output, app));
		}

	}

	public void edit() {
		if (app instanceof PipeApp) {
			// final PipeApp app = (PipeApp) this.app;
			HorizontalPanel bar = new HorizontalPanel();
			bar.add(new Seprator());
			bar.add(new Button("Job Options").setClickHandler(new ClickHandler() {
				@Override
                public void onClick(ClickEvent event) {
					// options.show();
				}
			}));
			bar.add(new Seprator());
			// bar.add(new OutputBlock(new Output("User Input", "",
			// "Make the user fill this field out", "", -2)));
			bar.add(new Seprator());
			// Inputs inputs = new Inputs(app.getApp());
			// SC.ask("Configure inputs for step: " + app.getApp().getName(),
			// inputs, bar, new ValueListener<Boolean>() {
			// public void returned(Boolean ret) {
			// if (ret) {
			// app.setJobOptions(options.getSpecs());
			// }
			// }
			// });
		} else if (app instanceof PipelineApp) {
			if (workspace == null) {
				PipelineApp app = (PipelineApp) this.app;
				workspace = new Workspace(app.getPipeline());
				// temp.getElement().getStyle().setPosition(Position.ABSOLUTE);
				workspace.setStyleName("sub-workspace");

				temp.add(workspace);
			} else {
				temp.remove(workspace);
				workspace = null;
			}
		}
	}

	@Override
    public void dragStart(IPCType record) {
		getElement().getStyle().setOpacity(.3);
	}

	@Override
    public boolean dragEnter(IPCType record) {
		return false;
	}

	@Override
    public boolean dragOver(IPCType record) {
		IPCType rec = DragCreator.getDragSource();
		if (rec instanceof Output) {
			inputPanel.setVisible(true);
		} else if ((rec instanceof PipeComponent || rec instanceof UserApp) && !rec.equals(app)) {
			addStyleName("hoverO");
			return true;
		}
		return false;
	}

	@Override
    public void dragLeave(IPCType record) {
		removeStyleName("hoverO");
	}

	@Override
    public void drop(IPCType record) {
		removeStyleName("hoverO");
		getElement().getStyle().setOpacity(1);
		IPCType rec = DragCreator.getDragSource();
		if (rec instanceof PipeComponent) {
			// assume that the source is being moved
			PipeComponent src = (PipeComponent) rec;
			if (src.getPosition() >= 0)
				listener.blockMoved(src, app.getPosition());
			else {
				listener.blockAdded(src, app.getPosition());
			}
		} else if (rec instanceof App) {
			App result = (App) rec;
			PipeApp wrap = new PipeApp(-1, result.getId(), app.getPosition());
			wrap.setApp(result);
			listener.blockAdded(wrap, app.getPosition());
		}

	}

	@Override
    public void dragEnd(IPCType record) {
		getElement().getStyle().setOpacity(1);
		int action = record.getDragAction();
		if (action == DragCreator.DELETE) {
			listener.blockRemoved(app);
		}
		removeStyleName("hoverO");

	}

	@Override
    public Element getDragImage(IPCType record) {
		return startBlock.getElement();
	}

	public void validate() {
		removeStyleName("hoverO");
		for (int i = 0; i < inputPanel.getWidgetCount(); i++) {
			InputBlock inputBlock = (InputBlock) inputPanel.getWidget(i);
			if (inputBlock.getInput().getMapped() != null && inputBlock.getInput().getParent().getPosition() < inputBlock.getInput().getMapped().getParent().getPosition()) {
				inputBlock.inValidate();
			}
		}
	}

}
