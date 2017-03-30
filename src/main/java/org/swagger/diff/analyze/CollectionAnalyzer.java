package org.swagger.diff.analyze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CollectionAnalyzer<K, V, C> {
    Map<K, V> oldKeys;
    Map<K, V> newKeys;
    BiFunction<K, V, C> add;
    BiFunction<K, V, C> remove;
    Function<K, C> update;

    List<C> run() {
        if (oldKeys == null) {
            oldKeys = new HashMap<K, V>();
        }
        if (newKeys == null) {
            newKeys = new HashMap<K, V>();
        }
        List<C> changes = new ArrayList<C>();

        if (add != null) {
            newKeys.entrySet().stream().filter(it -> !oldKeys.containsKey(it.getKey())).forEach(it -> changes.add(add.apply(it.getKey(), it.getValue())));
        }

        if (remove != null) {
            oldKeys.entrySet().stream().filter(it -> !newKeys.containsKey(it.getKey())).forEach(it -> changes.add(remove.apply(it.getKey(), it.getValue())));
        }

        if (update != null) {
            oldKeys.entrySet().stream().filter(it -> newKeys.containsKey(it.getKey())).forEach(it -> {
                C change = update.apply(it.getKey());
                if (change != null) {
                    changes.add(change);
                }
            });
        }
        return changes;
    }
}
