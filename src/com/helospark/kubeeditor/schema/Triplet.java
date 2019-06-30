package com.helospark.kubeeditor.schema;

public class Triplet {
    public String apiVersion;
    public String kind;
    public String descriptor;

    public Triplet(String apiVersion, String kind, String descriptor) {
        this.apiVersion = apiVersion;
        this.kind = kind;
        this.descriptor = descriptor;
    }
}
