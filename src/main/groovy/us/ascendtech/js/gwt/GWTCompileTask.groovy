package us.ascendtech.js.gwt

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.StopActionException

/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTCompileTask extends JavaExec {

    @Input
    Collection<String> modules

    @OutputDirectory
    File outputDir

    @OutputDirectory
    File extraOutputDir


    static void collectDependedUponProjects(Project project, LinkedHashSet result, String type) {
        def config = project.configurations.findByName(type)
        if (config) {
            def projectDeps = config.allDependencies.withType(ProjectDependency)
            def dependedUponProjects = projectDeps*.dependencyProject
            result.addAll(dependedUponProjects)
            for (dependedUponProject in dependedUponProjects) {
                collectDependedUponProjects(dependedUponProject, result, type)
            }
        }
    }

    @Override
    void exec() {
        def gwt = project.extensions.gwt

        def allProjects = [] as LinkedHashSet<Project>
        collectDependedUponProjects(project, allProjects, "compile")



        def gwtCompileArgs = []
        gwtCompileArgs += "-generateJsInteropExports"
        gwtCompileArgs += "-localWorkers"
        gwtCompileArgs += gwt.localWorkers
        gwtCompileArgs += "-logLevel"
        gwtCompileArgs += gwt.logLevel
        gwtCompileArgs += "-style"
        gwtCompileArgs += gwt.style
        gwtCompileArgs += "-extra"
        gwtCompileArgs += extraOutputDir.getAbsolutePath()
        gwtCompileArgs += "-war"
        gwtCompileArgs += outputDir.getAbsolutePath()

        for (String module : gwt.modules) {
            gwtCompileArgs += module
        }

        logger.info("GWT Compiler Args: " + gwtCompileArgs)


        if (gwt.modules == null || gwt.modules.size == 0) {
            logger.warn("No GWT Modules defined for project " + project.name)
            throw new StopActionException("No gwt modules specified")
        }

        main = "com.google.gwt.dev.Compiler"
        args = gwtCompileArgs


        def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME)

        classpath = project.files(compileOnlyConfiguration, project.sourceSets.main.java.srcDirs, project.sourceSets.main.resources.srcDirs, project.sourceSets.main.output.classesDirs, project.sourceSets.main.output.generatedSourcesDir)

        allProjects.each { p ->
            if (p.configurations.find { it.name == 'source' }) {
                p.configurations['source'].allArtifacts.getFiles().each {
                    logger.info("Add {} to GWT classpath!", it)
                    classpath += project.files(it)
                }

            }
        }


        super.exec()
    }
}
