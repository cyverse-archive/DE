package org.iplantc.de.client.models.search;

import org.iplantc.de.client.models.search.FileSizeRange.FileSizeUnit;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface SearchAutoBeanFactory extends AutoBeanFactory {

    public static final SearchAutoBeanFactory INSTANCE = GWT.create(SearchAutoBeanFactory.class);

    AutoBean<DiskResourceQueryTemplate> dataSearchFilter();

    AutoBean<DateInterval> dateInterval();

    AutoBean<FileSizeRange> fileSizeRange();

    AutoBean<FileSizeUnit> fileSizeUnit();

    AutoBean<DiskResourceQueryTemplateList> drQtList();

}
