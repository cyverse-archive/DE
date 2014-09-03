package org.iplantc.admin.belphegor.server;

import org.iplantc.de.server.DEServiceCallResolver;

import java.io.IOException;

public class BelphegorServiceCallResolver extends DEServiceCallResolver {

    public BelphegorServiceCallResolver() throws IOException {
        super(BelphegorProperties.get().getProperties());
    }
}
