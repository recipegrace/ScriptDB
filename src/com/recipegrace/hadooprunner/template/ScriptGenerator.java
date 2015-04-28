package com.recipegrace.hadooprunner.template;

import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Job;
import com.recipegrace.hadooprunner.core.Project;
import com.recipegrace.hadooprunner.db.JobDAO;
import com.recipegrace.hadooprunner.db.ProjectDAO;
import com.recipegrace.hadooprunner.db.TemplateDAO;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fjacob on 4/27/15.
 */
public class ScriptGenerator {

    private String mainClass;
    private Project project;
    private Job job;
    private Cluster cluster;
    private String template;

    public ScriptGenerator(String mainClass, String projectName, Cluster cluster) throws FileNotFoundException {
        this.mainClass = mainClass;
        this.project = new ProjectDAO().getProject(projectName);
        this.job = new JobDAO().getJob(projectName, mainClass);
        this.template = new TemplateDAO().getTemplate(job.getTemplateName()).getTemplate();
        this.cluster = cluster;
    }

    public String generateScript() throws IOException, HadoopRunnerException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mainclass", mainClass);
        map.put("jarname", project.getJarName());
        map.put("projectlocation", project.getProjectLocation());
        map.put("projectname", project.getProjectName());
        addOptions(map);
        addArguments(map);
        StringTemplateStringWrapper wrapper1 = new StringTemplateStringWrapper("template1", template);
        String output = wrapper1.render(map);
        Path outputPath = Paths.get("template" + System.currentTimeMillis());

        Files.write(outputPath, output.getBytes());
        return outputPath.toAbsolutePath().toString();
    }

    private void addOptions(Map<String, Object> map) {
        Map<String, String> arguments = job.getVmArguments();
        List<Pair<String, String>> pairs = arguments.keySet().stream().map(f -> new Pair<String, String>(f, arguments.get(f))).collect(Collectors.toList());
        map.put("options", pairs);
    }

    private void addArguments(Map<String, Object> map) {
        Map<String, String> arguments = job.getProgramArguments();
        List<Pair<String, String>> pairs = arguments.keySet().stream().map(f -> new Pair<String, String>(f, arguments.get(f))).collect(Collectors.toList());
        map.put("arguments", pairs);
    }

}
