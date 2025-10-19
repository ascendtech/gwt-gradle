package us.ascendtech.gwt.classic

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import us.ascendtech.gwt.common.GWTBaseTask
import javax.inject.Inject

/**
 * @author Luc Girardin
 * Apache 2.0 License
 */
abstract class GWTClassicDevTask extends GWTBaseTask {

    @Inject
    GWTClassicDevTask() {
        super()
        mainClass.set("com.google.gwt.dev.codeserver.CodeServer")
    }

    @Input
    String proxy

    @OutputDirectory
    File workDir

    @Override
    protected List<String> getGWTBaseArgs(Object gwt) {
        def gwtArgs = []
        gwtArgs += "-generateJsInteropExports"
        gwtArgs += "-workDir"
        gwtArgs += workDir.getAbsolutePath()
        gwtArgs += "-noincremental"
        return gwtArgs
    }
}
