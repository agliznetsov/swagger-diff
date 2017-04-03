package org.swagger.diff.html;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import org.apache.commons.lang3.StringUtils;
import org.swagger.diff.model.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class HtmlGenerator {
    Report report;
    Map<String, Object> map = new HashMap<>();

    public HtmlGenerator(Report report) {
        this.report = report;
    }

    public void write(OutputStream stream) throws IOException {
        addTitle();
        addBody();
        doWrite(stream);
    }

    private void addBody() {
        report.modelChanges.sort(Comparator.comparing(it -> it.typeName));
        map.put("modelChanges", report.modelChanges);

        Map<String, Map> tags = new HashMap<>();
        report.operationChanges.forEach(it -> {
            Map tag = tags.get(it.tag);
            if (tag == null) {
                tag = new HashMap();
                tag.put("tag", it.tag);
                tag.put("operations", new ArrayList<>());
                tags.put(it.tag, tag);
            }
            ((List) tag.get("operations")).add(it);
        });
        List tagsList = tags.values().stream().sorted(Comparator.comparing(it -> it.get("tag").toString())).collect(Collectors.toList());
        map.put("tags", tagsList);
    }

    private void addTitle() {
        map.put("oldInfo", report.oldInfo);
        map.put("newInfo", report.newInfo);
        if (StringUtils.equals(report.oldInfo.getTitle(), report.newInfo.getTitle())) {
            map.put("title", report.oldInfo.getTitle());
        } else {
            map.put("title", report.oldInfo.getTitle() + " vs " + report.newInfo.getTitle());
        }
    }

    private void doWrite(OutputStream stream) throws IOException {
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        mf.setObjectHandler(new ReflectionObjectHandler() {
            @Override
            protected boolean areMethodsAccessible(Map<?, ?> map) {
                return true;
            }
        });
        Mustache mustache = mf.compile("report.html");
        try (Writer writer = new OutputStreamWriter(stream)) {
            mustache.execute(writer, map).flush();
        }
    }
}
