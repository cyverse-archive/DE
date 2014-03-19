package org.iplantc.clavin.zookeeper;

import com.google.common.collect.ImmutableSet;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryNTimes;
import com.netflix.curator.test.TestingServer;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * Unit tests for {@link ZkClientImpl}.
 *
 * @author Dennis Roberts
 */
public class ZkClientImplTest {

    /**
     * The Zookeeper server to use for testing.
     */
    private static TestingServer server;

    /**
     * The Zookeeper client to use for testing.
     */
    private static ZkClientImpl client;

    /**
     * Sets up the test environment.
     *
     * @throws Exception if an error occurs.
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new TestingServer();
        client = new ZkClientImpl(server.getConnectString());
        populateServer();
    }

    /**
     * Populates the test server with some values.
     *
     * @throws Exception if an error occurs.
     */
    private static void populateServer() throws Exception {
        CuratorFramework cf = CuratorFrameworkFactory.newClient(server.getConnectString(), new RetryNTimes(4, 100));
        cf.start();
        setNode(cf, "/foo/bar/baz/quux/node1", "node1-value");
        setNode(cf, "/foo/bar/baz/quux/node2", "node2-value");
        setNode(cf, "/foo/bar/baz/quux/node3", "node3-value");
        cf.close();
    }

    /**
     * Sets a single node in Zookeeper, creating parent nodes if necessary.
     *
     * @param cf the client connection.
     * @param path the path to the node.
     * @param value the value of the node as a string.
     * @throws Exception if an error occurs.
     */
    private static void setNode(CuratorFramework cf, String path, String value) throws Exception {
        cf.create().creatingParentsIfNeeded().forPath(path, value.getBytes(ZkClientImpl.DEFAULT_ENCODING));
    }

    /**
     * Closes the server.
     *
     * @throws IOException if an error occurs.
     */
    @AfterClass
    public static void tearDownClass() throws IOException {
        server.close();
    }

    /**
     * Verifies that getChildren successfully returns the list of child node names.
     */
    @Test
    public void testGetChildren() {
        try {
            client.connect();
            Set<String> actual = ImmutableSet.copyOf(client.getChildren("/foo/bar/baz/quux"));
            Set<String> expected = ImmutableSet.copyOf(Arrays.asList("node1", "node2", "node3"));
            assertEquals(expected, actual);
        }
        finally {
            client.disconnect();
        }
    }

    /**
     * Verifies that we get a ChildListException if we try to list the children of a non-existent node.
     */
    @Test(expected = ChildListException.class)
    public void getChildrenShouldHandleMissingNode() {
        try {
            client.connect();
            client.getChildren("/i/dont/exist");
        }
        finally {
            client.disconnect();
        }
    }

    /**
     * Verifies that readNode successfully returns the value of a node.
     */
    @Test
    public void testReadNode() {
        try {
            client.connect();
            assertEquals("node1-value", client.readNode("/foo/bar/baz/quux/node1"));
            assertEquals("node2-value", client.readNode("/foo/bar/baz/quux/node2"));
            assertEquals("node3-value", client.readNode("/foo/bar/baz/quux/node3"));
            assertEquals("node1-value", client.readNode("/foo/bar/baz/quux", "node1"));
            assertEquals("node2-value", client.readNode("/foo/bar/baz/quux", "node2"));
            assertEquals("node3-value", client.readNode("/foo/bar/baz/quux", "node3"));
        }
        finally {
            client.disconnect();
        }
    }

    /**
     * Verifies that we get a ReadNodeException if we try to read a node that doesn't exist.
     */
    @Test(expected = ReadNodeException.class)
    public void readNodeShouldHandleMissingNode() {
        try {
            client.connect();
            client.readNode("/i/dont/exist");
        }
        finally {
            client.disconnect();
        }
    }
}
