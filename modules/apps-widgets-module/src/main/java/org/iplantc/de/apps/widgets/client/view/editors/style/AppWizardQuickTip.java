package org.iplantc.de.apps.widgets.client.view.editors.style;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.form.error.SideErrorHandler.SideErrorTooltipAppearance;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tips.ToolTip;

/**
 * XXX CLONE AND OWN of {@link QuickTip}
 * 
 * This class is a direct clone of {@link QuickTip}. The only difference is a change to the constructor,
 * where we are passing the {@link SideErrorTooltipAppearance} to the super constructor.
 */
public class AppWizardQuickTip extends ToolTip {

    private boolean initialized;
    private boolean interceptTitles;
    private Element targetElem;

    /**
     * Creates a new quick tip instance.
     * 
     * @param component the source component
     */
    public AppWizardQuickTip(Widget component) {
        super(component, GWT.<SideErrorTooltipAppearance> create(SideErrorTooltipAppearance.class));
    }

    /**
     * Returns true if intercept titles is enabled.
     * 
     * @return the intercept title state
     */
    public boolean isInterceptTitles() {
        return interceptTitles;
    }

    /**
     * True to automatically use the element's DOM title value if available
     * (defaults to false).
     * 
     * @param interceptTitles true to to intercept titles
     */
    public void setInterceptTitles(boolean interceptTitles) {
        this.interceptTitles = interceptTitles;
    }

    @Override
    protected void delayHide() {
        if (!isAttached()) {
            targetElem = null;
            bodyHtml = null;
            titleHtml = null;
        }
        super.delayHide();
    }

    @Override
    protected void onHide() {
        super.onHide();
        targetElem = null;
        bodyHtml = null;
        titleHtml = null;
    }

    @Override
    protected void onTargetMouseMove(MouseMoveEvent event) {
        onMouseMove(event.getNativeEvent().<Event> cast());
    }

    @Override
    protected void onTargetMouseOut(MouseOutEvent event) {
        onTargetOut(event.getNativeEvent().<Event> cast());
    }

    @Override
    protected void onTargetMouseOver(MouseOverEvent event) {
        onTargetOver(event.getNativeEvent().<Event> cast());
    }

    @Override
    protected void onTargetOut(Event ce) {
        EventTarget to = ce.getRelatedEventTarget();
        if (to == null || (Element.is(target.getElement()) && Element.is(to) && !DOM.isOrHasChild(target.getElement(), (Element)Element.as(to)))) {
            super.onTargetOut(ce);
        }
    }

    @Override
    protected void onTargetOver(Event ce) {
        if (disabled) {
            return;
        }

        Element t = ce.getEventTarget().cast();
        while (t != null && t != target.getElement()) {
            if (hasTip(t)) {
                break;
            }
            t = (Element)t.getParentElement();
        }

        boolean hasTip = t != null && hasTip(t);

        if (!initialized && !hasTip) {
            return;
        }
        initialized = true;

        if ((targetElem == null || !isAttached()) && hasTip) {
            updateTargetElement(t);
        } else {
            if (hasTip && targetElem != t) {
                updateTargetElement(t);
            } else if (targetElem != null && getElement().isOrHasChild(targetElem)) {
                return;
            } else {
                delayHide();
                return;
            }
        }
        clearTimers();
        targetXY = ce.<XEvent> cast().getXY();
        delayShow();
    }

    private String getAttributeValue(Element target, String attr) {
        String v = target.getAttribute(attr);
        return hasAttributeValue(v) ? v : null;
    }

    private boolean hasAttributeValue(String v) {
        return v != null && !v.equals("");
    }

    private boolean hasTip(Element target) {
        return hasAttributeValue(target.getAttribute("qtip")) || (interceptTitles && hasAttributeValue(target.getAttribute("title")));
    }

    private void updateTargetElement(Element target) {
        targetElem = target;
        bodyHtml = getAttributeValue(target, interceptTitles ? "title" : "qtip");
        titleHtml = interceptTitles ? null : getAttributeValue(target, "qtitle");

        String width = getAttributeValue(target, "qwidth");
        if (width != null && !"".equals(width)) {
            setWidth(Util.parseInt(width, 100));
        }
    }

}
