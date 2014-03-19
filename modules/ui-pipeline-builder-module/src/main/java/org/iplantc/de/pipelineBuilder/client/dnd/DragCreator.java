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
package org.iplantc.de.pipelineBuilder.client.dnd;

import org.iplantc.de.client.models.pipelines.PipelineApp;
import org.iplantc.de.client.models.pipelines.PipelineAppData;
import org.iplantc.de.pipelineBuilder.client.json.App;
import org.iplantc.de.pipelineBuilder.client.json.IPCType;
import org.iplantc.de.pipelineBuilder.client.json.Input;
import org.iplantc.de.pipelineBuilder.client.json.Output;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Image;

import java.util.List;

public class DragCreator {

	private static IPCType draggedRecord;
	public static final int MOVE = 1;
	public static final int COPY = 2;
	public static final int DELETE = 3;
	public static JavaScriptObject dragEvent;

	public static native void addDrag(Element element, IPCType rec, DragListener listener) /*-{
		function handleDragStart(e) {

			var dragIcon = listener.@org.iplantc.de.pipelineBuilder.client.dnd.DragListener::getDragImage(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
			e.dataTransfer.setDragImage(dragIcon, -10, -10);
			//e.dataTransfer.effectAllowed = 'copy';
			@org.iplantc.de.pipelineBuilder.client.dnd.DragCreator::draggedRecord = rec;
			listener.@org.iplantc.de.pipelineBuilder.client.dnd.DragListener::dragStart(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
			e.dataTransfer.effectAllowed = 'all';

			if (element.getAttribute("data-downloadurl") != null) {
				e.dataTransfer.setData("DownloadURL", element.getAttribute("data-downloadurl"));
			} else {
				e.dataTransfer.setData('Text',rec.@org.iplantc.de.pipelineBuilder.client.json.IPCType::getId()()); // required otherwise doesn't work
				@org.iplantc.de.pipelineBuilder.client.dnd.DragCreator::dragEvent = dragIcon;
			}
		}

		function handleDragOver(e) {
			if (e.stopPropagation) {
				e.stopPropagation(); // stops the browser from redirecting.
			}
			if (e.preventDefault)
				e.preventDefault();
			var canDrop = listener.@org.iplantc.de.pipelineBuilder.client.dnd.DragListener::dragOver(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
			if (canDrop)
				e.dataTransfer.dropEffect = 'copy';
			else {
				e.dataTransfer.dropEffect = 'move';
			}
			return false;
		}

		function handleDragEnter(e) {
			if (e.stopPropagation) {
				e.stopPropagation(); // stops the browser from redirecting.
			}
			if (e.preventDefault)
				e.preventDefault();
			var canDrop = listener.@org.iplantc.de.pipelineBuilder.client.dnd.DragListener::dragEnter(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
			if (canDrop)
				e.dataTransfer.dropEffect = 'copy';
			else {
				e.dataTransfer.dropEffect = 'move';
			}
			return false;
		}

		function handleDragLeave(e) {
			listener.@org.iplantc.de.pipelineBuilder.client.dnd.DragListener::dragLeave(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
		}
		function handleDrop(e) {
			// this / e.target is current target element.
			if (e.stopPropagation) {
				e.stopPropagation(); // stops the browser from redirecting.
			}
			if (e.preventDefault) {
				e.preventDefault();
			}
			listener.@org.iplantc.de.pipelineBuilder.client.dnd.DragListener::drop(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
		}

		function handleDragEnd(e) {
			listener.@org.iplantc.de.pipelineBuilder.client.dnd.DragListener::dragEnd(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
		}

		element.addEventListener('dragstart', handleDragStart, false);
		element.addEventListener('dragenter', handleDragEnter, false);
		element.addEventListener('dragover', handleDragOver, false);
		element.addEventListener('dragleave', handleDragLeave, false);
		element.addEventListener('drop', handleDrop, false);
		element.addEventListener('dragend', handleDragEnd, false);
    }-*/;

	public static native void addDrop(Element element, IPCType rec, DropListener listener) /*-{
		function handleDragOver(e) {
			if (e.stopPropagation) {
				e.stopPropagation(); // stops the browser from redirecting.
			}
			if (e.preventDefault)
				e.preventDefault();
			var canDrop = listener.@org.iplantc.de.pipelineBuilder.client.dnd.DropListener::dragOver(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
			if (canDrop)
				e.dataTransfer.dropEffect = 'copy';
			else {
				e.dataTransfer.dropEffect = 'move';
			}
			return false;
		}

		function handleDragEnter(e) {
			if (e.stopPropagation) {
				e.stopPropagation(); // stops the browser from redirecting.
			}
			if (e.preventDefault)
				e.preventDefault();
			var canDrop = listener.@org.iplantc.de.pipelineBuilder.client.dnd.DropListener::dragEnter(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
			if (canDrop)
				e.dataTransfer.dropEffect = 'copy';
			else {
				e.dataTransfer.dropEffect = 'move';
			}
			return false;
		}

		function handleDragLeave(e) {
			listener.@org.iplantc.de.pipelineBuilder.client.dnd.DropListener::dragLeave(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
		}
		function handleDrop(e) {
			// this / e.target is current target element.
			if (e.stopPropagation) {
				e.stopPropagation(); // stops the browser from redirecting.
			}
			if (e.preventDefault) {
				e.preventDefault();
			}
			listener.@org.iplantc.de.pipelineBuilder.client.dnd.DropListener::drop(Lorg/iplantc/de/pipelineBuilder/client/json/IPCType;)(rec);
		}

		function addEvent(el, type, fn) {
			if (el && el.nodeName || el === window) {
				el.addEventListener(type, fn, false);
			} else if (el && el.length) {
				for ( var i = 0; i < el.length; i++) {
					addEvent(el[i], type, fn);
				}
			}
		}
		//		addEvent(element, 'dragenter', handleDragEnter);
		addEvent(element, 'dragover', handleDragOver);
		addEvent(element, 'dragleave', handleDragLeave);
		addEvent(element, 'drop', handleDrop);
    }-*/;

	public static IPCType getDragSource() {
		return draggedRecord;
	}

    public static App createApp(PipelineApp json) {
        App app = new App();
        app.setName(json.getName());
        app.setDescription(json.getDescription());
        app.setId(1);
        app.setID(json.getId());
        app.setTemplateId(json.getTemplateId());

        List<PipelineAppData> inputs = json.getInputs();
        app.setAppDataInputs(inputs);
        for (PipelineAppData dataObj : inputs) {
            Input input = new Input();
            if (dataObj != null) {
                input.setName(dataObj.getName());
                input.setDescription(dataObj.getDescription());
                input.setId(1);
                input.setRequired(dataObj.getRequired());
                input.setType("File:" + dataObj.getFormat());
                input.setID(dataObj.getId());
                app.addInput(input);
            }
        }

        List<PipelineAppData> outputs = json.getOutputs();
        app.setAppDataOutputs(outputs);
        for (PipelineAppData dataObj : outputs) {
            Output output = new Output();
            if (dataObj != null) {
                output.setName(dataObj.getName());
                output.setDescription(dataObj.getDescription());
                output.setId(1);
                output.setType(dataObj.getFormat());
                output.setID(dataObj.getId());
                app.addOutput(output);
            }
        }

        return app;
    }

	public static Element getImageElement(String src) {
		Image img = new Image(src);
		img.setWidth("20px");
		img.setHeight("20px");
		return img.getElement();
	}

}
