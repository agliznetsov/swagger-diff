package org.swagger.diff.analyze;

import io.swagger.models.Model;
import io.swagger.models.Swagger;
import org.apache.commons.lang3.StringUtils;
import org.swagger.diff.model.*;

import java.util.HashMap;
import java.util.Map;

import static org.swagger.diff.model.ChangeType.ADD;
import static org.swagger.diff.model.ChangeType.REMOVE;

public class SwaggerAnalyzer {
    Swagger oldSwagger;
    Swagger newSwagger;
    Report report;

    public SwaggerAnalyzer(Swagger oldSwagger, Swagger newSwagger) {
        this.oldSwagger = oldSwagger;
        this.newSwagger = newSwagger;
    }

    public Report run() {
        report = new Report();
        report.oldInfo = oldSwagger.getInfo();
        report.newInfo = newSwagger.getInfo();

        compareOperations();
        compareDefinitions();

        return report;
    }

    private void compareOperations() {
        Map<String, SwaggerOperation> oldOperations = findOperations(oldSwagger);
        Map<String, SwaggerOperation> newOperations = findOperations(newSwagger);

        CollectionAnalyzer<String, SwaggerOperation, OperationChange> ca = new CollectionAnalyzer<>();
        ca.oldKeys = oldOperations;
        ca.newKeys = newOperations;
        ca.add = (key, value) -> missingOp(value, ADD);
        ca.remove = (key, value) -> missingOp(value, REMOVE);
        ca.update = (key) -> new OperationAnalyzer(oldOperations.get(key), newOperations.get(key)).run();
        report.operationChanges.addAll(ca.run());
    }

    private void compareDefinitions() {
        CollectionAnalyzer<String, Model, ModelChange> ca = new CollectionAnalyzer<>();
        ca.oldKeys = oldSwagger.getDefinitions();
        ca.newKeys = newSwagger.getDefinitions();
        ca.update = (key) -> new ModelAnalyzer(key, ca.oldKeys.get(key), ca.newKeys.get(key)).run();
        report.modelChanges.addAll(ca.run());
    }

    private OperationChange missingOp(SwaggerOperation op, ChangeType changeType) {
        OperationChange change = new OperationChange();
        change.type = changeType;
        change.breaking = changeType == REMOVE;
        change.operationId = op.operation.getOperationId();
        change.path = op.path;
        change.tag = op.tag;
        return change;
    }

    private Map<String, SwaggerOperation> findOperations(Swagger oldSwagger) {
        Map<String, SwaggerOperation> map = new HashMap<>();
        oldSwagger.getPaths().entrySet().forEach(e -> {
            e.getValue().getOperationMap().entrySet().forEach(ee -> {
                SwaggerOperation swaggerOperation = new SwaggerOperation();
                swaggerOperation.path = e.getKey();
                if (ee.getValue().getParameters() != null) {
                    ee.getValue().getParameters().forEach(p -> swaggerOperation.parameters.put(p.getName(), p));
                }
                swaggerOperation.tag = String.join(",", ee.getValue().getTags());
                swaggerOperation.method = ee.getKey();
                swaggerOperation.operation = ee.getValue();
                map.put(ee.getValue().getOperationId(), swaggerOperation);
            });
        });
        return map;
    }
}
