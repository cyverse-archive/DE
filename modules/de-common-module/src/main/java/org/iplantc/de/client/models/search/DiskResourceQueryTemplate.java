package org.iplantc.de.client.models.search;

import org.iplantc.de.client.models.diskResources.Folder;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * This object is used to collect the information required to build a search request for the endpoints
 * described <a href=
 * "https://github.com/iPlantCollaborativeOpenSource/Donkey/blob/dev/doc/endpoints/filesystem/search.md"
 * >here</a>
 * 
 * @author jstroot
 * 
 */
public interface DiskResourceQueryTemplate extends Folder {

    @PropertyName("execution-time")
    void setExecutionTime(long execution_time);

    @PropertyName("execution-time")
    long getExecutionTime();

    DateInterval getCreatedWithin();

    String getFileQuery();

    FileSizeRange getFileSizeRange();

    /**
     * Overrides the default property name binding of "id" to "label"
     * 
     * @see org.iplantc.de.client.models.HasId#getId()
     */
    @Override
    @PropertyName("label")
    String getId();

    String getMetadataAttributeQuery();

    String getMetadataValueQuery();

    DateInterval getModifiedWithin();

    String getNegatedFileQuery();

    /**
     * creator.username:(here is the content)
     * 
     * @return
     */
    String getOwnedBy();

    String getSharedWith();

    /**
     * @return true if the results of this query should include items from users' Trash, false otherwise.
     */
    boolean isIncludeTrashItems();

    /**
     * @return true if this template has unsaved changes, false otherwise.
     */
    boolean isDirty();

    /**
     * @return true if this template has been persisted, false otherwise.
     */
    boolean isSaved();

    void setCreatedWithin(DateInterval createdWithin);

    /**
     * Sets the templates dirty state.
     * 
     * @param dirty true if the template has unsaved changes.
     */
    void setDirty(boolean dirty);

    void setFileQuery(String fileQuery);

    void setFileSizeRange(FileSizeRange fileSizeRange);

    /**
     * Overrides the default property name binding of "id" to "label"
     * 
     * @see org.iplantc.de.client.models.diskResources.DiskResource#setId(java.lang.String)
     */
    @Override
    @PropertyName("label")
    void setId(String id);

    /**
     * Sets whether the results of this query should include items from any users' Trash.
     * 
     * @param include
     */
    void setIncludeTrashItems(boolean include);

    void setMetadataAttributeQuery(String attributeValues);

    void setMetadataQuery(String metadataQuery);

    void setMetadataValueQuery(String metadataValueValues);

    void setModifiedWithin(DateInterval modifiedWithin);

    void setNegatedFileQuery(String negatedFileQuery);

    void setOwnedBy(String ownedBy);

    void setSharedWith(String sharedWith);
}
