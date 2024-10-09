package com.helospark.kubeeditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.helospark.kubeeditor.schema.SchemaList;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;

public class YamlTools {

    public static List<String> getPath(String content, int offset) {
        int i = offset;

        List<String> result = new ArrayList<>();

        int minimumWhitespaces = Integer.MAX_VALUE;

        while (i >= 0) {
            int newIndex = content.lastIndexOf("\n", i);

            String line = content.substring(newIndex + 1, i + 1);

            if (line.trim().startsWith("-")) {
                line = line.replaceFirst("-", " ");
            }

            Pattern pattern = Pattern.compile("(\\s*)([A-Za-z]+)\\s*:\\s*(.*)");
            Matcher matcher = pattern.matcher(line);

            if (isLineDocumentSeparator(line)) {
                break;
            }
            if (matcher.matches()) {
                int numberOfWhitespaces = matcher.group(1).length();
                String pathElement = matcher.group(2);

                if (numberOfWhitespaces < minimumWhitespaces) {
                    result.add(pathElement);
                    minimumWhitespaces = numberOfWhitespaces;
                }
            } else if (minimumWhitespaces == Integer.MAX_VALUE) {
                int whitespacesInNewLine = 0;
                for (int j = 0; j < line.length(); ++j) {
                    if (!isWhitespace(line, j)) {
                        break;
                    }
                    ++whitespacesInNewLine;
                }
                minimumWhitespaces = whitespacesInNewLine;
            }

            i = newIndex - 1;
        }

        Collections.reverse(result);

        return result;
    }

    private static boolean isWhitespace(String content, int j) {
        return content.charAt(j) == '\t' || content.charAt(j) == ' ';
    }

    public static Optional<String> findKind(String content, int offset) {
        return findValueOfGlobalElement(content, offset, "kind");
    }

    public static Optional<String> findApiVersion(String content, int offset) {
        return findValueOfGlobalElement(content, offset, "apiVersion");
    }

    public static Optional<String> findValueOfGlobalElement(String content, int offset, String string) {
        int i = offset;

        while (i >= 0) {
            int newIndex = content.lastIndexOf("\n", i);
            String line = content.substring(newIndex + 1, i + 1);
            if (isLineDocumentSeparator(line)) {
                break;
            }

            if (newIndex == -1) {
                i = -1;
                break;
            }
            i = newIndex - 1;
        }

        while (i < content.length()) {
            int newIndex = content.indexOf("\n", i + 1);
            if (newIndex == -1) {
                break;
            }
            String line = content.substring(i + 1, newIndex);

            if (isLineDocumentSeparator(line)) {
                break;
            }
            if (line.startsWith(string)) {
                Pattern pattern = Pattern.compile(string + "\\s*:\\s*(.*)");
                Matcher matcher = pattern.matcher(line);

                if (matcher.matches()) {
                    return Optional.ofNullable(matcher.group(1));
                }
            }
            i = newIndex;
        }
        return Optional.empty();
    }

    private static boolean isLineDocumentSeparator(String line) {
        return line.equals("---") || line.equals("...");
    }

    public static boolean isEmptyLine(String content, int offset) {
        int prevIndex = content.lastIndexOf("\n", offset);
        int nextIndex = content.indexOf("\n", offset);

        if (nextIndex == -1) {
            nextIndex = content.length();
        }
        if (prevIndex == -1) {
            prevIndex = 0;
        }
        if (content.length() == 0) {
            return true;
        }

        String line = content.substring(prevIndex, nextIndex);

        for (int i = 0; i < line.length(); ++i) {
            if (!isWhitespace(content, i)) {
                return false;
            }
        }
        return true;
    }

    //    public static int calculateWhitespacesAtLine(String content, int offset) {
    //        int i = offset;
    //
    //        while (i >= 0) {
    //            char currentChar = content.charAt(i);
    //
    //            if (currentChar == '\n' || currentChar == '\r') {
    //                break;
    //            }
    //
    //            --i;
    //        }
    //        ++i;
    //        int result = 0;
    //        while (i < content.length()) {
    //            if (!isWhitespace(content.charAt(i))) {
    //                return result;
    //            }
    //            ++result;
    //            ++i;
    //        }
    //        return result;
    //    }

    //    public static boolean isWhitespace(char charAt) {
    //        return charAt == '\t' || charAt == ' ';
    //    }

    public static boolean isComment(String content, int offset) {
        int i = offset;

        while (i >= 0) {
            char currentChar = content.charAt(i);

            if (currentChar == '\n' || currentChar == '\r') {
                return false;
            }
            if (currentChar == '#') {
                return true;
            }
            --i;

        }
        return false;
    }

    public static boolean isAfterColon(String content, int offset) {
        int i = offset;

        while (i >= 0) {
            char currentChar = content.charAt(i);

            if (currentChar == '\n' || currentChar == '\r') {
                return false;
            }
            if (currentChar == ':') {
                return true;
            }

            --i;
        }

        return false;
    }

