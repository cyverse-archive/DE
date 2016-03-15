package org.iplantc.de.client.models.analysis;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface Analysis extends HasId, HasName {

    @PropertyName("description")
    String getComments();

    @PropertyName("description")
    void setComments(String comments);

    @PropertyName("wiki_url")
    public void setWikiUrl(String url);

    @PropertyName("resultfolderid")
    public String getResultFolderId();

    @PropertyName("startdate")
    public long getStartDate();

    @PropertyName("enddate")
    public long getEndDate();

    @PropertyName("app_id")
    public String getAppId();

    @PropertyName("app_name")
    public String getAppName();

    @PropertyName("app_description")
    public String getAppDescription();

    public String getStatus();

    @PropertyName("startdate")
    public void setStartDate(long startdate);

    @PropertyName("enddate")
    public void setEndDate(long enddate);

    @PropertyName("analysis_id")
    public void setAppId(String appId);

    @PropertyName("app_name")
    public void setAppName(String appName);

    @PropertyName("app_description")
    public void setAppDescription(String analysis_details);

    public void setStatus(String status);

    public void setId(String id);

    @PropertyName("resultfolderid")
    public void setResultFolderId(String resultfolderid);

    @PropertyName("wiki_url")
    public String getWikiUrl();

    @PropertyName("app_disabled")
    public boolean isAppDisabled();

    @PropertyName("app_disabled")
    public void setAppDisabled(boolean disabled);

    @PropertyName("batch")
    public void setBatch(boolean isBatch);

    @PropertyName("batch")
    public boolean isBatch();

    @PropertyName("batch_status")
    BatchStatus getBatchStatus();

    @PropertyName("batch_status")
    void setBatchStatus(BatchStatus status);

    @PropertyName("username")
    String getUserName();

    // key to determine whether share menu is enabled or not

    @PropertyName("can_share")
    public boolean isSharable();
}
