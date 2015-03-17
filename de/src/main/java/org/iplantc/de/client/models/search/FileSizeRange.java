package org.iplantc.de.client.models.search;

import org.iplantc.de.client.models.HasLabel;

public interface FileSizeRange {

    public interface FileSizeUnit extends HasLabel {
        int getUnit();

        void setUnit(int unit);
    }

    Double getMax();

    Double getMin();

    boolean isInclusive();

    void setInclusive(boolean inclusive);

    void setMax(Double max);

    void setMin(Double min);

    FileSizeUnit getMaxUnit();

    void setMaxUnit(FileSizeUnit unit);

    FileSizeUnit getMinUnit();

    void setMinUnit(FileSizeUnit unit);
}
