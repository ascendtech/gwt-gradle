package us.ascendtech.gwt.modern

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Compression
import org.gradle.api.tasks.bundling.Tar
import us.ascendtech.gwt.common.GWTCompileTask
import us.ascendtech.gwt.common.GWTExtension
import us.ascendtech.gwt.lib.GWTLibPlugin
import us.ascendtech.js.npm.NpmExtension
import us.ascendtech.js.npm.NpmPlugin


/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTModernPlugin implements Plugin<Project> {


    @Override
    void apply(final Project project) {
        project.getPluginManager().apply(JavaPlugin.class)
        project.pluginManager.apply(NpmPlugin)
        project.getPluginManager().apply(GWTLibPlugin.class)

        def gwt = project.extensions.findByType(GWTExtension)
        def npm = project.extensions.findByType(NpmExtension)

        def gwtConf = project.configurations.create("gwt")
        gwtConf.dependencies.add(project.dependencies.create("org.gwtproject:gwt-dev:${gwt.gwtVersion}"))
        gwtConf.dependencies.add(project.dependencies.create("org.gwtproject:gwt:${gwt.gwtVersion}"))
        gwtConf.dependencies.add(project.dependencies.create("us.ascendtech:gwt-devserver:1.1"))

        final File gwtExtraDir = project.layout.buildDirectory.dir("gwt/extras").get().asFile
        final File gwtOutputDir = project.layout.buildDirectory.dir("gwt/war").get().asFile
        final File codeServerDir = project.layout.buildDirectory.dir("gwt/codeServer").get().asFile


        project.tasks.register("gwtCompile", GWTCompileTask) { GWTCompileTask task ->
            task.dependsOn("classes", "webpack")
            task.outputDir = gwtOutputDir
            task.extraOutputDir = gwtExtraDir
            task.modules = gwt.modules
        }

        project.tasks.register("gwtDev", GWTModernDevTask) { GWTModernDevTask task ->
            task.dependsOn("classes")
            task.workDir = codeServerDir
            task.proxy = gwt.proxy
            task.modules = gwt.modules
        }

        project.tasks.register("gwtArchive", Tar) { Tar task ->
            task.dependsOn("gwtCompile")
            task.compression = Compression.GZIP
            task.destinationDirectory.set(project.layout.buildDirectory.dir("webapp"))
            task.from gwtOutputDir
            task.from npm.contentBase
            task.from npm.webpackOutputBase
        }


    }

}
