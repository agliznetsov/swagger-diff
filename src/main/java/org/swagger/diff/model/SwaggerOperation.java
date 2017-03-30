package org.swagger.diff.model;

import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerOperation {
    public HttpMethod method;
    public String path;
    public String tag;
//    public Map<String, Parameter> pathParameters = new HashMap<>();
    public Map<String, Parameter> parameters = new HashMap<>();
    public Operation operation;
}
