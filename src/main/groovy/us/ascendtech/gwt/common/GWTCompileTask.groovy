package us.ascendtech.gwt.common

import org.gradle.api.tasks.OutputDirectory
import javax.inject.Inject

/**
 * @author Matt Davis
 * @author Luc Girardin
 * Apache 2.0 License
 */
abstract class GWTCompileTask extends GWTBaseTask {
    @Inject
    GWTCompileTask() {
        super()
        mainClass.set("com.google.gwt.dev.Compiler")
    }

    @OutputDirectory
    File outputDir

    @OutputDirectory
    File extraOutputDir

    @Override
    protected List<String> getGWTBaseArgs(Object gwt) {
        def gwtCompileArgs = []
        gwtCompileArgs += "-generateJsInteropExports"
        gwtCompileArgs += "-localWorkers"
        gwtCompileArgs += gwt.localWorkers
        gwtCompileArgs += "-logLevel"
        gwtCompileArgs += gwt.logLevel
        gwtCompileArgs += "-style"
        gwtCompileArgs += gwt.style
        if(gwt.extra) {
            gwtCompileArgs += "-extra"
            gwtCompileArgs += extraOutputDir.getAbsolutePath()
        }
        if(gwt.incremental) {
            gwtCompileArgs += "-incremental"
        } else {
            gwtCompileArgs += "-noincremental"
        }
        gwtCompileArgs += "-sourceLevel"
        gwtCompileArgs += gwt.sourceLevel
        gwtCompileArgs += "-war"
        gwtCompileArgs += outputDir.getAbsolutePath()
        return gwtCompileArgs
    }
}
