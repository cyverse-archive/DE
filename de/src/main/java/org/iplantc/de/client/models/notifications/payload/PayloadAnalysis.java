/**
 * 
 */
package org.iplantc.de.client.models.notifications.payload;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * @author sriram
 * 
 */
public interface PayloadAnalysis {

    /**
     * XXX JDS This could be turned into an enum
     * 
     * @return
     */
    @PropertyName("action")
    String getAction();

    @PropertyName("action")
    void setAction(String action);

    @PropertyName("analysis-details")
    String getAnalysisDetails();

    @PropertyName("analysis-details")
    void setAnalysisDetails(String analysisdetails);

    @PropertyName("analysis_id")
    void setAnalysisId(String analysisId);

    @PropertyName("analysis_id")
    String getAnalysisId();

    @PropertyName("analysis_name")
    void setAnalysisName(String name);

    @PropertyName("analysis_name")
    String getAnalysisName();

    @PropertyName("description")
    void setDescription(String desc);

    @PropertyName("description")
    String getDescription();

    @PropertyName("enddate")
    Date getEndDate();

    @PropertyName("enddate")
    void setEndDate(Date date);

    @PropertyName("id")
    void setId(String id);

    @PropertyName("id")
    String getId();

    @PropertyName("name")
    void setName(String name);

    @PropertyName("name")
    String getName();

    @PropertyName("resultfolderid")
    void setResultFolderId(String resultId);

    @PropertyName("resultfolderid")
    String getResultFolderId();

    @PropertyName("startdate")
    Date getStartDate();

    @PropertyName("startdate")
    void setStartDate(Date startdate);

    @PropertyName("status")
    String getStatus();

    @PropertyName("status")
    void setStatus(String status);

}
