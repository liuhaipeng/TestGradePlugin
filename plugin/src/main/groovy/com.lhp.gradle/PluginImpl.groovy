package com.lhp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
public class PluginImpl implements Plugin<Project> {
    void apply(Project project) {
        project.task('testTask') {
            println "+++++++++++++++++++++++++"
            println "Hello gradle plugin"
            println "+++++++++++++++++++++++++"
        }
    }
}
