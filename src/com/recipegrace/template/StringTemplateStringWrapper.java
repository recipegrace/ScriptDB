package com.recipegrace.template;

import com.recipegrace.core.HadoopRunnerException;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.io.IOException;
import java.util.Map;

/**
 * Created by fjacob on 4/13/15.
 */
public class StringTemplateStringWrapper {

    private String templateInstance;
    private String template;

    public StringTemplateStringWrapper(String templateInstance, String template) {
        this.templateInstance = templateInstance;
        this.template = template;
    }

    public String render(Map<String, Object> properties) throws IOException, HadoopRunnerException {

        STGroup group = new STGroupString(template);
        ST st = group.getInstanceOf(templateInstance);
        for (String key : properties.keySet()) {
            st.add(key, properties.get(key));
        }
       if(st==null) throw new HadoopRunnerException("Templates missing");
        return st.render();

    }
}
