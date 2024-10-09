package com.helospark.kubeeditor.valueprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaticValueProviderList {
    public static List<ValidValueProviders> validValueProviders = new ArrayList<>();

    static {
        validValueProviders.add(new ApiVersionProvider());
        validValueProviders.add(new KindProvider());
        validValueProviders.add(new SimpleMultiValueValidValueProvider(Arrays.asList("spec", "ports", "protocol"), Arrays.asList("TCP", "UDP", "SCTP")));
        validValueProviders.add(new SimpleMultiValueValidValueProvider(Arrays.asList("spec", "template", "spec", "containers", "ports", "protocol"), Arrays.asList("TCP", "UDP", "SCTP")));
        validValueProviders.add(new SimpleMultiValueValidValueProvider(Arrays.asList("spec", "type"), Arrays.asList("ClusterIP", "NodePort", "LoadBalancer", "ExternalName")));
        validValueProviders.add(new SimpleMultiValueValidValueProvider(Arrays.asList("spec", "template", "spec", "containers", "imagePullPolicy"), Arrays.asList("Always", "Never", "IfNotPresent")));
    }

    public static List<String> validValues(List<String> path, Optional<String> current) {
        return validValueProviders.stream()
                .filter(provider -> isMatch(provider.path(), path))
                .flatMap(a -> a.getValidValues().stream())
                .filter(a -> a.toLowerCase().startsWith(current.orElse("").toLowerCase()))
                .collect(Collectors.toList());
    }

    public static boolean isMatch(List<String> path, List<String> path2) {
        if (path.size() != path2.size()) {
            return false;
        }

        for (int i = 0; i < path.size(); ++i) {
            if (!path.get(i).equalsIgnoreCase(path2.get(i))) {
                return false;
            }
        }
        return true;
    }
}
