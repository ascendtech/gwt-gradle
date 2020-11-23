package us.ascendtech.gwt.lib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar
import us.ascendtech.gwt.common.GWTBaseTask
import us.ascendtech.gwt.common.GWTExtension

/**
 * @author Matt Davis
 * @author Luc Girardin
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

        compileOnlyConfiguration.defaultDependencies { deps ->
            project.logger.info("Using gwt version " + gwt.gwtVersion)
            addDependentProjectLibs(project, gwt)

            if (gwt.libs.contains("vue")) {
                deps.add(project.dependencies.create("com.axellience:vue-gwt:1.0.1"))
                deps.add(project.dependencies.create("com.axellience:vue-router-gwt:1.0.1"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
            }
            if (gwt.libs.contains("autorest")) {
                deps.add(project.dependencies.create("com.intendia.gwt.autorest:autorest-gwt:0.9"))
                deps.add(project.dependencies.create("javax.annotation:javax.annotation-api:1.3.2"))
            }
            if (gwt.libs.contains("ast-aggrid")) {
                deps.add(project.dependencies.create('us.ascendtech:agGrid:0.1.10'))
                deps.add(project.dependencies.create('us.ascendtech:agGrid:0.1.10:sources'))
            }
            if (gwt.libs.contains("ast-momentjs")) {
                deps.add(project.dependencies.create('us.ascendtech:momentjs:0.1.11'))
                deps.add(project.dependencies.create('us.ascendtech:momentjs:0.1.11:sources'))
                if (gwt.includeGwtUser) {
                    deps.add(project.dependencies.create('us.ascendtech:momentjs-injector:0.1.11'))
                    deps.add(project.dependencies.create('us.ascendtech:momentjs-injector:0.1.11:sources'))
                }

            }
            if (gwt.libs.contains("ast-highcharts")) {
                deps.add(project.dependencies.create('us.ascendtech:highcharts:1.1.5'))
                deps.add(project.dependencies.create('us.ascendtech:highcharts:1.1.5:sources'))

                if (gwt.includeGwtUser) {
                    deps.add(project.dependencies.create('us.ascendtech:highcharts-injector:1.1.5'))
                    deps.add(project.dependencies.create('us.ascendtech:highcharts-injector:1.1.5:sources'))
                }
            }
            if (gwt.libs.contains("elemento-core-legacy")) {
                if (gwt.includeGwtUser) {
                    deps.add(project.dependencies.create("org.jboss.gwt.elemento:elemento-core:0.9.6-gwt2"))
                } else {
                    deps.add(project.dependencies.create("org.jboss.gwt.elemento:elemento-core:0.9.6"))
                }
            }
            if (gwt.libs.contains("elemento-core")) {
                deps.add(project.dependencies.create("org.jboss.elemento:elemento-core:1.0.2"))
            }

            if (gwt.libs.contains("core")) {
                deps.add(project.dependencies.create("org.gwtproject.core:gwt-core:1.0.0-RC1"))
            }

            if (gwt.libs.contains("event")) {
                deps.add(project.dependencies.create("org.gwtproject.event:gwt-event:1.0.0-RC1"))
            }

            if (gwt.libs.contains("places")) {
                deps.add(project.dependencies.create("org.gwtproject.places:gwt-places:1.0.0-RC1"))
            }

            if (gwt.libs.contains("history")) {
                deps.add(project.dependencies.create("org.gwtproject.user.history:gwt-history:1.0.0-RC1"))
            }

            if (gwt.libs.contains("timer")) {
                deps.add(project.dependencies.create("org.gwtproject.timer:gwt-timer:1.0.0-RC1"))
            }


            if (gwt.includeGwtUser) {
                deps.add(project.dependencies.create("com.google.gwt:gwt-user:${gwt.gwtVersion}"))
            }
        }

        def annotationConfiguration = project.configurations.getByName(JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME)
        annotationConfiguration.defaultDependencies { deps ->
            addDependentProjectLibs(project, gwt)
            if (gwt.libs.contains("vue")) {
                deps.add(project.dependencies.create("com.axellience:vue-gwt-processors:1.0-beta-10-SNAPSHOT"))
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

        if (gwt.forceRecompile) {
            project.tasks.compileJava.options.compilerArgs << '-parameters'
            project.tasks.compileJava.dependsOn(project.tasks.processResources)
            project.sourceSets.main.output.resourcesDir = "build/classes/java/main"
            project.tasks.compileJava.outputs.upToDateWhen { false }
            project.logger.info("Forcing full recompile use --build-cache -t compileJava for continuous build")
        }

        project.tasks.withType(Jar) { Jar jarTask ->
            jarTask.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }

        project.configurations.create("gwtLib")


    }

    private void addDependentProjectLibs(Project project, GWTExtension gwt) {
        def allProjects = [] as LinkedHashSet<Project>
        GWTBaseTask.collectDependedUponProjects(project, allProjects, "compile")
        allProjects.each { p ->
            if (p.configurations.find { it.name == 'gwtLib' }) {
                def libGwt = p.extensions.findByType(GWTExtension)
                project.logger.info("Dependent project " + p.name + " has libs " + libGwt.libs)


                libGwt.libs.forEach({
                    if (!gwt.libs.contains(it)) {
                        gwt.libs.add(it)
                    }
                })

            }
        }

        project.logger.info("Project " + project.name + " has libs " + gwt.libs)
    }

}


