package com.helospark.kubeeditor;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.converter.SwaggerConverter;

public class SchemaParser {
    private static OpenAPI api = null;

    public static OpenAPI getApi() {
        if (api == null) {
            api = doParse();
        }
        return api;
    }

    private static OpenAPI doParse() {
        String fileName = "swagger.json";
        String swaggerJson = ResourceFileReader.readFile(fileName);

        return new SwaggerConverter().readContents(swaggerJson, null, null).getOpenAPI();
    }

}
