package com.helospark.kubeeditor.validator;

import java.util.List;
import java.util.Optional;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class PositiveValueValidator implements FieldValueValidator {

    @Override
    public Optional<String> getErrorsForValues(String type, ScalarNode node, List<String> path) {
        if (isContainerPort(path) && type.equals("integer")) {
            int value = Integer.parseInt(node.getValue());
            if (value <= 0) {
                return Optional.ofNullable("Should be positive");
            }
        }
        return Optional.empty();
    }

    private boolean isContainerPort(List<String> path) {
        if (path.size() > 0) {
            String lastElement = path.get(path.size() - 1);
            return lastElement.equalsIgnoreCase("replicas");
        } else {
            return false;
        }
    }

}
