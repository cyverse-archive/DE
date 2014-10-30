package org.iplantc.de.fileViewers.client.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class ViewerPagingToolBar extends ToolBar {

    protected LabelToolItem beforePage, afterText;
    protected TextButton first, prev, next, last;
    protected Slider pageSize;
    protected NumberField<Integer> pageText;
    protected PagingToolBar.PagingToolBarAppearance pagingToolBarAppearance = GWT.create(PagingToolBar.PagingToolBarAppearance.class);
    long fileSize;
    private final AbstractFileViewer view;
    private int totalPages;

    public ViewerPagingToolBar(AbstractFileViewer view, long fileSize) {
        this.view = view;
        this.fileSize = fileSize;
        initPageSizeSlider();


        first = new TextButton();
        first.setIcon(pagingToolBarAppearance.first());

        prev = new TextButton();
        prev.setIcon(pagingToolBarAppearance.prev());

        next = new TextButton();
        next.setIcon(pagingToolBarAppearance.next());

        last = new TextButton();
        last.setIcon(pagingToolBarAppearance.last());

        beforePage = new LabelToolItem();

        afterText = new LabelToolItem();

        pageText = new NumberField<>(new NumberPropertyEditor.IntegerPropertyEditor());
        pageText.setWidth("30px");

        addToolbarItems();
        pageText.setValue(1);

        addFirstHandler();
        addLastHandler();
        addNextHandler();
        addPrevHandler();
        addPageSizeChangeHandler();
        addSelectPageKeyHandler();
        computeTotalPages();
    }

    public void addFirstSelectHandler(SelectHandler handler) {
        first.addSelectHandler(handler);
    }

    public void addLastSelectHandler(SelectHandler handler) {
        last.addSelectHandler(handler);
    }

    public void addNextSelectHandler(SelectHandler handler) {
        next.addSelectHandler(handler);
    }

    public void addPageSizeChangeHandler(ValueChangeHandler<Integer> changeHandler) {
        pageSize.addValueChangeHandler(changeHandler);
    }

    public void addPrevSelectHandler(SelectHandler handler) {
        prev.addSelectHandler(handler);
    }

    public void addSelectPageKeyHandler(KeyDownHandler handler) {
        pageText.addKeyDownHandler(handler);
    }

    public int getPageNumber() {
        return pageText.getCurrentValue();
    }

    public void setPageNumber(int i) {
        pageText.setValue(i);
    }

    /**
     * @return page size in bytes
     */
    public long getPageSize() {
        return pageSize.getValue() * 1024;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void onFirst() {
        view.loadData();
    }

    public void onLast() {
        view.loadData();
    }

    public void onNext() {
        view.loadData();
    }

    public void onPageSelect() {
        view.loadData();
    }

    public void onPageSizeChange() {
        view.loadData();
    }

    public void onPrev() {
        view.loadData();
    }

    public void setFirstEnabled(boolean enabled) {
        first.setEnabled(enabled);
    }

    public void setLastEnabled(boolean enabled) {
        last.setEnabled(enabled);
    }

    public void setNextEnabled(boolean enabled) {
        next.setEnabled(enabled);
    }

    public void setPrevEnabled(boolean enabled) {
        prev.setEnabled(enabled);
    }

    public void setTotalPagesText() {
        afterText.setLabel("of " + totalPages);
    }

    private void addFirstHandler() {
        addFirstSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                setPageNumber(1);
                setFirstEnabled(false);
                setPrevEnabled(false);
                setLastEnabled(true);
                setNextEnabled(true);
                onFirst();
            }
        });
    }

    private void addLastHandler() {
        addLastSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                setPageNumber(totalPages);
                setLastEnabled(false);
                setNextEnabled(false);

                setFirstEnabled(true);
                setPrevEnabled(true);
                onLast();
            }
        });
    }

    private void addNextHandler() {
        addNextSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                int temp = getPageNumber() + 1;
                setPageNumber(temp);

                if (temp == totalPages) {
                    setLastEnabled(false);
                    setNextEnabled(false);
                }

                setFirstEnabled(true);
                setPrevEnabled(true);
                onNext();
            }
        });
    }

    private void addPageSizeChangeHandler() {
        addPageSizeChangeHandler(new ValueChangeHandler<Integer>() {

            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                computeTotalPages();
                setPageNumber(1);
                onPageSizeChange();
            }
        });
    }

    private void addPrevHandler() {
        addPrevSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                int temp = getPageNumber() - 1;
                setPageNumber(temp);
                setLastEnabled(true);
                setNextEnabled(true);

                // chk first page
                if (temp - 1 == 0) {
                    setFirstEnabled(false);
                    setPrevEnabled(false);
                } else {
                    setFirstEnabled(true);
                    setPrevEnabled(true);
                }
                onPrev();
            }
        });
    }

    private void addSelectPageKeyHandler() {
        addSelectPageKeyHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    int pageNumber = getPageNumber();
                    if (pageNumber <= totalPages && pageNumber > 0) {
                        pageText.clearInvalid();
                        onPageSelect();
                        if (pageNumber == 1) {
                            setFirstEnabled(false);
                            setPrevEnabled(false);
                            setLastEnabled(true);
                            setNextEnabled(true);
                        } else if (pageNumber == totalPages) {
                            setLastEnabled(false);
                            setNextEnabled(false);
                        } else {
                            setPrevEnabled(true);
                            setNextEnabled(true);
                        }
                    } else {
                        pageText.markInvalid(org.iplantc.de.resources.client.messages.I18N.DISPLAY.inValidPage());
                    }
                }

            }
        });
    }

    private void addToolbarItems() {
        add(new FillToolItem());
        add(new LabelToolItem(org.iplantc.de.resources.client.messages.I18N.DISPLAY.pageSize()));
        add(pageSize);
        add(first);
        add(prev);
        add(new SeparatorToolItem());
        add(beforePage);
        add(pageText);
        add(afterText);
        add(new SeparatorToolItem());
        add(next);
        add(last);
        add(new SeparatorToolItem());
        add(new FillToolItem());
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
        setTotalPagesText();
        setPageNavButtonState();
    }

    private void initPageSizeSlider() {
        pageSize = new Slider();
        pageSize.setMinValue(FileViewer.MIN_PAGE_SIZE_KB);
        pageSize.setMaxValue(FileViewer.MAX_PAGE_SIZE_KB);
        pageSize.setIncrement(FileViewer.PAGE_INCREMENT_SIZE_KB);
        pageSize.setValue(FileViewer.MIN_PAGE_SIZE_KB);
        pageSize.setWidth(100);
    }

    private void setPageNavButtonState() {
        if (totalPages > 1) {
            setFirstEnabled(false);
            setPrevEnabled(false);
            setLastEnabled(true);
            setNextEnabled(true);
        } else {
            setFirstEnabled(false);
            setNextEnabled(false);
            setPrevEnabled(false);
            setLastEnabled(false);
        }
    }

}
