package us.ascendtech.js.npm

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input

import javax.inject.Inject

/**
 * @author Matt Davis
 * Apache 2.0 License
 * Based on https://github.com/solugo/gradle-nodejs-plugin
 */
abstract class NpmTask extends AbstractExecTask<NpmTask> {

    @Input
    abstract Property<String> getBaseCmd()

    @Input
    abstract ListProperty<String> getBaseArgs()

    @Input
    abstract ListProperty<String> getArgsSuffix()

    @Input
    abstract Property<String> getNpmModule()


    @Inject
    NpmTask() {
        super(NpmTask.class)
    }


    @Override
    protected void exec() {
        final NpmUtil nodeUtil = NpmUtil.getInstance(this.project.npm.nodeJsVersion)


        ArrayList<String> commandLine = nodeUtil.buildCommandLine(project, baseCmd.get(), baseArgs.get(), npmModule.get(), argsSuffix.get())

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
