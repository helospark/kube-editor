package com.helospark.kubeeditor;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class YamlToolsTest {

    @Test
    public void testGetPathWithEndOfDocument() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        run: my-nginx";

        List<String> path = YamlTools.getPath(testData, testData.length() - 1);

        System.out.println(path);

        assertEquals(Arrays.asList("spec", "template", "metadata", "labels", "run"), path);
    }

    @Test
    public void testGetPathInMiddleOfDocument() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        run: my-nginx";

        List<String> path = YamlTools.getPath(testData, testData.indexOf("template:") - 1);

        System.out.println(path);

        assertEquals(Arrays.asList("spec"), path);
    }

    @Test
    public void testGetPathFirstPlace() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        run: my-nginx";

        List<String> path = YamlTools.getPath(testData, 0);

        System.out.println(path);

        assertEquals(Arrays.asList(), path);
    }

    @Test
    public void testGetPathMiddleOfFirstLine() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        run: my-nginx";

        List<String> path = YamlTools.getPath(testData, testData.indexOf("apps/v1"));

        System.out.println(path);

        assertEquals(Arrays.asList("apiVersion"), path);
    }

    @Test
    public void testGetPathAfterLastLine() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        run: my-nginx\n" +
                "        ";

        List<String> path = YamlTools.getPath(testData, testData.length() - 1);

        assertEquals(Arrays.asList("spec", "template", "metadata", "labels"), path);
    }

    @Test
    public void testFindApiVersion() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        run: my-nginx\n" +
                "        ";

        Optional<String> value = YamlTools.findApiVersion(testData, testData.length() - 1);

        assertEquals("apps/v1", value.get());
    }

    @Test
    public void testFindKind() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        run: my-nginx\n" +
                "        ";

        Optional<String> value = YamlTools.findKind(testData, testData.length() - 1);

        assertEquals("Deployment", value.get());
    }
}
