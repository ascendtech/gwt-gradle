package us.ascendtech.gwt.lib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import us.ascendtech.gwt.common.GWTBaseTask
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
                url 'https://maven.ascend-tech.us/repo'
            }
        }

        def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME)

        project.tasks.compileJava.outputs.upToDateWhen { false }
        project.logger.warn("Forcing full recompile use --build-cache -t compileJava for continuous build")

        compileOnlyConfiguration.defaultDependencies { deps ->
            addDependentProjectLibs(project, gwt)

            if (gwt.libs.contains("vue")) {
                deps.add(project.dependencies.create("com.axellience:vue-gwt:1.0-beta-9"))
                deps.add(project.dependencies.create("com.axellience:vue-router-gwt:1.0-beta-9"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
            }
            if (gwt.libs.contains("autorest")) {
                deps.add(project.dependencies.create("com.intendia.gwt.autorest:autorest-gwt:0.9"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
            }
            if (gwt.libs.contains("ast-highcharts")) {
                deps.add(project.dependencies.create('us.ascendtech:highcharts:1.1.0'))
                deps.add(project.dependencies.create('us.ascendtech:highcharts:1.1.0:sources'))

                if (gwt.includeGwtUser) {
                    deps.add(project.dependencies.create('us.ascendtech:highcharts-injector:1.1.0'))
                    deps.add(project.dependencies.create('us.ascendtech:highcharts-injector:1.1.0:sources'))
                }
            }
            if (gwt.libs.contains("elemento-core")) {
                if (gwt.includeGwtUser) {
                    deps.add(project.dependencies.create("org.jboss.gwt.elemento:elemento-core:0.9.0-gwt2"))
                } else {
                    deps.add(project.dependencies.create("org.jboss.gwt.elemento:elemento-core:0.9.0"))
                }
            }
            if (gwt.includeGwtUser) {
                deps.add(project.dependencies.create("com.google.gwt:gwt-user:${gwt.gwtVersion}"))
            }
        }

        def annotationConfiguration = project.configurations.getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME)
        annotationConfiguration.defaultDependencies { deps ->
            addDependentProjectLibs(project, gwt)
            if (gwt.libs.contains("vue")) {
                deps.add(project.dependencies.create("com.axellience:vue-gwt-processors:1.0-beta-9"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
            }
            if (gwt.libs.contains("autorest")) {
                deps.add(project.dependencies.create("com.intendia.gwt.autorest:autorest-processor:0.9"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
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

    private void addDependentProjectLibs(Project project, GWTExtension gwt) {
        def allProjects = [] as LinkedHashSet<Project>
        GWTBaseTask.collectDependedUponProjects(project, allProjects, "compile")
        allProjects.each { p ->
            if (p.configurations.find { it.name == 'gwtLib' }) {
                def libGwt = p.extensions.findByType(GWTExtension)
                project.logger.warn("Dependent project " + p.name + " has libs " + libGwt.libs)


                libGwt.libs.forEach({
                    if (!gwt.libs.contains(it)) {
                        gwt.libs.add(it)
                    }
                })

            }
        }

        project.logger.warn("Project " + project.name + " has libs " + gwt.libs)
    }

}


