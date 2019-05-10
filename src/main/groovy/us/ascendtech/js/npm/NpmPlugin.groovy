package us.ascendtech.js.npm

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import us.ascendtech.gwt.common.GWTBaseTask

/**
 * @author Matt Davis
 * Apache 2.0 License
 * Based on https://github.com/solugo/gradle-nodejs-plugin
 */
class NpmPlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        def npm = project.extensions.create("npm", NpmExtension, project)


        def compileOnlyConfiguration = project.configurations.getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME)
        compileOnlyConfiguration.defaultDependencies { deps ->
            addDependentProjectLibs(project, npm)

            println npm.dependencies
        }

        project.task("npmClean", type: DefaultTask) {
            doLast {


                println "Deleting node modules"
                project.file("node_modules").deleteDir()
                project.file("node_modules").mkdir()
            }
        }

        project.task("npmInstallDep", type: DefaultTask, dependsOn: ["configurations.compile"]) {

            doLast {

                println "Found gradle npm dependencies: $npm.dependencies for project $project.name"
                File f = new File(project.file("node_modules").absolutePath + File.separator + "gradleNpmDep.txt")
                Set<String> newDeps = new LinkedHashSet<>(npm.dependencies)
                newDeps.removeAll(f.exists() ? f.readLines() : Collections.emptyList())

                if (!newDeps.isEmpty()) {
                    println "Found new gradle npm dependencies: $newDeps"
                }

                for (String dep : newDeps) {
                    int status = installNpmModule(npm, project, dep, (String[]) ["--save"])
                    if (status != 0) {
                        throw new GradleException("Failed to install gradle npm dependency " + dep)
                    }

                }

                f.write(npm.dependencies.join(System.lineSeparator()))

                println "Found gradle npm dev dependencies: $npm.devDependencies for project $project.name"
                f = new File(project.file("node_modules").absolutePath + File.separator + "gradleNpmDevDep.txt")
                newDeps = new LinkedHashSet<>(npm.devDependencies)
                newDeps.removeAll(f.exists() ? f.readLines() : Collections.emptyList())

                if (!newDeps.isEmpty()) {
                    println "Found new gradle npm dev dependencies: $newDeps"
                }

                for (String dep : newDeps) {
                    int status = installNpmModule(npm, project, dep, (String[]) ["--save-dev"])
                    if (status != 0) {
                        throw new GradleException("Failed to install gradle npm dev dependency " + dep)
                    }

                }

                f.write(npm.devDependencies.join(System.lineSeparator()))

            }


        }


        //final cleanTask = project.tasks.findByPath("clean")
        //cleanTask.dependsOn("npmClean")

        project.task("npmInstall", type: NpmTask) {
            baseCmd = "npm"
            baseArgs = ["install"]
            inputs.file(project.file("package.json"))
            outputs.dir(project.file("node_modules"))
        }

        project.task("npmInstallSaveDev", type: NpmTask) {
            baseCmd = "npm"
            baseArgs = ["install"]
            argsSuffix = ["--save-dev"]
        }

        project.task("npmInstallSave", type: NpmTask) {
            baseCmd = "npm"
            baseArgs = ["install"]
            argsSuffix = ["--save"]
        }

        project.tasks.create(name: "webpack", type: NpmTask, dependsOn: ["npmInstallDep", "npmInstall"]) {
            baseCmd = "webpack-cli"
            baseArgs = ["--mode=production", "--output-path", "${npm.webpackOutputBase}"]
            inputs.file(project.file("webpack.config.js"))
            inputs.file(project.file("package-lock.json"))
            inputs.dir(npm.webpackInputBase)
            outputs.dir(npm.webpackOutputBase)
        }

        project.tasks.create(name: "webpackDev", type: NpmTask, dependsOn: ["npmInstallDep", "npmInstall"]) {
            baseCmd = "webpack-dev-server"
            baseArgs = ["--mode=development", "--content-base", "${npm.contentBase}"]
        }

        project.configurations.create("npm")
    }

    private int installNpmModule(NpmExtension npm, Project project, String npmModule, String[] argsSuffix) {
        String baseCmd = "npm"
        String[] baseArgs = ["install"]

        final NpmUtil nodeUtil = NpmUtil.getInstance(npm.nodeJsVersion)
        List<String> commandLine = nodeUtil.buildCommandLine(project, baseCmd, baseArgs, npmModule, argsSuffix)

        ProcessBuilder builder = new ProcessBuilder().redirectErrorStream(true).command(commandLine)
        String path1 = builder.environment().get("PATH")
        if (path1 == null || path1.isEmpty()) {
            builder.environment().put("PATH", nodeUtil.bin.absolutePath + File.separator + path1)
        } else {
            builder.environment().put("PATH", nodeUtil.bin.absolutePath)
        }
        String path2 = builder.environment().get("Path")
        if (path2 == null || path2.isEmpty()) {
            builder.environment().put("Path", nodeUtil.bin.absolutePath + File.separator + path2)
        } else {
            builder.environment().put("Path", nodeUtil.bin.absolutePath)
        }

        Process process = builder.start()
        InputStream stdout = process.getInputStream()
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(stdout))

        def line
        while ((line = reader.readLine()) != null) {
            println line

        }

        return process.waitFor()
    }

    private void addDependentProjectLibs(Project project, NpmExtension npm) {
        def allProjects = [] as LinkedHashSet<Project>
        GWTBaseTask.collectDependedUponProjects(project, allProjects, "compile")
        allProjects.each { p ->
            if (p.configurations.find { it.name == 'npm' }) {
                def npmExt = p.extensions.findByType(NpmExtension)
                project.logger.warn("Dependent project " + p.name + " has npm dependencies " + npmExt.dependencies)
                project.logger.warn("Dependent project " + p.name + " has npm dev dependencies " + npmExt.devDependencies)


                npmExt.dependencies.forEach({
                    if (!npm.dependencies.contains(it)) {
                        npm.dependencies.add(it)
                    }
                })

                npmExt.devDependencies.forEach({
                    if (!npm.devDependencies.contains(it)) {
                        npm.devDependencies.add(it)
                    }
                })

            }
        }

        project.logger.warn("Project " + project.name + " has dependencies " + npm.dependencies)
        project.logger.warn("Project " + project.name + " has dev dependencies " + npm.devDependencies)
    }
}


