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


import org.iplantc.de.pipelineBuilder.client.ui.Filler;
import org.iplantc.de.pipelineBuilder.client.ui.SimpleButton;
import org.iplantc.de.pipelineBuilder.client.ui.SimpleLabel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SC {

	public static void ask(String message,final ValueListener<String> listener){
		VerticalPanel panel = new VerticalPanel();
		final Window window;
		panel.add(new SimpleLabel(message));
		final TextBox input = new TextBox();
		input.setStyleName("eta-input2");
		panel.add(input);
		input.setFocus(true);
		window=new Window("Input?", panel);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttons.add(new SimpleButton("Cancel").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		buttons.add(new Filler(10));
		buttons.add(new SimpleButton("OK").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				listener.returned(input.getValue());
				window.destroy();
			}
		}));
		buttons.add(new Filler(15));
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(buttons);
		window.showWindow();
	}
	
	public static void ask(String title,String message,final ValueListener<Boolean> listener){
		VerticalPanel panel = new VerticalPanel();
		final Window window;
		panel.add(new SimpleLabel(message));
		window=new Window(title, panel);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		buttons.add(new Filler(10));
		buttons.add(new SimpleButton("Yes").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(listener!=null)
				listener.returned(true);
				window.destroy();
			}
		}));
		buttons.add(new Filler(15));
		buttons.add(new SimpleButton("No").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				listener.returned(false);
				window.destroy();
			}
		}));
		buttons.add(new Filler(15));

		buttons.add(new SimpleButton("Cancel").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		buttons.add(new Filler(15));
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(buttons);
		window.showWindow();
	}
	
	public static void ask(String message,Widget content,final ValueListener<Boolean> listener,String width){
		VerticalPanel panel = new VerticalPanel();
		panel.add(content);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		final Window window = new Window(message, panel);
		buttons.add(new SimpleButton("Cancel").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		buttons.add(new Filler(10));
		buttons.add(new SimpleButton("OK").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(listener!=null)
				listener.returned(true);
				window.destroy();
			}
		}));
		window.setWidth(width);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(buttons);
		window.showWindow();
		
	}	
	public static void show(String message,Widget content){
		VerticalPanel panel = new VerticalPanel();
		panel.add(content);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		final Window window = new Window(message, panel);

		buttons.add(new Filler(10));
		buttons.add(new SimpleButton("OK").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(buttons);
		window.showWindow();
	}
	
	public static void ask(String message,Widget content,final ValueListener<Boolean> listener){
		VerticalPanel panel = new VerticalPanel();
		panel.add(content);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		final Window window = new Window(message, panel);
		buttons.add(new SimpleButton("Cancel").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		buttons.add(new Filler(10));
		buttons.add(new SimpleButton("OK").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(listener!=null)
				listener.returned(true);
				window.destroy();
			}
		}));
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(buttons);
		window.showWindow();
		
	}
	
	
	//used
	public static void ask(String message,Widget content,Widget bar,final ValueListener<Boolean> listener){
		VerticalPanel panel = new VerticalPanel();
		panel.add(content);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		final Window window = new Window(message, panel);
		buttons.add(new SimpleButton("Cancel").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		buttons.add(new Filler(10));
		buttons.add(new SimpleButton("OK").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(listener!=null)
				listener.returned(true);
				window.destroy();
			}
		}));
		window.setWidth("80%");
		window.addBar(bar);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(buttons);
		window.showWindow();
		
	}
	public static void show(String message,Widget content,Widget bar){
		VerticalPanel panel = new VerticalPanel();
		panel.add(content);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		final Window window = new Window(message, panel);
		buttons.add(new Filler(10));
		buttons.add(new SimpleButton("OK").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		window.setWidth("80%");
		window.addBar(bar);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(buttons);
		window.showWindow();
		
	}
	
	public static void alert(String title,String message){
		VerticalPanel panel = new VerticalPanel();
		panel.add(new SimpleLabel(message));
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setWidth("100%");
		buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		final Window window = new Window(title, panel);
		buttons.add(new SimpleButton("OK").addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		}));
		panel.add(buttons);
		window.showWindow();
	}
}
