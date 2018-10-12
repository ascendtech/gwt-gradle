package us.ascendtech.gwt.lib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import us.ascendtech.gwt.common.GWTExtension

/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTLibPlugin implements Plugin<Project> {


    @Override
    void apply(final Project project) {
        project.getPluginManager().apply(JavaPlugin.class)

        def gwt = project.extensions.create("gwt", GWTExtension)

        project.repositories {
            maven {
                url 'https://raw.githubusercontent.com/intendia-oss/rxjava-gwt/mvn-repo/'
            }
            maven {
                url 'https://oss.sonatype.org/content/repositories/google-snapshots/'
            }
            maven {
                url 'https://repo.vertispan.com/gwt-snapshot/'
            }
        }

        def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME)


        compileOnlyConfiguration.defaultDependencies { deps ->
            if (gwt.libs.contains("vue")) {
                deps.add(project.dependencies.create("com.axellience:vue-gwt:1.0-beta-9"))
                deps.add(project.dependencies.create("com.axellience:vue-router-gwt:1.0-beta-9"))
            }
            if (gwt.libs.contains("autorest")) {
                deps.add(project.dependencies.create("com.intendia.gwt.autorest:autorest-gwt:0.9"))
            }
            if (gwt.libs.contains("elemento-core")) {
                if (gwt.includeGwtUser) {
                    deps.add(project.dependencies.create("org.jboss.gwt.elemento:elemento-core:0.8.7-gwt2"))
                } else {
                    deps.add(project.dependencies.create("org.jboss.gwt.elemento:elemento-core:0.8.7"))
                }
            }
            if (gwt.includeGwtUser) {
                deps.add(project.dependencies.create("com.google.gwt:gwt-user:${gwt.gwtVersion}"))
            }
        }

        def annotationConfiguration = project.configurations.getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME)
        annotationConfiguration.defaultDependencies { deps ->
            if (gwt.libs.contains("vue")) {
                deps.add(project.dependencies.create("com.axellience:vue-gwt-processors:1.0-beta-9"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
            }
            if (gwt.libs.contains("autorest")) {
                deps.add(project.dependencies.create("com.intendia.gwt.autorest:autorest-processor:0.9"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
                deps.add(project.dependencies.create("com.google.gwt:gwt:HEAD-SNAPSHOT"))
            }
        }

        if (gwt.includeGwtUser) {
            project.configurations.all {
                resolutionStrategy {
                    force "com.google.gwt:gwt-user:${gwt.gwtVersion}"
                }
            }
        }

        project.configurations.all {
            resolutionStrategy {
                force "com.google.gwt:gwt:${gwt.gwtVersion}"
            }
        }

        project.tasks.compileJava.options.compilerArgs << '-parameters'
        project.tasks.compileJava.dependsOn(project.tasks.processResources)

        project.sourceSets.main.output.resourcesDir = "build/classes/java/main"

        project.configurations.create("gwtLib")


    }

}


