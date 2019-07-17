package us.ascendtech.gwt.classic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin
import us.ascendtech.gwt.common.GWTCompileTask
import us.ascendtech.gwt.common.GWTExtension
import us.ascendtech.gwt.lib.GWTLibPlugin

/**
 * @author Matt Davis
 * @author Luc Girardin
 * Apache 2.0 License
 */
class GWTClassicPlugin implements Plugin<Project> {


    @Override
    void apply(final Project project) {
        project.getPluginManager().apply(JavaPlugin.class)
        project.getPluginManager().apply(GWTLibPlugin.class)
        project.getPluginManager().apply(WarPlugin.class)

        def gwt = project.extensions.findByType(GWTExtension)

        def gwtConf = project.configurations.create("gwt")
        gwtConf.dependencies.add(new DefaultExternalModuleDependency("com.google.gwt", "gwt-dev", (String) gwt.gwtVersion))


        def runtimeOnly = project.configurations.getByName(JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME)
        runtimeOnly.defaultDependencies { deps ->
            deps.add(project.dependencies.create("com.google.gwt:gwt-servlet:${gwt.gwtVersion}"))
        }


        final File gwtExtraDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "extras")
        final File gwtOutputDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "war")
        final File codeServerDir = project.file(project.getBuildDir().name + File.separator + "gwt" + File.separator + "codeServer")

        project.task("gwtCompile", type: GWTCompileTask, dependsOn: ["classes"]) {
            outputDir = gwtOutputDir
            extraOutputDir = gwtExtraDir
            modules = gwt.modules
        }

        project.task("gwtDev", type: GWTClassicDevTask, dependsOn: "classes") {
            workDir = codeServerDir
            proxy = gwt.proxy
            modules = gwt.modules
        }

        project.tasks.war {
            dependsOn 'gwtCompile'
            from "war"
            from gwtOutputDir
        }


    }

}


