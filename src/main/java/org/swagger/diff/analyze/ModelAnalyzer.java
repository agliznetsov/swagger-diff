package org.swagger.diff.analyze;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import org.apache.commons.lang3.StringUtils;
import org.swagger.diff.model.Change;
import org.swagger.diff.model.ModelChange;

import static org.swagger.diff.model.ChangeType.*;

public class ModelAnalyzer {
    private String name;
    Model oldModel;
    Model newModel;
    ModelChange change;

    public ModelAnalyzer(String name, Model oldModel, Model newModel) {
        this.name = name;
        this.oldModel = oldModel;
        this.newModel = newModel;
    }

    public ModelChange run() {
        change = new ModelChange();
        change.typeName = name;

        CollectionAnalyzer<String, Property, Change> ca = new CollectionAnalyzer<>();
        ca.oldKeys = oldModel.getProperties();
        ca.newKeys = newModel.getProperties();
        ca.add = (key, value) -> new Change(ADD, false, key, null, getType(value));
        ca.remove = (key, value) -> new Change(REMOVE, true, key, getType(value), null);
        ca.update = (it) -> {
            String oldType = getType(oldModel.getProperties().get(it));
            String newType = getType(newModel.getProperties().get(it));
            if (!StringUtils.equals(oldType, newType)) {
                return new Change(UPDATE, false, it, oldType, newType);
            } else {
                return null;
            }
        };
        change.changes.addAll(ca.run());

        return change.changes.isEmpty() ? null : change;
    }

    private String getType(Property property) {
        if (property instanceof RefProperty) {
            return ((RefProperty) property).getSimpleRef();
        } else if (property instanceof ArrayProperty) {
            return getType(((ArrayProperty) property).getItems()) + "[]";
        } else {
            return property.getType();
        }
    }
}
