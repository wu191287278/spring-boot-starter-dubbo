package com.alibaba.dubbo.registry.hazelcast;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.support.AbstractRegistry;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.*;
import com.hazelcast.instance.HazelcastInstanceFactory;

import java.util.*;

/**
 * Created by wuyu on 2017/4/24.
 */
public class HazelcastRegistry extends AbstractRegistry {

    private HazelcastInstance hazelcastInstance;

    private final ReplicatedMap<String, Set<String>> replicatedMap;

    private final String nodeId;

    public HazelcastRegistry(URL url) {
        super(url);
        Config config = new Config("dubbo");

        if (url.getParameter("managementCenter") != null) {
            String managerUrl = url.getParameter("managementCenter");
            config.getManagementCenterConfig().setEnabled(true);
            config.getManagementCenterConfig().setUrl(managerUrl);
            config.getManagementCenterConfig().setUpdateInterval(url.getParameter("updateInterval", 5));
        }
        config.setGroupConfig(new GroupConfig(url.getUsername() == null ? "dubbo" : url.getUsername(), url.getPassword() == null ? "dubbo" : url.getPassword()));
        this.hazelcastInstance = HazelcastInstanceFactory.getOrCreateHazelcastInstance(config);
        this.hazelcastInstance.getCluster().addMembershipListener(new MembershipListener() {
            @Override
            public void memberAdded(MembershipEvent membershipEvent) {

            }

            @Override
            public void memberRemoved(MembershipEvent membershipEvent) {
               replicatedMap.remove(membershipEvent.getMember().getUuid());
            }

            @Override
            public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {

            }
        });
        this.replicatedMap = hazelcastInstance.getReplicatedMap("dubbo-registered");
        this.nodeId = hazelcastInstance.getCluster().getLocalMember().getUuid();
        replicatedMap.put(nodeId, new LinkedHashSet<String>());
        replicatedMap.addEntryListener(new EntryAdapter<String, Set<String>>() {
            @Override
            public void onEntryEvent(EntryEvent<String, Set<String>> event) {
                HazelcastRegistry.this.notify(toUrl(event.getValue()));
            }

            @Override
            public void onMapEvent(MapEvent event) {
                HazelcastRegistry.this.notify(new ArrayList<URL>());
            }
        });
    }

    @Override
    public boolean isAvailable() {
        return hazelcastInstance.getCluster().getClusterState().equals(ClusterState.ACTIVE);
    }

    @Override
    public void register(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("register url == null");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Register: " + url);
        }


        Set<String> urls = replicatedMap.get(nodeId);
        urls.add(url.toFullString());

        ILock lock = hazelcastInstance.getLock(nodeId);
        lock.lock();
        try {
            replicatedMap.put(nodeId, urls);
        } finally {
            lock.unlock();
        }
        getRegistered().add(url);
    }

    @Override
    public void unregister(URL url) {
        Set<String> urls = replicatedMap.get(this.nodeId);
        ILock lock = hazelcastInstance.getLock(nodeId);
        lock.lock();
        try {
            urls.remove(url.toFullString());
        } finally {
            lock.unlock();
        }
        getRegistered().remove(url);
    }

    @Override
    public Set<URL> getRegistered() {
        Set<URL> registered = new LinkedHashSet<>();
        for (Set<String> values : replicatedMap.values()) {
            registered.addAll(toUrl(values));
        }
        return registered;
    }


    @Override
    public void destroy() {
        try {
            ILock lock = hazelcastInstance.getLock(nodeId);
            lock.lock();
            try {
                Set<String> urls = replicatedMap.remove(nodeId);
                notify(toUrl(urls));
            } finally {
                lock.unlock();
            }
            hazelcastInstance.shutdown();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private List<URL> toUrl(Collection<String> urls) {
        if (urls == null) {
            return new ArrayList<>();
        }
        List<URL> converts = new ArrayList<>();
        for (String url : urls) {
            converts.add(URL.valueOf(URL.decode(url)));
        }
        return converts;
    }


    public String getNodeId() {
        return nodeId;
    }
}
