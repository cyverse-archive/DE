package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.fileViewers.client.events.ViewerPagingToolbarUpdatedEvent;
import org.iplantc.de.fileViewers.client.events.ViewerPagingToolbarUpdatedEvent.ViewerPagingToolbarUpdatedEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;

/**
 * @author jstroot
 */
public class ViewerPagingToolBar extends Composite {

    @UiTemplate("ViewerPagingToolBar.ui.xml")
    interface ViewerPagingToolBarUiBinder extends UiBinder<Widget, ViewerPagingToolBar> { }

    public interface ViewerPagingToolBarAppearance {
        int MAX_PAGE_SIZE_KB = 1024;
        int MIN_PAGE_SIZE_KB = 8;
        int PAGE_INCREMENT_SIZE_KB = 8;

        String afterTextLabel(int totalPages); // "of " + total pages

        String invalidPage();

        String pageNumberFieldWidth(); // 30px

        int getMaxPageSizeKb();

        int getMinPageSizeKb();

        int getPageIncrementSizeKb();

        int sliderWidth(); // 100

        String pageSizeLabel(); // DISPLAY.pageSize()
    }

    private static ViewerPagingToolBarUiBinder BINDER = GWT.create(ViewerPagingToolBarUiBinder.class);

    @UiField LabelToolItem afterText;
    @UiField TextButton first, prev, next, last;
    @UiField Slider pageSizeSlider;
    @UiField IntegerField pageNumber;
    @UiField ViewerPagingToolBarAppearance appearance;

    long fileSize;
    private int totalPages;

    public ViewerPagingToolBar(long fileSize) {
        this.fileSize = fileSize;

        initWidget(BINDER.createAndBindUi(this));
        pageNumber.setValue(1, false);

        computeTotalPages();
    }

    public HandlerRegistration addViewerPagingToolbarUpdatedEventHandler(ViewerPagingToolbarUpdatedEventHandler changeHandler) {
        return addHandler(changeHandler, ViewerPagingToolbarUpdatedEvent.TYPE);
    }

    public int getPageNumber() {
        return pageNumber.getCurrentValue();
    }

    /**
     * @return page size in bytes
     */
    public long getPageSize() {
        return pageSizeSlider.getValue() * 1024;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @UiHandler("pageNumber")
    void onPageNumberValueChange(ValueChangeEvent<Integer> event) {
        fireEvent(new ViewerPagingToolbarUpdatedEvent(pageNumber.getValue(), getPageSize()));
    }

    @UiHandler("pageSizeSlider")
    void onPageSizeSliderValueChange(ValueChangeEvent<Integer> event) {
        computeTotalPages();
        pageNumber.setValue(1, false);
        fireEvent(new ViewerPagingToolbarUpdatedEvent(pageNumber.getValue(), getPageSize()));
    }

    @UiHandler("first")
    void onFirstSelect(SelectEvent event){
        pageNumber.setValue(1, true);
        first.setEnabled(false);
        prev.setEnabled(false);
        last.setEnabled(true);
        next.setEnabled(true);
    }

    @UiHandler("last")
    void onLastSelect(SelectEvent event){
        pageNumber.setValue(totalPages, true);
        last.setEnabled(false);
        next.setEnabled(false);

        first.setEnabled(true);
        prev.setEnabled(true);
    }

    @UiHandler("next")
    void onNextSelect(SelectEvent event){
        int temp = getPageNumber() + 1;
        pageNumber.setValue(temp, true);

        if (temp == totalPages) {
            last.setEnabled(false);
            next.setEnabled(false);
        }

        first.setEnabled(true);
        prev.setEnabled(true);
    }

    @UiHandler("prev")
    void onPrevSelect(SelectEvent event){
        int temp = getPageNumber() - 1;
        pageNumber.setValue(temp, true);
        last.setEnabled(true);
        next.setEnabled(true);

        // chk first page
        if (temp - 1 == 0) {
            first.setEnabled(false);
            prev.setEnabled(false);
        } else {
            first.setEnabled(true);
            prev.setEnabled(true);
        }
    }

    @UiHandler("pageNumber")
    void onPageNumberKeyDown(KeyDownEvent event){
        if (event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
            return;
        }

        int currPageNumber = getPageNumber();
        if (currPageNumber <= totalPages && currPageNumber > 0) {
            pageNumber.clearInvalid();
            if (currPageNumber == 1) {
                first.setEnabled(false);
                prev.setEnabled(false);
                last.setEnabled(true);
                next.setEnabled(true);
            } else if (currPageNumber == totalPages) {
                last.setEnabled(false);
                next.setEnabled(false);
            } else {
                prev.setEnabled(true);
                next.setEnabled(true);
            }
        } else {
            pageNumber.markInvalid(appearance.invalidPage());
        }
    }

    private void computeTotalPages() {
        long pageSize = getPageSize();
        totalPages = 0;
        if (fileSize < pageSize) {
            totalPages = 1;
        } else {
            totalPages = (int) ((fileSize / pageSize));
            if (fileSize % pageSize > 0) {
                totalPages++;
            }

        }
        afterText.setLabel(appearance.afterTextLabel(totalPages));
        setPageNavButtonState();
    }

    @UiFactory
    Slider createPageSizeSlider() {
        Slider slider = new Slider();
        slider.setMinValue(appearance.getMinPageSizeKb());
        slider.setMaxValue(appearance.getMaxPageSizeKb());
        slider.setIncrement(appearance.getPageIncrementSizeKb());
        slider.setValue(appearance.getMinPageSizeKb());
        slider.setWidth(appearance.sliderWidth());
        return slider;
    }

    private void setPageNavButtonState() {
        if (totalPages > 1) {
            first.setEnabled(false);
            prev.setEnabled(false);
            last.setEnabled(true);
            next.setEnabled(true);
        } else {
            first.setEnabled(false);
            next.setEnabled(false);
            prev.setEnabled(false);
            last.setEnabled(false);
        }
    }

}
