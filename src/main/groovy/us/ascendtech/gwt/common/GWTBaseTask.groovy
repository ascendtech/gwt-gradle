package us.ascendtech.gwt.common

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.StopActionException

abstract class GWTBaseTask extends JavaExec {
    @Input
    Collection<String> modules

    /**
     * Enumerate the list of directories containing source code and resources that should trigger recompilation when
     * changed, i.e. these are the input to the GWT compiler
     *
     * @return the list of directories
     */
    @InputFiles
    public getInputFiles() {
        List<File> files = new ArrayList<>();

        def allProjects = [] as LinkedHashSet<Project>
        collectDependedUponProjects(project, allProjects, JavaPlugin.API_CONFIGURATION_NAME)
        collectDependedUponProjects(project, allProjects, JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)

        for (Iterator iterator = allProjects.iterator(); iterator.hasNext();) {
            def p  =  iterator.next();
            for (File s : p.sourceSets.main.allSource.getSourceDirectories()) {
                files += p.fileTree(s)
            }
        }

        files += project.sourceSets.main.java.srcDirs

        return files;
    }

    @Override
    void exec() {
        def gwt = (GWTExtension) project.extensions.gwt

        def allProjects = [] as LinkedHashSet<Project>
        collectDependedUponProjects(project, allProjects, JavaPlugin.API_CONFIGURATION_NAME)
        collectDependedUponProjects(project, allProjects, JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)

        if (gwt.modules == null || gwt.modules.size == 0) {
            logger.warn("No GWT Modules defined for project " + project.name)
            throw new StopActionException("No gwt modules specified")
        }


        List<String> gwtCompileArgs = getGWTBaseArgs(gwt)
        for (String module : gwt.modules) {
            gwtCompileArgs += module
        }

        logger.info("GWT Args: " + gwtCompileArgs)
        args = gwtCompileArgs

        if(!gwt.persistentunitcache) {
            systemProperty("gwt.persistentunitcache", "false")
        }
        systemProperty("gwt.watchFileChanges", "false")

        def implementationConfiguration = project.configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)
        def runtimeOnlyConfiguration = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
        def gwtConfiguration = project.configurations.getByName("gwt")

        if (project.sourceSets.main.output.hasProperty("generatedSourcesDir")) {
            classpath = project.files(implementationConfiguration, runtimeOnlyConfiguration, gwtConfiguration, project.sourceSets.main.java.srcDirs, project.sourceSets.main.resources.srcDirs, project.sourceSets.main.output.classesDirs, project.sourceSets.main.output.generatedSourcesDir)
        } else {
            classpath = project.files(implementationConfiguration, runtimeOnlyConfiguration, gwtConfiguration, project.sourceSets.main.java.srcDirs, project.sourceSets.main.resources.srcDirs, project.sourceSets.main.output.classesDirs)
        }


        allProjects.each { p ->
            print(p.name);
            if (p.configurations.find { it.name == 'gwtLib' }) {
                for (File s : p.sourceSets.main.allSource.getSourceDirectories()) {
                    classpath += p.files(s)
                }

                classpath += p.files("build/generated/sources/annotationProcessor/java/main")
            }
        }
        classpath += project.files("build/generated/sources/annotationProcessor/java/main")
        logger.warn("Classpath: " + classpath.toList());

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
