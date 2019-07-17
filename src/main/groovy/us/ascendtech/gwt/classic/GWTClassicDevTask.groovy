package us.ascendtech.gwt.classic

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import us.ascendtech.gwt.common.GWTBaseTask

/**
 * @author Luc Girardin
 * Apache 2.0 License
 */
class GWTClassicDevTask extends GWTBaseTask {


    @Input
    String proxy

    @OutputDirectory
    File workDir


    public GWTClassicDevTask() {
        main = "com.google.gwt.dev.codeserver.CodeServer"
    }

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
