package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.FileSizeRange;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.util.SearchModelUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class uses a builder pattern to construct a search query from a given query template.
 *
 * If a field in the given query template is null or empty, the corresponding search term will be omitted
 * from the final query.
 *
 * @author jstroot
 */
@SuppressWarnings("nls")
public class DataSearchQueryBuilder {

    public static final String METADATA_VALUE = "metadata.value";
    public static final String METADATA_ATTRIBUTE = "metadata.attribute";
    public static final String FIELDS = "fields";
    public static final String QUERY_STRING = "query_string";
    public static final String LESSER = "lte";
    public static final String GREATER = "gte";
    public static final String RANGE2 = "range";
    public static final String WILDCARD = "wildcard";
    public static final String OR_OPERATOR = " OR ";
    public static final String DATE_MODIFIED = "dateModified";
    public static final String QUERY2 = "query";
    public static final String PATH = "path";
    public static final String METADATA2 = "metadata";
    public static final String NESTED2 = "nested";
    public static final String HAS_CHILD = "has_child";
    public static final String FILE_SIZE = "fileSize";
    public static final String LABEL = "label";
    public static final String DATE_CREATED = "dateCreated";
    public static final String BOOL = "bool";

    private final DiskResourceQueryTemplate dsf;
    private final UserInfo userinfo;
    private final Splittable mustList;
    private final Splittable mustNotList;
    private final SearchModelUtils searchModelUtils;

    Splittable query = StringQuoter.createSplittable();
    Splittable bool = addChild(query, BOOL);

    Logger LOG = Logger.getLogger(DataSearchQueryBuilder.class.getName());

    public DataSearchQueryBuilder(DiskResourceQueryTemplate dsf, UserInfo userinfo) {
        this.dsf = dsf;
        this.userinfo = userinfo;
        this.searchModelUtils = SearchModelUtils.getInstance();
        mustList = StringQuoter.createIndexed();
        mustNotList = StringQuoter.createIndexed();
    }

    public String buildFullQuery() {
        ownedBy().createdWithin()
                 .file()
                 .fileSizeRange()
                 .metadataAttribute()
                 .metadataValue()
                 .modifiedWithin()
                 .negatedFile()
                 .sharedWith()
                 .taggedWith();

        LOG.fine("search query==>" + toString());
        return toString();
    }

    public String taggedWith() {
        Set<Tag> tags = dsf.getTagQuery();

        if (tags == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Tag it : tags) {
            sb.append(it.getId());
            sb.append(",");
        }

        // delete last comma
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();

    }

    /**
     * {"nested":{"path":"userPermissions", "query":{"bool":{"must":[{"term":{"permission":"own"}},
     * {"wildcard":{"user":(some query)}}]}}}}
     */
    public DataSearchQueryBuilder ownedBy() {
        String queryContent = dsf.getOwnedBy();
        if (!Strings.isNullOrEmpty(queryContent)) {
            appendArrayItem(mustList, createOwnerQuery(queryContent));
        }
        return this;
    }

    /**
     * {"range": {"dateModified": {"gte":(some query),"lte":(some query)}}}
     */
    public DataSearchQueryBuilder createdWithin() {
        if ((dsf.getCreatedWithin() != null)) {
            Date dateFrom = dsf.getCreatedWithin().getFrom();
            Date dateTo = dsf.getCreatedWithin().getTo();
            if ((dateFrom != null) && (dateTo != null)) {
                // {"range": {"dateCreated": {"gte":"1380559151000","lte":"1390511909000"}}}
                Splittable range = createRangeQuery(DATE_CREATED, dateFrom.getTime(), dateTo.getTime());
                appendArrayItem(mustList, range);
            } else if (dateFrom != null) {
                // {"range": {"dateModified": {"gte":"1380559151000"}}}
                Splittable range = createMinRangeQuery(DATE_CREATED, dateFrom.getTime());
                appendArrayItem(mustList, range);
            } else if (dateTo != null) {
                // {"range": {"dateModified": {"lte":"1390511909000"}}}
                Splittable range = createMaxRangeQuery(DATE_CREATED, dateTo.getTime());
                appendArrayItem(mustList, range);
            }
        }
        return this;
    }

