package com.aamirabro.configjson

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.json.JSONObject

import java.util.stream.Collectors

/**
 * Created by Aamir Abro on 25/06/2017.
 */
class ConfigJsonTask extends DefaultTask {

    File intermediateDir;
    String classDirString;
    List<String> jsonFileNames;
    String packageName;

    @TaskAction
    public void action() throws IOException {

        if (jsonFileNames != null && !jsonFileNames.isEmpty()) {
            generateClass()
        } else {
            project.logger.error 'ConfigJson: No config data found'
            throw new IllegalArgumentException('Please add configJsonFiles and configJsonPackage')
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

    @InputFiles
    def getJsonFiles () {
        return jsonFileNames.stream()
                .map({name ->  new File(project.projectDir, name)})
                .collect(Collectors.toList());
    }

    private void generateClass() {

        def allFields = parseFieldsFromFile()

        def fieldStatements = []
        allFields.values().each {
            fieldStatements.add("    public static final $it.fieldType $it.fieldName = $it.fieldValue;")
        }

        def fieldsStr = fieldStatements.join("\n")

        def classString = "package $packageName;\n \n" +
                "public final class ConfigJson {\n" +
                "$fieldsStr" +
                "\n" +
                "}"

        project.logger.info('ConfigJson: java file \n{}', classString)

        def classFile = getGeneratedClassFile()
        classFile.createNewFile()
        classFile.withWriter {out -> out.write(classString)}

    }


    def parseFieldsFromFile () {

        project.logger.lifecycle('ConfigJson: using files : {}', jsonFileNames)

        Map<String, JsonEntry> allFields = new HashMap<String, JsonEntry>()
        getJsonFiles().each {

            it.withReader { inp ->
                def content = inp.readLines().join("\n")

                JSONObject jsonObj = new JSONObject(content)
                jsonObj.keys().each {
                    def fieldName = it.toUpperCase(Locale.US)
                    Object fieldValue = jsonObj.get(it)
                    def fieldType = getTypeString(fieldValue)
                    fieldValue = escapeValue(fieldValue)

                    allFields.put(fieldName, new JsonEntry(fieldName, fieldValue, fieldType))
                }
            }
        }

        return allFields
    }


    static def getTypeString (fieldValue) {
        String fieldType
        if(fieldValue instanceof Integer){
            fieldType = 'int'
        } else if (fieldValue instanceof Long) {
            fieldType = 'long'
        } else if (fieldValue instanceof Float) {
            fieldType = 'float'
        } else if (fieldValue instanceof Double) {
            fieldType = 'double'
        } else if (fieldValue instanceof Boolean) {
            fieldType = 'boolean'
        } else if (fieldValue instanceof String) {
            fieldType = 'String'
        } else {
            throw new IllegalArgumentException(String.format("Field %s has unknown type %s", fieldValue, fieldValue.class))
        }

        return fieldType
    }


    static def escapeValue (fieldValue) {
        if (fieldValue instanceof String) {
            fieldValue = "\"$fieldValue\"" // wrap it again in quotes
        } else if (fieldValue instanceof Long) {
            fieldValue = fieldValue+"L"
        }
        return fieldValue
    }

    static class JsonEntry {
        Object fieldValue;
        String fieldName;
        String fieldType;

        JsonEntry(String fieldName, Object fieldValue, String fieldType) {
            this.fieldName = fieldName
            this.fieldValue = fieldValue
            this.fieldType = fieldType
        }
    }


}
