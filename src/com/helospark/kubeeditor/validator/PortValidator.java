package com.helospark.kubeeditor.validator;

import java.util.List;
import java.util.Optional;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class PortValidator implements FieldValueValidator {

    @Override
    public Optional<String> getErrorsForValues(String type, ScalarNode node, List<String> path) {
        if (isContainerPort(path) && type.equals("integer")) {
            int port = Integer.parseInt(node.getValue());
            if (port <= 0 || port >= 65536) {
                return Optional.ofNullable("Port should be between 0 and 65536");
            }
        }
        return Optional.empty();
    }

    private boolean isContainerPort(List<String> path) {
        if (path.size() > 0) {
            String lastElement = path.get(path.size() - 1);
            return lastElement.equalsIgnoreCase("containerPort") || lastElement.equalsIgnoreCase("hostPort")
                    || lastElement.equalsIgnoreCase("targetPort");
        } else {
            return false;
        }
    }

}
