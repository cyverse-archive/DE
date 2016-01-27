package org.iplantc.de.client.models.tool;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * Created by aramsey on 10/30/15.
 */
public interface ToolTestData {

    @PropertyName("input_files")
    List<String> getInputFiles();

    @PropertyName("input_files")
    void setInputFiles(List<String> inputFiles);

    @PropertyName("output_files")
    List<String> getOutputFiles();

    @PropertyName("output_files")
    void setOutputFiles(List<String> outputFiles);

}
