package org.swagger.diff.model;

import io.swagger.models.HttpMethod;

import java.util.ArrayList;
import java.util.List;

public class OperationChange {
    public ChangeType type;
    public String operationId;
    public HttpMethod method;
    public String path;
    public String tag;
    public boolean breaking;
    public final List<Change> changes = new ArrayList<>();
}
