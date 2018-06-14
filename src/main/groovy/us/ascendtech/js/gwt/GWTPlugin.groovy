package us.ascendtech.js.gwt

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Compression
import org.gradle.api.tasks.bundling.Tar
import org.gradle.jvm.tasks.Jar
import us.ascendtech.js.npm.NpmExtension
import us.ascendtech.js.npm.NpmPlugin


/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTPlugin implements Plugin<Project> {


    @Override
    void apply(final Project project) {
        project.pluginManager.apply(NpmPlugin)
        project.getPluginManager().apply(JavaPlugin.class)

        def gwt = project.extensions.create("gwt", GWTExtension)
        def npm = project.extensions.findByType(NpmExtension)


        project.repositories {
            maven {
                url 'https://jitpack.io'
            }
        }

        def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)
        compileOnlyConfiguration.defaultDependencies { deps ->
            deps.add(project.dependencies.create("com.google.gwt:gwt-dev:${gwt.gwtVersion}"))
            deps.add(project.dependencies.create("com.google.gwt:gwt-user:${gwt.gwtVersion}"))
            deps.add(project.dependencies.create("org.eclipse.jetty:jetty-proxy:9.2.14.v20151106"))
            deps.add(project.dependencies.create("com.github.tbroyer:gwt-devserver:-SNAPSHOT"))
        }


        final File gwtExtraDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "extras")
        final File gwtOutputDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "war")
        final File codeServerDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "codeServer")


        project.task("sourceJar", type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }

        project.task("gwtCompile", type: GWTCompileTask, dependsOn: ["classes", "webpack"]) {
            outputDir = gwtOutputDir
            extraOutputDir = gwtExtraDir
            modules = gwt.modules
        }

        project.task("gwtDev", type: GWTDevTask, dependsOn: "classes") {
            workDir = codeServerDir
            proxy = gwt.proxy
            modules = gwt.modules
        }

        project.task("gwtArchive", type: Tar, dependsOn: "gwtCompile") {
            compression = Compression.GZIP
            destinationDir = project.file(project.getBuildDir().name + File.separator + "webapp")
            from gwtOutputDir
            from npm.contentBase
            from npm.webpackOutputBase
        }

        project.tasks.compileJava.options.compilerArgs << '-parameters'
        project.tasks.compileJava.dependsOn(project.tasks.processResources)

        project.sourceSets.main.output.resourcesDir = "build/classes/java/main"

        project.tasks.jar.dependsOn(project.tasks.sourceJar)

        project.configurations.create("source")

        project.artifacts.add("source", project.tasks.sourceJar)


    }

}


