package com.aamirabro.configjson


import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.json.JSONObject

class ConfigJsonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def hasApp = project.plugins.hasPlugin AppPlugin
        def hasLib = project.plugins.hasPlugin LibraryPlugin
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        def variants
        if (hasApp) {
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }

        variants.all { variant ->
            addGenerateConfigJsonTask(project, variant)
        }

    }

    private static void addGenerateConfigJsonTask(Project project, def variant) {

        def outputDir = new File(getBasePathForClass(project, variant))

        ConfigJsonTask task = project.tasks.create("generate${variant.name}ConfigJson", ConfigJsonTask )

        task.group = "configJsonPlugin"
        task.description = "generates Config Json Java file"

        task.intermediateDir = outputDir
        task.classDirString = getBasePathForClass(project, variant) + "/" + getPackageName(project).replace(".", "/")
        task.jsonFile = new File(project.projectDir, getJsonFileName(project))
        task.packageName = getPackageName(project)

        variant.registerJavaGeneratingTask(task, outputDir)

    }

    private static String getJsonFileName (Project project) {
        project.extensions.getByName("ext").properties.get("configJsonFile")
    }

    private static String getPackageName (Project project) {
        project.extensions.getByName("ext").properties.get("configJsonPackage")
    }

    private static String getPathForClass (Project project, def variant) {
        //def srcJava = project.android.sourceSets.main.java // not working because of some []
        getBasePathForClass(project, variant) +"/"+
                getPackageName(project).replace('.', "/");
    }

    private static String getBasePathForClass(Project project, def variant) {
        "$project.buildDir/generated/configJson/java/$variant.dirName"
    }


    static class JsonEntry {
        Object fieldValue;
        String fieldName;
        String fieldType;

        JsonEntry(Object fieldValue, String fieldName, String fieldType) {
            this.fieldValue = fieldValue
            this.fieldName = fieldName
            this.fieldType = fieldType
        }
    }
}