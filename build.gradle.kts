
plugins {
    id("com.gradle.plugin-publish") version "1.3.1"
    id("java-gradle-plugin")
    id("groovy")
    alias(libs.plugins.reckon)
}

reckon {
    setDefaultInferredScope("patch")
    setScopeCalc(calcScopeFromProp())
    snapshots()
    stages("beta", "final")
    setStageCalc(calcStageFromProp())
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

group = "us.ascendtech"

dependencies {
    implementation(gradleApi())
    implementation("org.codehaus.plexus:plexus-archiver:4.10.0")
    implementation("org.codehaus.plexus:plexus-archiver:4.10.0")
    testCompileOnly(gradleTestKit())
}

gradlePlugin {
    website = "https://github.com/ascendtech/gwt-gradle"
    vcsUrl = "https://github.com/ascendtech/gwt-gradle"
    plugins {
        create("npmPlugin") {
            id = "us.ascendtech.js.npm"
            implementationClass = "us.ascendtech.js.npm.NpmPlugin"
            displayName = "NPM/Webpack plugin"
            description = "Plugin for npm and webpack support in gradle"
            tags = listOf("webpack", "npm")
        }
        create("gwtModernPlugin") {
            id = "us.ascendtech.gwt.modern"
            implementationClass = "us.ascendtech.gwt.modern.GWTModernPlugin"
            displayName = "Modern GWT plugin"
            description = "Plugin for modern GWT projects based on webpack"
            tags = listOf("gwt", "webpack", "npm")
        }
        create("gwtClassicPlugin") {
            id = "us.ascendtech.gwt.classic"
            implementationClass = "us.ascendtech.gwt.classic.GWTClassicPlugin"
            displayName = "Classic GWT plugin"
            description = "Plugin for classic GWT projects"
            tags = listOf("gwt")
        }
        create("gwtLibPlugin") {
            id = "us.ascendtech.gwt.lib"
            implementationClass = "us.ascendtech.gwt.lib.GWTLibPlugin"
            displayName = "GWT Lib plugin"
            description = "Plugin for gwt lib projects"
            tags = listOf("gwt")
        }
        create("gwtDepPlugin") {
            id = "us.ascendtech.gwt.dep"
            implementationClass = "us.ascendtech.gwt.dep.GWTDepPlugin"
            displayName = "GWT Dep plugin"
            description = "Plugin for gwt dependencies projects"
            tags = listOf("gwt")
        }
    }

}


