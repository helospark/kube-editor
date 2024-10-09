package com.helospark.kubeeditor.validator;

import java.util.List;
import java.util.Optional;

import org.yaml.snakeyaml.nodes.ScalarNode;

public interface FieldValueValidator {

    public Optional<String> getErrorsForValues(String type, ScalarNode node, List<String> path);

}
