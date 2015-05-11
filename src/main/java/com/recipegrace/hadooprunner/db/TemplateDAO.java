package com.recipegrace.hadooprunner.db;

import com.google.gson.reflect.TypeToken;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Template;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fjacob on 4/11/15.
 */
public class TemplateDAO extends AbstractDAO<Template> {


    public void checkTemplateExists(String templateName) throws FileNotFoundException, HadoopRunnerException {
        if (getTemplate(templateName) != null) throw new HadoopRunnerException("Template already exists");
    }


    public void createTemplate(Template template) throws IOException, HadoopRunnerException {

        checkTemplateExists(template.getTemplateName());
        List<Template> templates = getAll();
        templates.add(template);
        saveAsJSON(templates);

    }

    public void saveTemplate(Template template) throws IOException, HadoopRunnerException {

        List<Template> templates = getAll().stream()
                .filter(f -> !f.getTemplateName().equals(template.getTemplateName()))
                .collect(Collectors.toList());
        templates.add(template);
        saveAsJSON(templates);

    }


    public Template getTemplate(String templateName) throws FileNotFoundException {

        Stream<Template> selectedTemplates = getAll().stream().filter(f -> f.getTemplateName().equals(templateName));
        return selectedTemplates.findFirst().orElse(null);
    }

    @Override
    protected String getFile() {
        return ".db/templates.json";
    }

    protected Type getType() {
        return new TypeToken<ArrayList<Template>>() {
        }.getType();
    }
}
