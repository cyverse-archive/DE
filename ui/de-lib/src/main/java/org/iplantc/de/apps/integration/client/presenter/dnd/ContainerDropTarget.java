package org.iplantc.de.apps.integration.client.presenter.dnd;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;

import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.Insert;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.InsertResizeContainer;

/**
 * A generic DropTarget class which handles drag and drop insert and append operations for
 * {@link InsertResizeContainer}s.
 *
 * @author jstroot
 * 
 * @param <W>
 */
public class ContainerDropTarget<W extends Container> extends DropTarget {

    protected final W container;
    protected int insertIndex;
    boolean before;
    private boolean autoScroll = true;
    private AutoScrollSupport scrollSupport = null;

    public ContainerDropTarget(W container) {
        super(container);
        this.container = container;
    }

    public ContainerDropTarget(W container, XElement scrollElement) {
        this(container);
        if (scrollElement != null) {
            scrollSupport = new AutoScrollSupport(scrollElement);
        }
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        Object dragData = event.getDragSource().getData();
        if (autoScroll) {
            if (scrollSupport == null) {
                scrollSupport = new AutoScrollSupport(container.getElement());
                scrollSupport.setScrollDelay(200);
            } else if (scrollSupport.getScrollElement() == null) {
                scrollSupport.setScrollElement(container.getElement());
            }
        }
        if (verifyDragData(dragData)) {
            // It exists within the container
            event.setCancelled(false);
            event.getStatusProxy().setStatus(true);
            scrollSupport.start();

            return;
        }

        event.getStatusProxy().setStatus(false);
        event.setCancelled(true);
    }

    /**
     * Autoscroll is 'true' by default.
     * 
     * @param autoScroll
     */
    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public void setAutoScrollSupport(XElement scrollElement){
        scrollSupport = new AutoScrollSupport(scrollElement);
    }

    public void setScrollDelay(int scrollDelay) {
        scrollSupport.setScrollDelay(scrollDelay);
    }

    public void setScrollRegionHeight(int scrollRegionHeight) {
        scrollSupport.setScrollRegionHeight(scrollRegionHeight);
    }

    public void setScrollRepeatDelay(int scrollRepeatDelay) {
        scrollSupport.setScrollRepeatDelay(scrollRepeatDelay);
    }

    /**
     * This method will become necessary if we ever support dropping multiple ArgumentGroups
     * 
     * @param event
     * @param index
     * @return
     */
    protected int adjustIndex(DndDragMoveEvent event, int index) {
        return index;
    }

    @Override
    protected void onDragCancelled(DndDragCancelEvent event) {
        super.onDragCancelled(event);
        if (autoScroll && (scrollSupport != null)) {
            scrollSupport.stop();
        }
    }

    @Override
    protected void onDragDrop(DndDropEvent event) {
        Object dragData = event.getData();
        if (autoScroll && (scrollSupport != null)) {
            scrollSupport.stop();
        }
        if (!verifyDragData(dragData)) {
            return;
        }
    }

    @Override
    protected void onDragFail(DndDropEvent event) {
        super.onDragFail(event);
        if (autoScroll && (scrollSupport != null)) {
            scrollSupport.stop();
        }
    }

    @Override
    protected void onDragLeave(DndDragLeaveEvent event) {
        super.onDragLeave(event);
        if (autoScroll && (scrollSupport != null)) {
            scrollSupport.stop();
        }
    }

    @Override
    protected void onDragMove(DndDragMoveEvent event) {
        EventTarget target = event.getDragMoveEvent().getNativeEvent().getEventTarget();
        if (verifyDragMove(target, event.getData())) {
            event.setCancelled(false);
            event.getStatusProxy().setStatus(true);
            return;
        }

        event.setCancelled(true);
        event.getStatusProxy().setStatus(false);
    }

    @Override
    protected void showFeedback(DndDragMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        EventTarget target = event.getDragMoveEvent().getNativeEvent().getEventTarget();

        if (feedback == Feedback.INSERT || feedback == Feedback.BOTH) {
            Element childElement = null;
            int childCount = container.getElement().getChildCount();
            int childIndex = -1;
            for (int j = 0; j < childCount; j++) {
                Element child = container.getElement().getChild(j).cast();
                if (child.isOrHasChild(Element.as(target))) {
                    childElement = child;
                    childIndex = j;
                    break;
                }
            }

            if (childElement == null && container.getWidgetCount() > 0) {
                // Then set childElement to last child in container, and update the childIndex
                childIndex = container.getWidgetCount() - 1;
                childElement = container.getWidget(childIndex).getElement();
            }

            if (childElement != null) {
                int height = childElement.getOffsetHeight();
                int mid = height / 2;
                mid += childElement.getAbsoluteTop();
                int y = event.getDragMoveEvent().getNativeEvent().getClientY();
                before = y < mid;
                int idx = before ? childIndex : childIndex + 1;

                insertIndex = adjustIndex(event, idx);

                showInsert(childElement);
            } else {
                insertIndex = 0;
            }
        }
    }

    protected boolean verifyDragData(Object dragData) {
        if (dragData != null) {
            return true;
        }
        return false;
    }

    protected boolean verifyDragMove(EventTarget target, Object dragData) {
        XElement conElement = container.getElement();
        Element as = Element.as(target);
        if (Element.is(target) && conElement.isOrHasChild(as) && verifyDragData(dragData)) {
            // The drag is currently over this container, or one of its children.
            return true;
        }
        return false;
    }

    private void showInsert(Element childElement) {
        Insert insert = Insert.get();
        insert.show(childElement);
        Rectangle rect = XElement.as(childElement).getBounds();
        int y = !before ? (rect.getY() + rect.getHeight() - 4) : rect.getY() - 2;
        insert.getElement().setBounds(rect.getX(), y, rect.getWidth(), 6);
    }
}
