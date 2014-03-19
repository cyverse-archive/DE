package org.iplantc.clavin.zookeeper;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Map;

/**
 * A mock Zookeeper client for testing.
 *
 * @author Dennis Roberts
 */
public class MockZkClient extends AbstractZkClient {

    /**
     * The nodes stored in the mock Zookeeper instance.
     */
    private Map<String, String> nodes = new HashMap<String, String>();

    /**
     * Establishes the connection.  In the mock implementation of this interface, this is a no-op.
     */
    public void connect() {}

    /**
     * Drops the connection.  In the mock implementation of this interface, this is a no-op.
     */
    public void disconnect() {}

    /**
     * Adds a node to the mock Zookeeper instance.
     *
     * @param path the path to the node.
     * @param value the value to store in the node.
     */
    public void addNode(String path, String value) {
        nodes.put(validatePath(path), value);
    }

    /**
     * Gets the list of children for the node corresponding to a path.
     *
     * @param path the path to the node.
     * @return the collection of children of that node.
     */
    public Iterable<String> getChildren(final String path) {
        validatePath(path);
        Iterable<String> children = Iterables.filter(nodes.keySet(), new Predicate<String>() {
            public boolean apply(String t) {
                return t.startsWith(path);
            }
        });
        return Iterables.transform(children, new Function<String, String>() {
            public String apply(String f) {
                return f.replaceFirst(path + "/", "").split("/", 2)[0];
            }
        });
    }

    /**
     * Reads the data at the node corresponding to a path.
     *
     * @param path the path to the node.
     * @return the data stored in the node as a string.
     */
    public String readNode(String path) {
        if (!nodes.containsKey(validatePath(path))) {
            throw new NoNodeException(path);
        }
        return nodes.get(path);
    }
}
