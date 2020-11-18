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

    //version of node js to download and use
    String nodeJsVersion = "14.15.1"

    //this is where additional content is served from in webpack dev mode
    String contentBase = "./src/main/webapp/public/"

    //this is used to check if the webpack task needs to be rerun or is up to date
    String webpackInputBase = "./src/main/webapp/"

    //this is set to build/js and is not configurable currently
    String webpackOutputBase = ""

    //dependencies to be saved
    Collection<String> dependencies = []

    //dev dependencies to be saved
    Collection<String> devDependencies = []

}
