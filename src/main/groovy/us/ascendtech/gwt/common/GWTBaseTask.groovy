package us.ascendtech.gwt.common

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.StopActionException

abstract class GWTBaseTask extends JavaExec {

    @Input
    Collection<String> modules

    @Override
    void exec() {
        def gwt = (GWTExtension) project.extensions.gwt

        def allProjects = [] as LinkedHashSet<Project>
        collectDependedUponProjects(project, allProjects, "compile")

        if (gwt.modules == null || gwt.modules.size == 0) {
            logger.warn("No GWT Modules defined for project " + project.name)
            throw new StopActionException("No gwt modules specified")
        }


        List<String> gwtCompileArgs = getGWTBaseArgs(gwt)
        for (String module : gwt.modules) {
            gwtCompileArgs += module
        }

        logger.warn("GWT Args: " + gwtCompileArgs)
        args = gwtCompileArgs

        systemProperty("gwt.watchFileChanges", "false")

        def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME)
        def runtimeOnlyConfiguration = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
        def gwtConfiguration = project.configurations.getByName("gwt")

        classpath = project.files(compileOnlyConfiguration, runtimeOnlyConfiguration, gwtConfiguration, project.sourceSets.main.java.srcDirs, project.sourceSets.main.resources.srcDirs, project.sourceSets.main.output.classesDirs, project.sourceSets.main.output.generatedSourcesDir)

        allProjects.each { p ->
            if (p.configurations.find { it.name == 'gwtLib' }) {
                for (File s : p.sourceSets.main.allSource.getSourceDirectories()) {
                    classpath += p.files(s)
                }
                classpath += p.files("build/generated/source/apt/main")
            }
        }

        super.exec()
    }

    protected abstract List<String> getGWTBaseArgs(gwt)

    public static void collectDependedUponProjects(Project project, LinkedHashSet result, String type) {
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

}
