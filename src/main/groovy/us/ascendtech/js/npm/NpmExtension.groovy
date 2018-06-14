package us.ascendtech.js.npm

import org.gradle.api.Project

/**
 * @author Matt Davis
 * Apache 2.0 License
 * Based on https://github.com/solugo/gradle-nodejs-plugin
 */
class NpmExtension {

    NpmExtension(Project project) {
        webpackOutputBase = project.file(project.getBuildDir().name + File.separator + "js").getAbsolutePath()
    }

    String nodeJsVersion = "10.4.0"
    String contentBase = "./src/main/webapp/public/"
    String webpackInputBase = "./src/main/webapp/"
    String webpackOutputBase = ""

}
