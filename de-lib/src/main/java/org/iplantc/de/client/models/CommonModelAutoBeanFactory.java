package org.iplantc.de.client.models;

import org.iplantc.de.client.models.viewer.StructuredText;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface CommonModelAutoBeanFactory extends AutoBeanFactory {

    AutoBean<AboutApplicationData> aboutApplicationData();

    AutoBean<HasId> hasId();

    AutoBean<HasPath> hasPath();

    AutoBean<HasPaths> hasPaths();

    AutoBean<UserBootstrap> bootstrap();

    AutoBean<UserSession> userSession();

    AutoBean<StructuredText> getStructuredText();
}
