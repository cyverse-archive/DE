package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface AboutApplicationData {

    @PropertyName("release")
    String getReleaseVersion();

    String getBuild();

    String getBuildNumber();

    String getBuildId();

    String getBuildCommit();

    String getBuildBranch();

    String getBuildJdk();
}
