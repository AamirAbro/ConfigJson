package com.aamirabro.configjson;

import org.gradle.api.DefaultTask
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction

import java.io.File;
import java.io.IOException;


import org.gradle.api.Plugin
import org.json.JSONObject

/**
 * Created by aamirabro on 25/06/2017.
 */
public class ConfigJsonTask extends DefaultTask {


    @OutputDirectory
    public File intermediateDir;

    public String classDirString;

    @Input
    public File jsonFile;

    @Input
    public String packageName;

    @TaskAction
    public void action() throws IOException {

        println "print2"

        if (jsonFile != null) {
            generateClass()
        } else {
            println "no config data found"
            throw new IllegalArgumentException(String.format("please add configjsonFile and configjsonPackage", it))
        }

    }

    private void generateClass() {

        jsonFile.withReader { inp ->
            def content = inp.readLines().join("\n")

            println content
        }

        def allFields = parseFieldsFromFile()


        def fieldStatements = []
        allFields.each {
            fieldStatements.add("\tpublic static final $it.fieldType $it.fieldName = $it.fieldValue;")
        }

        def fieldsStr = fieldStatements.join("\n")

        def classString = "package $packageName;\n \n" +
                "public final class ConfigJson {\n" +
                "$fieldsStr" +
                "\n" +
                "}"


        println classString

        println intermediateDir

        def dir = new File(classDirString)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        def classFile = new File(dir, "ConfigJson.java")
        classFile.createNewFile()

        classFile.withWriter {out -> out.write(classString)}

    }


    private List parseFieldsFromFile () {
        def allFields = []
        jsonFile.withReader { inp ->
            def content = inp.readLines().join("\n")

            println content

            JSONObject jsonObj = new JSONObject(content);
            jsonObj.keys().each {
                def fieldName = it.toUpperCase(Locale.US)
                Object fieldValue = jsonObj.get(it);
                def fieldType = ConfigJsonTask.getTypeString(fieldValue)
                fieldValue = ConfigJsonTask.escapeValue(fieldValue)

                allFields.add(new ConfigJsonPlugin.JsonEntry(fieldValue, fieldName, fieldType))
            }
        }
        return allFields
    }


    private static String getTypeString (Object fieldValue) {
        def fieldType
        if(fieldValue instanceof Integer){
            fieldType = 'int'
        } else if (fieldValue instanceof Float) {
            fieldType = 'float'
        } else if (fieldValue instanceof Double) {
            fieldType = 'double'
        } else if (fieldValue instanceof Boolean) {
            fieldType = 'boolean'
        } else if (fieldValue instanceof String) {
            fieldType = 'String'
        } else {
            throw new IllegalArgumentException(String.format("Field %s has unknown type", fieldValue))
        }

        return fieldType
    }


    private static String escapeValue (fieldValue) {
        if (fieldValue instanceof String) {
            fieldValue = "\"$fieldValue\"" // wrap it again in quotes
        }
        return fieldValue
    }


}
