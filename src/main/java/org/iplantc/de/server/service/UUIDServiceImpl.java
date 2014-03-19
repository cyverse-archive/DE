package org.iplantc.de.server.service;

import org.iplantc.de.client.services.UUIDService;

import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("serial")
public class UUIDServiceImpl extends RemoteServiceServlet implements UUIDService {

    @Override
    public List<String> getUUIDs(int num) {
       List<String> uuids = Lists.newArrayList();
        for(int i = 0; i < num; i++){
            uuids.add(UUID.randomUUID().toString());
        }
        return uuids;
    }

}
