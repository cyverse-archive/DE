package org.iplantc.de.server.services;

import org.iplantc.de.client.services.UUIDService;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author jstroot
 */
public class UUIDServiceImpl implements UUIDService {

    @Override
    public ArrayList<String> getUUIDs(int num) {
       ArrayList<String> uuids = Lists.newArrayList();
        for(int i = 0; i < num; i++){
            uuids.add(UUID.randomUUID().toString());
        }
        return uuids;
    }

}
