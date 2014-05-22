package org.iplantc.de.client.viewer.views;

import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.theme.gray.client.toolbar.GrayPagingToolBarAppearance;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public abstract class AbstractPagingToolbar extends ToolBar {

    GrayPagingToolBarAppearance appearance = new GrayPagingToolBarAppearance();
    protected TextButton first, prev, next, last;
    protected LabelToolItem beforePage, afterText;
    protected NumberField<Integer> pageText;
    protected Slider pageSize;
    private int totalPages;
    long fileSize;
    protected TextButton saveBtn;
    protected boolean editing;

    public AbstractPagingToolbar(long fileSize, boolean editing) {
        this.fileSize = fileSize;
        this.editing = editing;
        initPageSizeSlider();

        first = new TextButton();
        first.setIcon(appearance.first());

        prev = new TextButton();
        prev.setIcon(appearance.prev());

        next = new TextButton();
        next.setIcon(appearance.next());

        last = new TextButton();
        last.setIcon(appearance.last());

        beforePage = new LabelToolItem();

        afterText = new LabelToolItem();

        pageText = new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
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

        saveBtn = new TextButton("", IplantResources.RESOURCES.save());
        add(saveBtn);
        // saveBtn.addSelectHandler(new SelectHandler() {
        //
        // @Override
        // public void onSelect(SelectEvent event) {
        // AbstractPagingToolbar.this.fireEvent(new SaveFileEvent());
        //
        // }
        // });

    }

    private void addToolbarItems() {
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
    }

    private void initPageSizeSlider() {
        pageSize = new Slider();
        pageSize.setMinValue(FileViewer.MIN_PAGE_SIZE_KB);
        pageSize.setMaxValue(FileViewer.MAX_PAGE_SIZE_KB);
        pageSize.setIncrement(FileViewer.PAGE_INCREMENT_SIZE_KB);
        pageSize.setValue(FileViewer.MIN_PAGE_SIZE_KB);
        pageSize.setWidth(100);
        pageSize.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.pageSize());
    }

    /**
     * 
     * @return page size in bytes
     */
    public long getPageSize() {
        return pageSize.getValue() * 1024;
    }

    public void addFirstSelectHandler(SelectHandler handler) {
        first.addSelectHandler(handler);
    }

    public void addPrevSelectHandler(SelectHandler handler) {
        prev.addSelectHandler(handler);
    }

    public void addNextSelectHandler(SelectHandler handler) {
        next.addSelectHandler(handler);
    }

    public void addLastSelectHandler(SelectHandler handler) {
        last.addSelectHandler(handler);
    }

    public void addSelectPageKeyHandler(KeyDownHandler handler) {
        pageText.addKeyDownHandler(handler);
    }

    public void setPrevEnabled(boolean enabled) {
        prev.setEnabled(enabled);
    }

    public void setFirstEnabled(boolean enabled) {
        first.setEnabled(enabled);
    }

    public void setNextEnabled(boolean enabled) {
        next.setEnabled(enabled);
    }

    public void setLastEnabled(boolean enabled) {
        last.setEnabled(enabled);
    }

    public void setPageNumber(int i) {
        pageText.setValue(i);
    }

    public int getPageNumber() {
        return pageText.getCurrentValue();
    }

    public void setTotalPagesText() {
        afterText.setLabel("of " + totalPages);
    }

    public int getToltalPages() {
        return totalPages;
    }

    public void addPageSizeChangeHandler(ValueChangeHandler<Integer> changeHandler) {
        pageSize.addValueChangeHandler(changeHandler);
    }

    public abstract void onFirst();

    public abstract void onLast();

    public abstract void onPrev();

    public abstract void onNext();

    public abstract void onPageSizeChange();

    public abstract void onPageSelect();

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

    private void computeTotalPages() {
        long pageSize = getPageSize();
        totalPages = 0;
        if (fileSize < pageSize) {
            totalPages = 1;
        } else {
            totalPages = (int)((fileSize / pageSize));
            if (fileSize % pageSize > 0) {
                totalPages++;
            }

        }
        setTotalPagesText();
        setPageNavButtonState();
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

    public void setEditing(boolean editing) {
        saveBtn.setEnabled(editing);
    }

    /**
     * @return the editing
     */
    public boolean isEditing() {
        return editing;
    }

}