    public static Optional<String> currentKey(String content, int offset) {
        int newIndex = content.lastIndexOf("\n", offset);

        String line = content.substring(newIndex + 1, offset + 1);

        if (line.trim().startsWith("-")) {
            line = line.replaceFirst("-", " ");
        }

        int colonIndex = line.indexOf(":");

        String keyValue;
        if (colonIndex == -1) {
            keyValue = line.trim();
        } else {
            keyValue = line.substring(0, colonIndex);
        }

        if (keyValue.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(keyValue.trim());
        }
    }

    public static Optional<String> currentValue(String content, int offset) {
        int newIndex = content.lastIndexOf("\n", offset);
        String line = content.substring(newIndex + 1, offset + 1);

        int colonIndex = line.indexOf(":");

        if (colonIndex == -1 || colonIndex >= line.length() - 1) {
            return Optional.empty();
        } else {
            return Optional.of(line.substring(colonIndex + 1).trim());
        }
    }

    public static Optional<Schema> findSchemaForPath(List<String> path, String content, int offset) {
        Optional<String> apiVersion = YamlTools.findApiVersion(content, offset);
        Optional<String> kind = YamlTools.findKind(content, offset);
        return findSchemaForPath(path, apiVersion, kind);
    }

    public static Optional<Schema> findSchemaForPath(List<String> path, Optional<String> apiVersion, Optional<String> kind) {
        Optional<Schema> foundSchema = Optional.empty();
        OpenAPI api = SchemaParser.getApi();
        if (apiVersion.isPresent() && kind.isPresent()) {
            Optional<String> descriptor = getSchemaDescriptor(apiVersion.get(), kind.get());

            if (descriptor.isPresent()) {
                Schema schema = api.getComponents().getSchemas().get(descriptor.get());

                for (int i = 0; i < path.size(); ++i) {
                    String pathElement = path.get(i);

                    Map properties = schema.getProperties();
                    if (properties == null) {
                        return Optional.empty();
                    }
                    Object newElement = properties.get(pathElement);

                    if (newElement instanceof ArraySchema) {
                        newElement = ((ArraySchema) (newElement)).getItems();
                    }

                    if (newElement instanceof Schema && ((Schema) (newElement)).get$ref() != null) {
                        schema = api.getComponents().getSchemas().get(((Schema) (newElement)).get$ref().replaceFirst("#/components/schemas/", ""));
                    } else if (newElement instanceof MapSchema) {
                        schema = (Schema) newElement;
                    } else {
                        schema = (Schema) newElement;
                        break;
                    }
                }
                foundSchema = Optional.ofNullable(schema);
            }
        }
        return foundSchema;
    }

    public static Optional<String> getSchemaDescriptor(final String apiVersion, final String kind) {
        return SchemaList.triplets.stream()
                .filter(a -> a.apiVersion.equalsIgnoreCase(apiVersion) && a.kind.equalsIgnoreCase(kind))
                .map(a -> a.descriptor)
                .findFirst();
    }

    public static String getCurrentLine(String content, int offset) {
        if (content.length() == 0) {
            return "";
        }
        int prevIndex = content.lastIndexOf("\n", offset);
        int nextIndex = content.indexOf("\n", offset);

        if (nextIndex == -1) {
            nextIndex = content.length();
        }

        return content.substring(prevIndex, nextIndex);
    }

    public static Optional<Integer> currentSpaces(String content, int offset) {
        int prevIndex = content.lastIndexOf("\n", offset);

        if (prevIndex + 1 > offset) {
            return Optional.of(0);
        }

        String line = content.substring(prevIndex + 1, offset);

        for (int i = 0; i < line.length(); ++i) {
            if (!isWhitespace(line, i)) {
                return Optional.of(i);
            }
        }

        return Optional.of(line.length() + 1);
    }

    public static boolean isNewDocumentBeforePosition(String content, int offset) {
        int lastNewLineLocation = content.lastIndexOf("\n", offset);
        if (lastNewLineLocation == -1) {
            return true;
        }
        int lastLineStartPosition = content.lastIndexOf('\n', lastNewLineLocation - 1);
        if (lastLineStartPosition == -1) {
            return true;
        }
        String lastLine = content.substring(lastLineStartPosition, lastNewLineLocation);
        return lastLine.startsWith("---") || lastLine.startsWith("...");
    }

    public static boolean isNewDocumentAfterPosition(String content, int offset) {
        int nextNewLineLocation = content.indexOf("\n", offset);
        if (nextNewLineLocation == -1) {
            return true;
        }
        int lastLineStartPosition = content.indexOf('\n', nextNewLineLocation + 1);
        if (lastLineStartPosition == -1) {
            return true;
        }
        String lastLine = content.substring(nextNewLineLocation, lastLineStartPosition);
        return lastLine.startsWith("---") || lastLine.startsWith("...");
    }

}
