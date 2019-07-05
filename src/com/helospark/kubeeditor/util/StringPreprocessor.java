package com.helospark.kubeeditor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringPreprocessor {

    public static PreprocessedString preprocess(String contentToModify, int offsetInput) {
        Pattern pattern = Pattern.compile("\r\n");
        Matcher matcher = pattern.matcher(contentToModify);
        int extraCount = 0;
        while (matcher.find()) {
            if (matcher.start() < offsetInput) {
                ++extraCount;
            } else {
                break;
            }
        }

        int simpleOffset = offsetInput - extraCount;

        String content = contentToModify.replaceAll("\r\n", "\n")
                .replaceAll("\r", "\n");

        return new PreprocessedString(content, simpleOffset);
    }

}