    /**
     * {"wildcard":{"label":(some query)}}
     */
    public DataSearchQueryBuilder file() {
        String content = dsf.getFileQuery();
        if (!Strings.isNullOrEmpty(content)) {
            /*
             * { "simple_query_string": { "query": "*test*|*csv*", "fields": [ "label" ] } }
             */
            appendArrayItem(mustList, getSimpleQuery(LABEL, content));
        }
        return this;
    }

    /**
     * {"range": {"fileSize": {"gte":(some query),"lte":(some query)}}}
     */
    public DataSearchQueryBuilder fileSizeRange() {
        FileSizeRange fileSizeRange = dsf.getFileSizeRange();
        if (fileSizeRange != null) {
            Double minSize = searchModelUtils.convertFileSizeToBytes(fileSizeRange.getMin(),
                                                                     fileSizeRange.getMinUnit());
            Double maxSize = searchModelUtils.convertFileSizeToBytes(fileSizeRange.getMax(),
                                                                     fileSizeRange.getMaxUnit());

            if ((minSize != null) && (maxSize != null)) {
                // {"range": {"fileSize": {"gte":"1000","lte":"100000"}}}
                appendArrayItem(mustList,
                                createRangeQuery(FILE_SIZE, minSize.longValue(), maxSize.longValue()));
            } else if (minSize != null) {
                // {"range": {"fileSize": {"gte":"1000"}}}
                Splittable range = createMinRangeQuery(FILE_SIZE, minSize.longValue());
                appendArrayItem(mustList, range);
            } else if (maxSize != null) {
                // {"range": {"fileSize": {"lte":"100000"}}}
                Splittable range = createMaxRangeQuery(FILE_SIZE, maxSize.longValue());
                appendArrayItem(mustList, range);
            }
        }
        return this;
    }

    /**
     * @return the currently constructed query.
     */
    public String getQuery() {
        return toString();
    }

    private Splittable metadataNested(String content, String field) {
        // {"nested":{"path":"metadata","query":{"query_string":{"query":"*ipc* OR *attrib*","fields":["metadata.attribute"]}}}}
        Splittable metadata = StringQuoter.createSplittable();

        Splittable nested = addChild(metadata, NESTED2);
        StringQuoter.create(METADATA2).assign(nested, PATH);

        getSimpleQuery(field, content).assign(nested, QUERY2);
        return metadata;
    }

    private Splittable childQuery(String type, Splittable innerQuery) {
        // {"has_child": {"type": "...", "score_mode": "max", "query": {...}}
        Splittable query = StringQuoter.createSplittable();
        Splittable hasChild = addChild(query, HAS_CHILD);
        StringQuoter.create(type).assign(hasChild, "type");
        StringQuoter.create("max").assign(hasChild, "score_mode");
        innerQuery.assign(hasChild, QUERY2);
        return query;
    }

    public DataSearchQueryBuilder metadataAttribute() {
        String content = dsf.getMetadataAttributeQuery();
        if (!Strings.isNullOrEmpty(content)) {
            Splittable attr = StringQuoter.createSplittable();
            Splittable attrBool = addChild(attr, BOOL);
            Splittable attrShouldList = addArray(attrBool, "should");

            Splittable metadata = metadataNested(content, METADATA_ATTRIBUTE);
            appendArrayItem(attrShouldList, metadata);

            Splittable childFileQuery = childQuery("file_metadata", metadata.deepCopy());
            appendArrayItem(attrShouldList, childFileQuery);

            Splittable childFolderQuery = childQuery("folder_metadata", metadata.deepCopy());
            appendArrayItem(attrShouldList, childFolderQuery);

            appendArrayItem(mustList, attr);
        }
        return this;
    }

    public DataSearchQueryBuilder metadataValue() {
        String content = dsf.getMetadataValueQuery();
        if (!Strings.isNullOrEmpty(content)) {
            Splittable value = StringQuoter.createSplittable();
            Splittable valueBool = addChild(value, BOOL);
            Splittable valueShouldList = addArray(valueBool, "should");

            Splittable metadata = metadataNested(content, METADATA_VALUE);
            appendArrayItem(valueShouldList, metadata);

            Splittable childFileQuery = childQuery("file_metadata", metadata.deepCopy());
            appendArrayItem(valueShouldList, childFileQuery);

            Splittable childFolderQuery = childQuery("folder_metadata", metadata.deepCopy());
            appendArrayItem(valueShouldList, childFolderQuery);

            appendArrayItem(mustList, value);
        }
        return this;
    }

