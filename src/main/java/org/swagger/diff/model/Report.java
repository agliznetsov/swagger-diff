package org.swagger.diff.model;

import io.swagger.models.Info;

import java.util.ArrayList;
import java.util.List;

public class Report {
    public Info oldInfo;
    public Info newInfo;
    public final List<OperationChange> operationChanges = new ArrayList<>();
    public final List<ModelChange> modelChanges = new ArrayList<>();
}
