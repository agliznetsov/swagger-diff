package org.swagger.diff.analyze;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.Response;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.RefProperty;
import org.apache.commons.lang3.StringUtils;
import org.swagger.diff.model.Change;
import org.swagger.diff.model.OperationChange;
import org.swagger.diff.model.SwaggerOperation;

import static org.swagger.diff.model.ChangeType.*;

public class OperationAnalyzer {
    SwaggerOperation oldOperation;
    SwaggerOperation newOperation;
    OperationChange change;

    public OperationAnalyzer(SwaggerOperation oldOperation, SwaggerOperation newOperation) {
        this.oldOperation = oldOperation;
        this.newOperation = newOperation;
    }

    public OperationChange run() {
        change = new OperationChange();
        change.type = UPDATE;
        change.operationId = newOperation.operation.getOperationId();
        checkMethod();
        checkPath();
        checkTag();
        checkParameters();
        checkResponses();
        return change.changes.isEmpty() ? null : change;
    }

    private void checkMethod() {
        change.method = newOperation.method;
        if (oldOperation.method != newOperation.method) {
            change.changes.add(new Change(UPDATE, true, "method", oldOperation.method.toString(), newOperation.method.toString()));
        }
    }

    private void checkPath() {
        change.path = newOperation.path;
        if (!StringUtils.equals(oldOperation.path, newOperation.path)) {
            change.changes.add(new Change(UPDATE, true, "path", oldOperation.path, newOperation.path));
        }
    }

    private void checkTag() {
        change.tag = newOperation.tag;
        if (!StringUtils.equals(oldOperation.path, newOperation.path)) {
            change.changes.add(new Change(UPDATE, false, "tag", oldOperation.tag, newOperation.tag));
        }
    }

    private void checkParameters() {
        CollectionAnalyzer<String, Parameter, Change> ca = new CollectionAnalyzer<>();
        ca.oldKeys = oldOperation.parameters;
        ca.newKeys = newOperation.parameters;
        ca.add = (key, value) -> new Change(ADD, value.getRequired(), "parameter." + key, null, getParameterType(value));
        ca.remove = (key, value) -> new Change(REMOVE, value.getRequired(), "parameter." + key, getParameterType(value), null);
        ca.update = (it) -> {
            String oldType = getParameterType(oldOperation.parameters.get(it));
            String newType = getParameterType(newOperation.parameters.get(it));
            if (!StringUtils.equals(oldType, newType)) {
                return new Change(UPDATE, true, "parameter." + it, oldType, newType);
            } else {
                return null;
            }
        };
        change.changes.addAll(ca.run());
    }

    private void checkResponses() {
        CollectionAnalyzer<String, Response, Change> ca = new CollectionAnalyzer<>();
        ca.oldKeys = oldOperation.operation.getResponses();
        ca.newKeys = newOperation.operation.getResponses();
        ca.add = (key, value) -> new Change(ADD, false, "response." + key, null, null);
        ca.remove = (key, value) -> new Change(REMOVE, false, "response." + key, null, null);
        ca.update = (it) -> {
            String oldType = getResponseType(oldOperation, it);
            String newType = getResponseType(newOperation, it);
            if (!StringUtils.equals(oldType, newType)) {
                return new Change(UPDATE, false, "response." + it, oldType, newType);
            } else {
                return null;
            }
        };
        change.changes.addAll(ca.run());
    }

    private String getParameterType(Parameter parameter) {
        if (parameter instanceof SerializableParameter) {
            return ((SerializableParameter) parameter).getType();
        } else if (parameter instanceof BodyParameter) {
            Model schema = ((BodyParameter)parameter).getSchema();
            if (schema instanceof ArrayModel) {
                RefProperty ref = (RefProperty) ((ArrayModel) schema).getItems();
                String name = ref.getSimpleRef();
                return name + "[]";
            } else {
                String name = schema.getReference();
                return name.substring(name.lastIndexOf('/') + 1);
            }
        } else {
            throw new IllegalStateException("parameter not supported: " + parameter);
        }
    }

    private String getResponseType(SwaggerOperation operation, String key) {
        return operation.operation.getResponses().get(key).getSchema().getName();
    }
}
