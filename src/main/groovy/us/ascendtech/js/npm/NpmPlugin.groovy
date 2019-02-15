package us.ascendtech.js.npm

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Matt Davis
 * Apache 2.0 License
 * Based on https://github.com/solugo/gradle-nodejs-plugin
 */
class NpmPlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        def npm = project.extensions.create("npm", NpmExtension, project)

        project.task("npmClean", type: DefaultTask) {
            doLast {
                println "Deleting node modules"
                project.file("node_modules").deleteDir()
                project.file("node_modules").mkdir()
            }
        }

        final cleanTask = project.tasks.findByPath("clean")
        cleanTask.dependsOn("npmClean")

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

        project.tasks.create(name: "webpack", type: NpmTask, dependsOn: "npmInstall") {
            baseCmd = "webpack-cli"
            baseArgs = ["--mode=production", "--output-path", "${npm.webpackOutputBase}"]
            inputs.file(project.file("webpack.config.js"))
            inputs.file(project.file("package-lock.json"))
            inputs.dir(npm.webpackInputBase)
            outputs.dir(npm.webpackOutputBase)
        }

        project.tasks.create(name: "webpackDev", type: NpmTask, dependsOn: "npmInstall") {
            baseCmd = "webpack-dev-server"
            baseArgs = ["--mode=development", "--content-base", "${npm.contentBase}"]
        }

    }

}