    /**
     * {"range": {"dateModified": {"gte":(some query),"lte":(some query)}}}
     */
    public DataSearchQueryBuilder modifiedWithin() {
        if ((dsf.getModifiedWithin() != null)) {
            Date dateFrom = dsf.getModifiedWithin().getFrom();
            Date dateTo = dsf.getModifiedWithin().getTo();

            if ((dateFrom != null) && (dateTo != null)) {
                // {"range": {"dateModified": {"gte":"1380559151000","lte":"1390511909000"}}}
                Splittable range = createRangeQuery(DATE_MODIFIED, dateFrom.getTime(), dateTo.getTime());
                appendArrayItem(mustList, range);
            } else if (dateFrom != null) {
                // {"range": {"dateModified": {"gte":"1380559151000"}}}
                Splittable range = createMinRangeQuery(DATE_MODIFIED, dateFrom.getTime());
                appendArrayItem(mustList, range);
            } else if (dateTo != null) {
                // {"range": {"dateModified": {"lte":"1390511909000"}}}
                Splittable range = createMaxRangeQuery(DATE_MODIFIED, dateTo.getTime());
                appendArrayItem(mustList, range);
            }
        }
        return this;
    }

    public DataSearchQueryBuilder negatedFile() {
        String content = dsf.getNegatedFileQuery();
        if (!Strings.isNullOrEmpty(content)) {
            /*
             * { "simple_query_string": { "query": "*test*|*csv*", "fields": [ "label" ] } }
             */
            appendArrayItem(mustNotList, getSimpleQuery(LABEL, content));

        }
        return this;
    }

    public DataSearchQueryBuilder sharedWith() {
        String content = applyImplicitUsernameWildcard(dsf.getSharedWith());
        if (!Strings.isNullOrEmpty(content)) {
            // {"bool":{"must":[{"nested":{"path":"userPermissions","query":{"bool":{"must":[{"term":{"permission":"own"}},{"wildcard":{"userPermissions.user":"currentUser#*"}}]}}}},{"nested":{"path":"userPermissions","query":{"bool":{"must":[{"wildcard":{"user":queryContent}}]}}}}]}}
            Splittable query = StringQuoter.createSplittable();
            Splittable bool = addChild(query, BOOL);
            Splittable must = addArray(bool, "must");

            appendArrayItem(must, createOwnerQuery(userinfo.getUsername()));

            Splittable sharedWith = StringQuoter.createSplittable();

            Splittable nested = addChild(sharedWith, NESTED2);
            StringQuoter.create("userPermissions").assign(nested, PATH);

            createWildcard("userPermissions.user", content).assign(nested, QUERY2);

            appendArrayItem(must, sharedWith);

            appendArrayItem(mustList, query);
        }
        return this;
    }

    /**
     * Applies "implicit asterisks" to the front and end of every search term delimited term in the given
     * searchText string if that string does not contain any of the following characters:
     *
     * <pre>
     * *
     * ?
     * \
     * </pre>
     *
     * @return a string whose space-delimited terms are prepended and appended with "*" if the given
     * string does not contain *, ?, nor /.
     */
    String applyImplicitAsteriskSearchText(final String searchText) {
        String implicitSearchText = "";
        if (searchText.matches(".*[*?\\\\]+.*")) {
            // Leave text alone
            implicitSearchText = searchText;
        } else {
            // Apply implicit "*"
            final Iterable<String> transform = Iterables.transform(Splitter.on(OR_OPERATOR)
                                                                           .omitEmptyStrings()
                                                                           .trimResults()
                                                                           .split(searchText),
                                                                   new Function<String, String>() {
                                                                       @Override
                                                                       public String apply(String input) {
                                                                           return "*".concat(input)
                                                                                     .concat("*");
                                                                       }
                                                                   });
            implicitSearchText = Joiner.on(OR_OPERATOR).join(transform);
        }
        return implicitSearchText;
    }

    /**
     * Join multiple search text using '|'
     */
    String applyOROperator(final String searchText) {
        String implicitSearchText = "";
        final Iterable<String> transform =
                Splitter.on(" ").trimResults().omitEmptyStrings().split(searchText);
        implicitSearchText = Joiner.on(OR_OPERATOR).join(transform);
        return implicitSearchText;
    }

