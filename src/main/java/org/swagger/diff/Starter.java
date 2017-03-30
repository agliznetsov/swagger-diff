package org.swagger.diff;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.io.FileUtils;
import org.swagger.diff.analyze.SwaggerAnalyzer;
import org.swagger.diff.model.OutputFormat;
import org.swagger.diff.model.Report;

import java.io.File;
import java.io.IOException;

public class Starter {
    public static void main(String... args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: <old> <new> <output");
            System.exit(1);
        }

//        try {
            Starter starter = new Starter(args[0], args[1], args[2]);
            starter.run();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            System.exit(1);
//        }
    }

    String oldPath;
    String newPath;
    String outputPath;
    OutputFormat outputFormat;
    ObjectMapper objectMapper = new ObjectMapper();

    Starter(String oldPath, String newPath, String outputPath) {
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.outputPath = outputPath;
        this.outputFormat = outputPath.toLowerCase().endsWith(".html") ? OutputFormat.html : OutputFormat.json;
    }

    void run() throws IOException {
        Swagger oldSwagger = readSwagger(this.oldPath);
        Swagger newSwagger = readSwagger(this.newPath);
        Report report = new SwaggerAnalyzer(oldSwagger, newSwagger).run();
        writeReport(report);
    }

    private void writeReport(Report report) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputPath), report);
    }

    private Swagger readSwagger(String path) throws IOException {
        String json = FileUtils.readFileToString(new File(path));
        return new SwaggerParser().parse(json);
    }
}
