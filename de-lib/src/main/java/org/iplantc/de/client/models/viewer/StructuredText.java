package org.iplantc.de.client.models.viewer;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.Splittable;

public interface StructuredText {

    String DATA_KEY = "csv";
    String COL_KEY = "max-cols";
    String TEXT_CHUNK_KEY = "chunk";

    @PropertyName("chunk-size")
    String getChunkSize();

    @PropertyName("max-cols")
    String getMaxColumns();

    @PropertyName("page")
    String getPage();

    String getPath();

    @PropertyName("number-pages")
    int getNumberPages();

    @PropertyName("file-size")
    long getFileSize();

    @PropertyName("csv")
    Splittable getData();
}
