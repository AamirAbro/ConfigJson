package com.aamirabro.configjson

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.json.JSONObject

/**
 * Created by Aamir Abro on 25/06/2017.
 */
class ConfigJsonTask extends DefaultTask {


    public File intermediateDir;

    public String classDirString;

    public File jsonFile;

    public String packageName;



    @TaskAction
    public void action() throws IOException {

        if (jsonFile != null) {
            generateClass()
        } else {
            println "no config data found"
            throw new IllegalArgumentException(String.format("please add configjsonFile and configjsonPackage", it))
        }

    }

    @OutputFile
    def getGeneratedClassFile () {
        def dir = new File(classDirString)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        return new File(dir, "ConfigJson.java")
    }

    @InputFile
    def getJsonFile () {
        return jsonFile;
    }

    private void generateClass() {

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


        def classFile = getGeneratedClassFile()
        classFile.createNewFile()
        classFile.withWriter {out -> out.write(classString)}

    }


    private List parseFieldsFromFile () {
        def allFields = []
        jsonFile.withReader { inp ->
            def content = inp.readLines().join("\n")

            JSONObject jsonObj = new JSONObject(content);
            jsonObj.keys().each {
                def fieldName = it.toUpperCase(Locale.US)
                Object fieldValue = jsonObj.get(it);
                def fieldType = getTypeString(fieldValue)
                fieldValue = escapeValue(fieldValue)

                allFields.add(new ConfigJsonPlugin.JsonEntry(fieldValue, fieldName, fieldType))
            }
        }
        return allFields
    }


    static def getTypeString (fieldValue) {
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


    static def escapeValue (fieldValue) {
        if (fieldValue instanceof String) {
            fieldValue = "\"$fieldValue\"" // wrap it again in quotes
        }
        return fieldValue
    }


}
