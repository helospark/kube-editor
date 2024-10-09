package com.helospark.kubeeditor.valueprovider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.helospark.kubeeditor.schema.SchemaList;

public class KindProvider implements ValidValueProviders {

    @Override
    public List<String> getValidValues() {
        return SchemaList.triplets
                .stream()
                .map(a -> a.kind)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> path() {
        return Arrays.asList("kind");
    }

}
