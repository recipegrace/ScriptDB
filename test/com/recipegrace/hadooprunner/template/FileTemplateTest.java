package com.recipegrace.hadooprunner.template;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import javafx.util.Pair;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fjacob on 4/28/15.
 */
public class FileTemplateTest {
@Test
public void testGenerateScript() throws IOException, HadoopRunnerException {

    List<String> lines =Files.readAllLines(Paths.get( System.getProperty("user.dir"),"templates","remoteScript.stg" ));
    StringBuffer buffer = new StringBuffer();
    for(String line: lines){
        buffer.append(line+"\n");

    }

    StringTemplateStringWrapper wrapper1 = new StringTemplateStringWrapper("template1", buffer.toString());

    Pair<String,String> pair1 = new Pair("key","value");
    Pair<String,String> pair2 = new Pair("key1","value2");

    List<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
    pairs.add(pair1);
    pairs.add(pair2);

    Map<String,Object> map = new HashMap<String,Object>();
    map.put("options", pairs);
    String result=wrapper1.render(map);
    System.out.println(result);
}
}
