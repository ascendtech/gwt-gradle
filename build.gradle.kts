plugins {
    id("com.gradle.plugin-publish") version "0.10.0"
    id("java-gradle-plugin")
    id("groovy")
}


version = "0.3.16"

repositories {
    mavenCentral()
}


dependencies {
    compile(gradleApi())
    compile("org.codehaus.plexus:plexus-archiver:3.5")
    compile("org.codehaus.plexus:plexus-container-default:1.7.1")
    testCompile(gradleTestKit())
}

gradlePlugin {
    plugins {
        create("npmPlugin") {
            id = "us.ascendtech.js.npm"
            implementationClass = "us.ascendtech.js.npm.NpmPlugin"
        }
        create("gwtModernPlugin") {
            id = "us.ascendtech.gwt.modern"
            implementationClass = "us.ascendtech.gwt.modern.GWTModernPlugin"
        }
        create("gwtClassicPlugin") {
            id = "us.ascendtech.gwt.classic"
            implementationClass = "us.ascendtech.gwt.classic.GWTClassicPlugin"
        }
        create("gwtLibPlugin") {
            id = "us.ascendtech.gwt.lib"
            implementationClass = "us.ascendtech.gwt.lib.GWTLibPlugin"
        }
    }
}


pluginBundle {
    website = "https://github.com/ascendtech/gwt-gradle"
    vcsUrl = "https://github.com/ascendtech/gwt-gradle"

    (plugins) {
        "npmPlugin" {
            id = "us.ascendtech.js.npm"
            displayName = "NPM/Webpack plugin"
            description = "Plugin for npm and webpack support in gradle"
            tags = listOf("webpack", "npm")

        }
        "gwtModernPlugin" {
            id = "us.ascendtech.gwt.modern"
            displayName = "Modern GWT plugin"
            description = "Plugin for modern GWT projects based on webpack"
            tags = listOf("gwt", "webpack", "npm")

        }
        "gwtClassicPlugin" {
            id = "us.ascendtech.gwt.classic"
            displayName = "Classic GWT plugin"
            description = "Plugin for classic GWT projects"
            tags = listOf("gwt")

        }
        "gwtLibPlugin" {
            id = "us.ascendtech.gwt.lib"
            displayName = "GWT Lib plugin"
            description = "Plugin for gwt lib projects"
            tags = listOf("gwt")

        }
    }


}