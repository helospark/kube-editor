package com.helospark.kubeeditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ResourceFileReader {

    public static String readFile(String fileName) {
        String swaggerJson = "";
        try {
            Bundle bundle = FrameworkUtil.getBundle(SchemaParser.class);

            if (bundle != null) {
                URL configURL = bundle.getBundleContext().getBundle().getEntry("Resources/" + fileName);
                BufferedReader in = new BufferedReader(new InputStreamReader(configURL.openStream()));

                StringBuilder sb = new StringBuilder();

                int size;
                char[] array = new char[10000];
                while ((size = in.read(array)) > 0) {
                    sb.append(array, 0, size);
                }
                in.close();
                swaggerJson = sb.toString();
            } else {
                swaggerJson = readClasspathFile(fileName);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return swaggerJson;
    }

    private static String readClasspathFile(String fileName) throws IOException, URISyntaxException {
        Path uri = Paths.get(SchemaParser.class.getResource("/" + fileName).toURI());
        return new String(Files.readAllBytes(uri), Charset.forName("UTF-8"));
    }
}
