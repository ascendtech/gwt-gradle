package us.ascendtech.gwt.modern

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
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
        project.pluginManager.apply(NpmPlugin)
        project.getPluginManager().apply(JavaPlugin.class)
        project.getPluginManager().apply(GWTLibPlugin.class)

        def gwt = project.extensions.findByType(GWTExtension)
        def npm = project.extensions.findByType(NpmExtension)

        def gwtConf = project.configurations.create("gwt")
        gwtConf.dependencies.add(new DefaultExternalModuleDependency("com.google.gwt", "gwt-dev", (String) gwt.gwtVersion))
        gwtConf.dependencies.add(new DefaultExternalModuleDependency("net.ltgt.gwt", "gwt-devserver", "1.0-SNAPSHOT"))

        final File gwtExtraDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "extras")
        final File gwtOutputDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "war")
        final File codeServerDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "codeServer")


        project.task("gwtCompile", type: GWTCompileTask, dependsOn: ["classes", "webpack"]) {
            outputDir = gwtOutputDir
            extraOutputDir = gwtExtraDir
            modules = gwt.modules
        }

        project.task("gwtDev", type: GWTModernDevTask, dependsOn: "classes") {
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


    }

}


