package org.iplantc.de.client.util;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.FileSizeRange.FileSizeUnit;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.List;

public class SearchModelUtils {

    private final static List<String> fileSizeUnits = Lists.newArrayList("KB", "MB", "GB", "TB");
    private final static SearchAutoBeanFactory factory = GWT.create(SearchAutoBeanFactory.class);

    public static DiskResourceQueryTemplate createDefaultFilter() {
        Splittable defFilter = StringQuoter.createSplittable();
        // Need to create full permissions by default in order to function as a "smart folder"
        Splittable permissions = StringQuoter.createSplittable();
        StringQuoter.create(true).assign(permissions, "own");
        StringQuoter.create(true).assign(permissions, "read");
        StringQuoter.create(true).assign(permissions, "write");
        permissions.assign(defFilter, "permissions");
        StringQuoter.create("/savedFilters/").assign(defFilter, "path");

        DiskResourceQueryTemplate dataSearchFilter = AutoBeanCodex.decode(factory, DiskResourceQueryTemplate.class, defFilter).as();
        dataSearchFilter.setCreatedWithin(factory.dateInterval().as());
        dataSearchFilter.setModifiedWithin(factory.dateInterval().as());
        dataSearchFilter.setFileSizeRange(factory.fileSizeRange().as());
        dataSearchFilter.getFileSizeRange().setMaxUnit(createDefaultFileSizeUnit());
        dataSearchFilter.getFileSizeRange().setMinUnit(createDefaultFileSizeUnit());

        return dataSearchFilter;
    }

    public static Double convertFileSizeToBytes(Double size, FileSizeUnit unit) {
        if (size != null && unit != null && unit.getUnit() > 0) {
            return size * Math.pow(1024, unit.getUnit());
        }

        return size;
    }

    public static List<FileSizeUnit> createFileSizeUnits() {
        List<FileSizeUnit> ret = Lists.newArrayList();

        int unit = 0;
        for (String fsLabel : fileSizeUnits) {
            unit++;
            ret.add(createFileSizeUnit(unit, fsLabel));
        }

        return ret;
    }

    private static FileSizeUnit createDefaultFileSizeUnit() {
        return createFileSizeUnit(1, fileSizeUnits.get(0));
    }

    private static FileSizeUnit createFileSizeUnit(int unit, String label) {
        FileSizeUnit fsUnit = factory.fileSizeUnit().as();
        fsUnit.setUnit(unit);
        fsUnit.setLabel(label);
        return fsUnit;
    }
}
