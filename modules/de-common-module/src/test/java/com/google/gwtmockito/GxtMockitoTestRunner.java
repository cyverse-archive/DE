package com.google.gwtmockito;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.NorthSouthContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

import org.junit.runners.model.InitializationError;

import java.util.Collection;

public class GxtMockitoTestRunner extends GwtMockitoTestRunner {

    public GxtMockitoTestRunner(Class<?> unitTestClass) throws InitializationError {
        super(unitTestClass);
    }

    @Override
    protected Collection<Class<?>> getClassesToStub() {
        Collection<Class<?>> classes = super.getClassesToStub();
        classes.add(XElement.class);
        classes.add(SimpleContainer.class);
        classes.add(ResizeContainer.class);
        classes.add(Container.class);
        classes.add(StyleInjectorHelper.class);
        classes.add(NorthSouthContainer.class);
        classes.add(Draggable.class);
        classes.add(GridDragSource.class);
        classes.add(Component.class);
        classes.add(XDOM.class);
        classes.add(ListView.class);
        classes.add(AutoBean.class);
        classes.add(SimpleBeanEditorDriver.class);
        return classes;
    }

}
