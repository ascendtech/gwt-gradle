package us.ascendtech.js.gwt

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.StopActionException

/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTDevTask extends JavaExec {

    @Input
    Collection<String> modules

    @Input
    String proxy

    @OutputDirectory
    File workDir


    @Override
    void exec() {
        def gwt = project.extensions.gwt;

        def allProjects = [] as LinkedHashSet
        GWTCompileTask.collectDependedUponProjects(project, allProjects, "compile")


        def gwtCompileArgs = []
        gwtCompileArgs += "-generateJsInteropExports"
        gwtCompileArgs += "-workDir"
        gwtCompileArgs += workDir.getAbsolutePath()
        gwtCompileArgs += "-proxyTo"
        gwtCompileArgs += proxy

        for (String module : gwt.modules) {
            gwtCompileArgs += module
        }

        logger.info("GWT Dev Args: " + gwtCompileArgs)


        if (gwt.modules == null || gwt.modules.size == 0) {
            logger.warn("No GWT Modules defined for project " + project.name)
            throw new StopActionException("No gwt modules specified")
        }

        main = "net.ltgt.gwt.devserver.DevServer"
        args = gwtCompileArgs


        def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME)
        def runtimeConfiguration = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)


        def buildPluginPath = project.getRootProject().file("buildSrc" + File.separator + "build" + File.separator + "classes" + File.separator + "groovy" + File.separator + "main")
        classpath = project.files(buildPluginPath, runtimeConfiguration, compileOnlyConfiguration, project.sourceSets.main.java.srcDirs, project.sourceSets.main.resources.srcDirs, project.sourceSets.main.output.classesDirs, project.sourceSets.main.output.generatedSourcesDir)

        allProjects.each { p ->
            p.configurations['source'].allArtifacts.getFiles().each {
                logger.info("Add {} to GWT classpath!", it)
                classpath += it
            }
        }


        super.exec()
    }
}
