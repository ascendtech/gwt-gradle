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
        }

        if (gwt.includeGwtUser) {
            def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME)
            compileOnlyConfiguration.defaultDependencies { deps ->
                deps.add(project.dependencies.create("com.google.gwt:gwt-user:${gwt.gwtVersion}"))
            }

            project.configurations.all {
                resolutionStrategy {
                    force "com.google.gwt:gwt-user:${gwt.gwtVersion}"
                }
            }
        }

        project.tasks.compileJava.options.compilerArgs << '-parameters'
        project.tasks.compileJava.dependsOn(project.tasks.processResources)

        project.sourceSets.main.output.resourcesDir = "build/classes/java/main"

        project.configurations.create("gwtLib")


    }

}


