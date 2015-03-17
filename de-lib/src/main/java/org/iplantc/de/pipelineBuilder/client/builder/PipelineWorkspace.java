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
import org.iplantc.de.pipelineBuilder.client.dnd.DropListener;
import org.iplantc.de.pipelineBuilder.client.json.App;
import org.iplantc.de.pipelineBuilder.client.json.IPCType;
import org.iplantc.de.pipelineBuilder.client.json.Pipeline;
import org.iplantc.de.pipelineBuilder.client.ui.SimpleLabel;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PipelineWorkspace extends Composite {
	private final Image trashImg = new Image(IplantResources.RESOURCES.trashClose().getSafeUri().asString());
	private Workspace workspace;
	private TextBox nameBox;
	private TextBox descBox;
	private Pipeline pipeline;
	private final FlowPanel userInputs = new FlowPanel();
	private SimpleLabel nameLabel;
	private SimpleLabel descLabel;

	public PipelineWorkspace(Pipeline pipeline) {
		FlowPanel pane = new FlowPanel();
		initWidget(pane);
		this.pipeline = pipeline;
		workspace = new Workspace(pipeline);

		// workspace.add(new ForBlock());
		pane.add(workspace);
		pane.add(trashImg);
		VerticalPanel infoPane = new VerticalPanel();
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, new SimpleLabel("Name:"));
		nameBox = new TextBox();
		nameBox.setText(pipeline.getName());
		table.setWidget(0, 1, nameBox);
		table.setWidget(1, 0, new SimpleLabel("Description:"));
		descBox = new TextBox();
		descBox.setText(pipeline.getDescription());
		loadNonBlocks();
		table.setWidget(2, 0, descBox);
		table.getFlexCellFormatter().setColSpan(1, 0, 2);
		table.getFlexCellFormatter().setColSpan(2, 0, 2);
		infoPane.setWidth("100%");
		table.setWidth("100%");
		infoPane.add(table);
		workspace.setStyleName("pipe-workspace");
		workspace.setHeight("100%");

		trashImg.setStyleName("trash");

		DragCreator.addDrop(trashImg.getElement(), new TrashCan(), new DropListener() {
			@Override
            public void drop(IPCType record) {
				trashImg.setUrl(IplantResources.RESOURCES.trashClose().getSafeUri().asString());
				DragCreator.getDragSource().setDragAction(DragCreator.DELETE);
			}

			@Override
            public boolean dragOver(IPCType record) {
				trashImg.setUrl(IplantResources.RESOURCES.trashOpen().getSafeUri().asString());
				return true;
			}

			@Override
            public void dragLeave(IPCType record) {
				trashImg.setUrl(IplantResources.RESOURCES.trashClose().getSafeUri().asString());
			}

			@Override
            public boolean dragEnter(IPCType record) {
				trashImg.setUrl(IplantResources.RESOURCES.trashOpen().getSafeUri().asString());
				return true;
			}

		});

	}

	public void removeBlock(Block block) {
		workspace.remove(block);
	}

	public void add(Widget wid) {
		workspace.add(wid);
	}

	public void loadPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
		loadNonBlocks();
		workspace.loadPipeline(pipeline);
		userInputs.clear();

	}

	private void loadNonBlocks() {
		final FlowPanel center = new FlowPanel();
		center.setStyleName("start-block");

		String name = pipeline.getName();
        if (name == null || name.isEmpty()) {
            name = "Click to edit name";
			pipeline.setName(name);
		}
        String description = pipeline.getDescription();
        if (description == null || description.isEmpty()) {
            description = "Click to edit description";
            pipeline.setDescription(description);
        }

        nameLabel = new SimpleLabel(name);
		descLabel = new SimpleLabel(description);
		
		String nameT = name;
		if(nameT.length()>30){
			nameLabel.setToolTip(nameT);
			nameT= nameT.substring(0, 27)+"...";
		}
		nameLabel.setText(nameT);
		
		String descT = description;
		if(descT.length()>30){
			nameLabel.setToolTip(descT);
			descT= descT.substring(0, 27)+"...";
		}
		descLabel.setText(descT);
		
		
		center.add(nameLabel);
		center.add(descLabel);
		nameLabel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				center.remove(0);
				nameBox.setText(pipeline.getName());
				center.insert(nameBox, 0);
				nameBox.setFocus(true);
			}
		}, ClickEvent.getType());

		nameBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				center.remove(0);
				pipeline.setName(nameBox.getText());
				String nameT = pipeline.getName();
				if(nameT.length()>30){
					nameLabel.setToolTip(nameT);
					nameT= nameT.substring(0, 27)+"...";
				}
				nameLabel.setText(nameT);
				center.insert(nameLabel, 0);
			}
		});
		nameBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					nameBox.setFocus(false);
				}
			}
		});
		
		descBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					descBox.setFocus(false);
				}
			}
		});
		descLabel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				center.remove(1);
				descBox.setText(pipeline.getDescription());
				center.insert(descBox, 1);
				descBox.setFocus(true);
			}
		}, ClickEvent.getType());

		descBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				center.remove(1);
				String descT = descBox.getText();
				if(descT.length()>30){
					descLabel.setToolTip(descT);
					descT= descT.substring(0, 27)+"...";
				}
				descLabel.setText(descT);
				pipeline.setDescription(descBox.getText());
				center.insert(descLabel, 1);
			}
		});

		workspace.addNonBlock(center);
	}

	public Pipeline getPipeline() {
		return pipeline;
	}

	public void appendApp(App app) {
		workspace.appendApp(app);
	}
}
