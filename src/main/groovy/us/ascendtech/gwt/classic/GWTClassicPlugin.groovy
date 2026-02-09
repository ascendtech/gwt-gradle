package us.ascendtech.gwt.classic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.tasks.bundling.War
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
        gwtConf.dependencies.add(project.dependencies.create("org.gwtproject:gwt:${gwt.gwtVersion}"))


        def runtimeOnly = project.configurations.getByName(JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME)
        runtimeOnly.defaultDependencies { deps ->
            deps.add(project.dependencies.create("org.gwtproject:gwt-servlet:${gwt.gwtVersion}"))
        }


        final File gwtExtraDir = project.layout.buildDirectory.dir("gwt/extras").get().asFile
        final File gwtOutputDir = project.layout.buildDirectory.dir("gwt/war").get().asFile
        final File codeServerDir = project.layout.buildDirectory.dir("gwt/codeServer").get().asFile

        project.tasks.register("gwtCompile", GWTCompileTask) { GWTCompileTask task ->
            task.dependsOn("classes")
            task.outputDir = gwtOutputDir
            task.extraOutputDir = gwtExtraDir
            task.modules = gwt.modules
        }

        project.tasks.register("gwtDev", GWTClassicDevTask) { GWTClassicDevTask task ->
            task.dependsOn("classes")
            task.workDir = codeServerDir
            task.proxy = gwt.proxy
            task.modules = gwt.modules
        }

        project.tasks.named("war", War) { War task ->
            task.dependsOn 'gwtCompile'
            task.from "war"
            task.from gwtOutputDir
            task.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }


    }

}
