package org.swagger.diff.model;

import java.util.ArrayList;
import java.util.List;

public class ModelChange {
    public String typeName;
    public final List<Change> changes = new ArrayList<>();
}
