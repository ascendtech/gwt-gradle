package us.ascendtech.gwt.modern

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import us.ascendtech.gwt.common.GWTBaseTask

/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTModernDevTask extends GWTBaseTask {


    @Input
    String proxy

    @OutputDirectory
    File workDir


    public GWTModernDevTask() {
        main = "net.ltgt.gwt.devserver.DevServer"
    }

    @Override
    protected List<String> getGWTBaseArgs(Object gwt) {
        def gwtArgs = []
        gwtArgs += "-generateJsInteropExports"
        gwtArgs += "-workDir"
        gwtArgs += workDir.getAbsolutePath()
        gwtArgs += "-proxyTo"
        gwtArgs += proxy
        return gwtArgs
    }
}
