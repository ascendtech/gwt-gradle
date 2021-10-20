import org.ajoberstar.reckon.gradle.ReckonExtension

plugins {
    id("com.gradle.plugin-publish") version "0.15.0"
    id("java-gradle-plugin")
    id("groovy")
    id("org.ajoberstar.reckon") version "0.13.0"
}

configure<ReckonExtension> {
    scopeFromProp()
    stageFromProp("rc", "final") // For distribution
//    snapshotFromProp() // For local build
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    api("org.tukaani:xz:1.9")
    implementation("org.codehaus.plexus:plexus-archiver:4.2.5")
    implementation("org.codehaus.plexus:plexus-container-default:2.1.0")
    testCompileOnly(gradleTestKit())
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
        create("gwtDepPlugin") {
            id = "us.ascendtech.gwt.dep"
            implementationClass = "us.ascendtech.gwt.dep.GWTDepPlugin"
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
        "gwtDepPlugin" {
            id = "us.ascendtech.gwt.dep"
            displayName = "GWT Dep plugin"
            description = "Plugin for gwt dependencies projects"
            tags = listOf("gwt")

        }
    }


}
