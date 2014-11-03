package org.iplantc.de.theme.base.client.fileViewers;

import com.google.gwt.i18n.client.Messages;

/**
 * @author jstroot
 */
public interface FileViewerStrings extends Messages {

    /**
     * Translated "Select a valid page".
     *
     * @return translated "Select a valid page"
     */
    @DefaultMessage("Select a valid page")
    @Key("invalidPage")
    String invalidPage();

    /**
     * @param totalPages the total number of pages to display
     * @return translated "of " + total pages
     */
    @Key("ofTotalPages")
    String ofTotalPages(int totalPages);

    /**
     * Translated "page size".
     *
     * @return translated "page size"
     */
    @DefaultMessage("Page Size (KB)")
    @Key("pageSize")
    String pageSize();

    @Key("editingStatusText")
    String editingStatusText();

    @Key("notEditingStatusText")
    String notEditingStatusText();

    @Key("lineNumberCheckboxLabel")
    String lineNumberCheckboxLabel();

    @Key("addRowButtonTooltip")
    String addRowButtonTooltip();

    @Key("deleteRowButtonTooltip")
    String deleteRowButtonTooltip();

    @Key("headerRowsLabel")
    String headerRowsLabel();

    @Key("skipLinesLabel")
    String skipLinesLabel();

    @Key("previewMarkdownLabel")
    String previewMarkdownLabel();

    /**
     * Translated "Wrap Text".
     *
     * @return translated "Wrap Text"
     */
    @DefaultMessage("Wrap Text")
    @Key("wrap")
    String wrap();

}
