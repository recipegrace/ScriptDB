package com.recipegrace.hadooprunner.template;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjacob on 4/13/15.
 */
public class StringTemplateTest {


    @Test
    public void testScriptText() throws IOException, HadoopRunnerException {

        String templateGroup1 = "template1(executable,arguments,main,options,scaldingTool) ::=<<#!/bin/bash\n" +
                "hadoop1 jar <executable> <scaldingTool><options:{u|-D<u.name>=<u.value> }><main> --hdfs <arguments:{u|--<u.name> <u.value> }> \n" +
                ">>\n";
        String templateGroup2 = "template2(executable,arguments,main,options,scaldingTool) ::=<<#!/bin/bash\n" +
                "hadoop2 jar <executable> <scaldingTool><options:{u|-D<u.name>=<u.value> }><main> --hdfs <arguments:{u|--<u.name> <u.value> }> \n" +
                ">>";
        StringTemplateStringWrapper wrapper1 = new StringTemplateStringWrapper("template1", templateGroup1 + templateGroup2);
        String content1 = wrapper1.render(new HashMap<String, Object>());
        String out1 = "#!/bin/bash\n" +
                "hadoop1 jar   --hdfs  ";
        assertEquals(content1, out1);

        StringTemplateStringWrapper wrapper2 = new StringTemplateStringWrapper("template2", templateGroup1 + templateGroup2);
        String content2 = wrapper2.render(new HashMap<String, Object>());
        String out2 = "#!/bin/bash\n" +
                "hadoop2 jar   --hdfs  ";
        assertEquals(content2, out2);


    }


}
