package com.helospark.kubeeditor.valueprovider;

import java.util.List;

public class SimpleMultiValueValidValueProvider implements ValidValueProviders {
    private List<String> path;
    private List<String> possibilities;

    public SimpleMultiValueValidValueProvider(List<String> path, List<String> possibilities) {
        this.path = path;
        this.possibilities = possibilities;
    }

    @Override
    public List<String> getValidValues() {
        return possibilities;
    }

    @Override
    public List<String> path() {
        return path;
    }

}
