package com.recipegrace.hadooprunner.db;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Template;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjacob on 4/11/15.
 */
public class TemplateDAOTest extends AbstractDAOTest {


    @Test(expected = HadoopRunnerException.class)
    public void testSameCreateTemplate() throws IOException, HadoopRunnerException {
        TemplateDAO rgDAO = new TemplateDAO();
        createTemplate("templateName", "templateLocation");
        createTemplate("templateName", "templateLocation");
    }

    @Test
    public void testSaveTemplate() throws IOException, HadoopRunnerException {
        TemplateDAO rgDAO = new TemplateDAO();
        createTemplate("templateName", "templateLocation");
        Template result = rgDAO.getTemplate("templateName");
        assertEquals(result.getTemplate(), "templateLocation");

    }

    private void createTemplate(String templateName, String templateText) throws IOException, HadoopRunnerException {
        TemplateDAO rgDAO = new TemplateDAO();
        Template template = new Template();
        template.setTemplateName(templateName);
        template.setTemplate(templateText);
        rgDAO.createTemplate(template);
    }
}