    /**
     * @return the currently constructed query.
     */
    @Override
    public String toString() {
        // {"bool":{"must":[mustList],"must_not":[mustNotList]}}

        mustList.assign(bool, "must");
        mustNotList.assign(bool, "must_not");

        return query.getPayload();
    }

    private Splittable createWildcard(String field, String content) {
        // {"wildcard": {field: content}}
        // Use lowercase values since wildcard queries are not analyzed by the
        // service and values are indexed as lowercase.
        content = Strings.isNullOrEmpty(content) ? null : content.toLowerCase();
        return createQuery(WILDCARD, field, content);
    }

    private Splittable createQuery(String queryType, String field, String content) {
        // {queryType: {field: content}}
        Splittable query = StringQuoter.createSplittable();

        Splittable wildcard = addChild(query, queryType);
        StringQuoter.create(content).assign(wildcard, field);

        return query;
    }

    private Splittable createRangeQuery(String field, long lowerLimit, long upperLimit) {
        // {"range": {field: {"gte": lowerLimit,"lte": upperLimit}}}
        Splittable query = StringQuoter.createSplittable();

        Splittable range = addChild(addChild(query, RANGE2), field);

        StringQuoter.create(String.valueOf(lowerLimit)).assign(range, GREATER);
        StringQuoter.create(String.valueOf(upperLimit)).assign(range, LESSER);

        return query;
    }

    private Splittable createMinRangeQuery(String field, long lowerLimit) {
        // {"range": {field: {"gte": lowerLimit}}}
        Splittable query = StringQuoter.createSplittable();

        Splittable range = addChild(addChild(query, RANGE2), field);

        StringQuoter.create(String.valueOf(lowerLimit)).assign(range, GREATER);

        return query;
    }

    private Splittable createMaxRangeQuery(String field, long upperLimit) {
        // {"range": {field: {"lte": upperLimit}}}
        Splittable query = StringQuoter.createSplittable();

        Splittable range = addChild(addChild(query, RANGE2), field);

        StringQuoter.create(String.valueOf(upperLimit)).assign(range, LESSER);

        return query;
    }

    private Splittable createOwnerQuery(String user) {
        // {"nested":{"path":"userPermissions","query":{"bool":{"must":[{"term":{"userPermissions.permission":"own"}},{"wildcard":{"userPermissions.user":queryContent}}]}}}}
        Splittable ownedBy = StringQuoter.createSplittable();

        Splittable nested = addChild(ownedBy, NESTED2);
        StringQuoter.create("userPermissions").assign(nested, PATH);

        Splittable query = addChild(nested, QUERY2);
        Splittable bool = addChild(query, BOOL);
        Splittable must = addArray(bool, "must");

        appendArrayItem(must, createQuery("term", "userPermissions.permission", "own"));
        appendArrayItem(must, createWildcard("userPermissions.user", applyImplicitUsernameWildcard(user)));

        return ownedBy;
    }

    private String applyImplicitUsernameWildcard(String user) {
        // usernames are formatted as user#zone
        if (!Strings.isNullOrEmpty(user) && !user.endsWith("*") && !user.contains("#")) {
            user += "#*";
        }

        return user;
    }

    private Splittable addChild(Splittable parent, String key) {
        Splittable child = StringQuoter.createSplittable();
        child.assign(parent, key);
        return child;
    }

    private Splittable addArray(Splittable parent, String key) {
        Splittable child = StringQuoter.createIndexed();
        child.assign(parent, key);
        return child;
    }

    private void appendArrayItem(Splittable array, Splittable item) {
        item.assign(array, array.size());
    }

    public Splittable getSimpleQuery(String field, String userEntry) {
        // {"query": {"query_string": {"query": "*la* OR *foo*", "fields":["whatever"]}}}
        Splittable query = StringQuoter.createSplittable();
        Splittable simpleQuery = addChild(query, QUERY_STRING);
        String entry = applyImplicitAsteriskSearchText(applyOROperator(userEntry));
        StringQuoter.create(entry).assign(simpleQuery, QUERY2);
        Splittable fieldsArr = addArray(simpleQuery, FIELDS);
        appendArrayItem(fieldsArr, StringQuoter.create(field));
        return query;
    }
}
