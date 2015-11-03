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
import org.iplantc.de.pipelineBuilder.client.json.Input;
import org.iplantc.de.pipelineBuilder.client.json.PipeApp;
import org.iplantc.de.pipelineBuilder.client.json.PipeComponent;
import org.iplantc.de.pipelineBuilder.client.json.Pipeline;
import org.iplantc.de.pipelineBuilder.client.json.UserApp;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Vector;

public class Workspace extends FlowPanel implements DropListener, BlockChangeListener {
	private Vector<PipeComponent> wrappers;
	private int offset = 0;
	private Pipeline pipe;
	private int id = 0;
	private static int onId = 0;

	public Workspace(Pipeline pipeline) {
		id = onId++;
		loadPipeline(pipeline);
		pipe = pipeline;
		DragCreator.addDrop(getElement(), null, this);
	}

	@Override
    public boolean dragEnter(IPCType recod) {
		IPCType record = DragCreator.getDragSource();
		if (record instanceof UserApp) {
			// addStyleName("hover");
		}
		return true;
	}

	@Override
    public boolean dragOver(IPCType recor) {
		IPCType record = DragCreator.getDragSource();
		if (record instanceof UserApp|| record instanceof App|| record instanceof PipeComponent) {
			return true;
		}
		return false;
	}

	@Override
    public void dragLeave(IPCType record) {
		removeStyleName("hover");
	}

	@Override
    public void drop(IPCType reco) {
		IPCType record = DragCreator.getDragSource();

		if (record instanceof App) {
			App app = (App) record;
			appendApp(app);
		} else if (record instanceof PipeComponent) {
			// assume this is being moved and add to the bottom of the stack
			PipeComponent wrap = (PipeComponent) record;
			if (wrap.getPosition() >= 0) {
				Widget wid = getWidget(wrap.getPosition() + offset);
				remove(wrap.getPosition() + offset);
				add(wid);
				wrappers.remove(wrap.getPosition());
				wrappers.add(wrap);
				for (int i = 0; i < wrappers.size(); i++) {
					wrappers.get(i).setPosition(i);
				}
			} else {
				wrappers.add(wrap);
				add(new Block(wrap, Workspace.this,false));
				for (int i = 0; i < wrappers.size(); i++) {
					wrappers.get(i).setPosition(i);
				}
			}
		}else if(record instanceof Input){
			record.setDragAction(DragCreator.DELETE);
		}
		removeStyleName("hover");
	}

	public void dragEnd(IPCType record) {
		removeStyleName("hover");
	}

	public void appendApp(App app) {
		PipeApp wrap = new PipeApp(-1, app.getId(), wrappers.size());
		wrap.setApp(app);
		wrappers.add(wrap);
		add(new Block(wrap, Workspace.this,false));
		for (int i = 0; i < wrappers.size(); i++) {
			wrappers.get(i).setPosition(i);
		}
	}

	@Override
    public void blockMoved(PipeComponent wrapper, int before) {
		if (wrapper.getPosition() == before)
			return;
		Widget wid = getWidget(wrapper.getPosition() + offset);
		remove(wrapper.getPosition() + offset);
		wrappers.remove(wrapper.getPosition());
		if (wrapper.getPosition() > before) {
			insert(wid, before + offset);
			wrappers.insertElementAt(wrapper, before);
		} else {
			insert(wid, before + offset - 1);
			wrappers.insertElementAt(wrapper, before - 1);
		}
		for (int i = 0; i < wrappers.size(); i++) {
			wrappers.get(i).setPosition(i);
		}
		revalidate();
	}

	@Override
    public void blockRemoved(PipeComponent wrapper) {
		remove(wrapper.getPosition() + offset);
		wrappers.remove(wrapper.getPosition());
		for (int i = 0; i < wrappers.size(); i++) {
			wrappers.get(i).setPosition(i);
		}
		revalidate();
	}

	public void revalidate() {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget wid = getWidget(i);
			if (wid instanceof Block) {
				((Block) wid).validate();
			}
		}
	}

	@Override
    public void blockAdded(PipeComponent wrapper, int before) {
		insert(new Block(wrapper, this,false), before + offset);
		wrappers.insertElementAt(wrapper, before);
		for (int i = 0; i < wrappers.size(); i++) {
			wrappers.get(i).setPosition(i);
		}
		removeStyleName("hover");
	}

	@Override
    public Vector<PipeComponent> getPreviousApps(int position) {
		Vector<PipeComponent> ret = new Vector<PipeComponent>();
		for (int i = position - 1; i >= 0; i--) {
			ret.add(wrappers.get(i));
		}
		return ret;
	}

	public void addNonBlock(Widget w) {
		insert(w, 0);
		offset++;
	}

	/**
	 * @param pipeline
	 */
	public void loadPipeline(Pipeline pipeline) {
		pipe = pipeline;
		wrappers = pipeline.getSteps();

		clear();
		for (PipeComponent wrapper : wrappers) {
			add(new Block(wrapper, this,true));
		}
	}

	public int getId() {
		return id;
	}

	public Pipeline getPipeline() {
		return pipe;
	}
}
