package com.helospark.kubeeditor.validator;

import java.util.List;
import java.util.Optional;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class BooleanValidator implements FieldValueValidator {

    @Override
    public Optional<String> getErrorsForValues(String type, ScalarNode scalarNode, List<String> path) {
        if (type.equals("boolean")) {
            if (!(scalarNode.getValue().equals("true") || scalarNode.getValue().equals("false"))) {
                return Optional.of("Boolean value expected");
            }
        }
        return Optional.empty();
    }

}
