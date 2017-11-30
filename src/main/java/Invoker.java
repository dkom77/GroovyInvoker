import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Invoker {
    public enum Registered {
        SCRIPT_ALIAS("test_script.groovy");
        private final static String SCRIPT_ROOT = "/scripts/";
        private String scriptUrl;

        Registered(String name){
            scriptUrl = SCRIPT_ROOT + name;
        }

    }

    private Registered script;
    private Map<String, Object> params;

    private Invoker(Registered script){
        params = new HashMap<>();
        this.script = script;
    }

    public static Invoker getInstance(Registered script){
        return new Invoker(script);
    }

    private final static String OUT_PARAM_NAME = "output";

    public Invoker addParam(String name, Object value){
        params.put(name, value);
        return this;
    }

    public Object invoke(){
        try{
            String script = getScript();
            Binding binding = prepareBinding();
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(script);
            return  binding.getVariable(OUT_PARAM_NAME);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Binding prepareBinding(){
        Binding binding = new Binding();
        //iterate map and populate Binding with its entries
        params.forEach(binding::setVariable);

        binding.setVariable(OUT_PARAM_NAME, "");

        return binding;
    }

    private String getScript() throws IOException {
        //get script as external file
        //File file = new File(script.scriptUrl);
        //ResourceExtractor extractor = ResourceExtractor.getInstance();
        //extractor.setFileSource(file);

        //get as resource
        ResourceExtractor extractor = ResourceExtractor.getInstance();
        extractor.setResourceName(script.scriptUrl);
        return extractor.getContent();
    }

}
