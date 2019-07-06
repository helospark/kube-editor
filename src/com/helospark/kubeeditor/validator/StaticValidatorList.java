package com.helospark.kubeeditor.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class StaticValidatorList {
    public static final List<FieldValueValidator> validators = new ArrayList<>();

    static {
        validators.add(new BooleanValidator());
        validators.add(new IntegerValidator());
        validators.add(new PortValidator());
        validators.add(new PositiveValueValidator());
    }

    public static List<String> getValidationMessage(String type, ScalarNode node, List<String> path) {
        return validators.stream()
                .map(a -> {
                    try {
                        return a.getErrorsForValues(type, node, path);
                    } catch (Exception e) {
                        return Optional.<String>empty();
                    }
                })
                .filter(a -> a.isPresent())
                .map(a -> a.get())
                .collect(Collectors.toList());
    }
}
