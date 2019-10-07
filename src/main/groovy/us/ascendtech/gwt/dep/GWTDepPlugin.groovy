package us.ascendtech.gwt.dep

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import us.ascendtech.gwt.common.GWTBaseTask
import us.ascendtech.gwt.common.GWTExtension

/**
 * Plugin to handle modules that should be included in the GWT compilation but that aren't GWT modules
 *
 * @author Luc Girardin
 * Apache 2.0 License
 */
class GWTDepPlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        project.getPluginManager().apply(JavaPlugin.class)

        def gwt = project.extensions.create("gwt", GWTExtension)

        project.configurations.create("gwtLib")
    }
}


