package us.ascendtech.js.npm


import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option

import javax.inject.Inject

/**
 * @author Matt Davis
 * Apache 2.0 License
 * Based on https://github.com/solugo/gradle-nodejs-plugin
 */
class NpmTask extends AbstractExecTask<NpmTask> {

    @Input
    def baseCmd = ""
    @Input
    String[] baseArgs = []
    @Input
    String[] argsSuffix = []
    @Input
    def npmModule = ""


    @Inject
    NpmTask() {
        super(NpmTask.class)
    }

    @Option(option = "npmModule", description = "npm module")
    public void setNpmModule(String npmModule) {
        this.npmModule = npmModule
    }

    @Option(option = "baseArgs", description = "base args")
    public void setBaseArgs(String[] baseArgs) {
        this.setBaseArgs = baseArgs
    }

    @Override
    protected void exec() {
        final NpmUtil nodeUtil = NpmUtil.getInstance(this.project.npm.nodeJsVersion)


        ArrayList<String> commandLine = nodeUtil.buildCommandLine(project, baseCmd, baseArgs, npmModule, argsSuffix)

        project.logger.info("Adding to path ${nodeUtil.bin.absolutePath}")

        try {
            this.environment 'PATH', "${nodeUtil.bin.absolutePath}${File.pathSeparator}${environment.PATH}"
            this.environment 'Path', "${nodeUtil.bin.absolutePath}${File.pathSeparator}${environment.Path}"
            this.commandLine(commandLine)
            project.logger.info("Running " + commandLine)
            super.exec()
        } catch (Exception ex) {
            throw new RuntimeException("Error running ${commandLine.join(" ")}", ex)
        }
    }


}
