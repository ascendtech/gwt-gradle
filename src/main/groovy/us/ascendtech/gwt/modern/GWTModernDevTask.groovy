package us.ascendtech.gwt.modern

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import us.ascendtech.gwt.common.GWTBaseTask
import javax.inject.Inject

/**
 * @author Matt Davis
 * Apache 2.0 License
 */
abstract class GWTModernDevTask extends GWTBaseTask {

    @Inject
    GWTModernDevTask() {
        super()
        mainClass.set("net.ltgt.gwt.devserver.DevServer")
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
        gwtArgs += "-proxyTo"
        gwtArgs += proxy
        return gwtArgs
    }
}
