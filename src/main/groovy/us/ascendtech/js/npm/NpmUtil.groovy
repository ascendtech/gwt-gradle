package us.ascendtech.js.npm

import org.apache.tools.ant.taskdefs.condition.Os
import org.codehaus.plexus.archiver.ArchiverException
import org.codehaus.plexus.archiver.UnArchiver
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.codehaus.plexus.archiver.tar.TarXZUnArchiver
import org.codehaus.plexus.archiver.zip.ZipUnArchiver
import org.codehaus.plexus.components.io.filemappers.FileMapper
import org.gradle.api.Project

/**
 * @author Matt Davis
 * Apache 2.0 License
 * Based on https://github.com/solugo/gradle-nodejs-plugin
 */
class NpmUtil {

    static synchronized NpmUtil getInstance(String version) {
        File home = new File(System.getProperty("user.home"))
        File cache = new File(home, ".nodejs")
        File target = new File(cache, version)

        File modules
        File bin

        String platform
        String ext

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            platform = "win"
            ext = "zip"
            modules = new File(target, "node_modules")
            bin = target
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            platform = "darwin"
            ext = "tar.gz"
            modules = new File(target, "lib")
            bin = new File(target, "bin")
        } else if (Os.isFamily(Os.FAMILY_UNIX)) {
            platform = "linux"
            ext = "tar.gz"
            modules = new File(target, "lib/node_modules")
            bin = new File(target, "bin")
        } else {
            throw new UnsupportedOperationException("Platform not supported")
        }

        final String arch
        if (Os.isArch("x86")) {
            arch = "x86"
        } else {
            arch = "x64"
        }

        if (!target.exists()) {
            final root = "node-v${version}-${platform}-${arch}"
            final file = "${root}.${ext}"
            final downloadFile = new File(cache, file)

            if (!downloadFile.exists()) {

                downloadFile.parentFile.mkdirs()
                final url = new URL("https://nodejs.org/dist/v${version}/${file}")

                println("Downloading NodeJs from ${url}")
                url.withInputStream { is -> downloadFile.withOutputStream { os -> os << is } }
            }

            try {
                println("Extracting NodeJs to ${target.absolutePath}")

                target.mkdirs()

                final UnArchiver unArchiver

                if (ext == "tar.xz") {
                    unArchiver = new NodeJsTarXzUnArchiver()
                } else if (ext == "tar.gz") {
                    unArchiver = new NodeJsTarGzUnArchiver()
                } else if (ext == "zip") {
                    unArchiver = new NodeJsZipUnArchiver()
                } else {
                    throw new UnsupportedOperationException("Archive ${downloadFile.name} not supported")
                }

                unArchiver.sourceFile = downloadFile
                unArchiver.overwrite = true
                unArchiver.destFile = target
                unArchiver.extract()
            } catch (final Throwable throwable) {
                target.delete()
                throw throwable
            }

        }

        return new NpmUtil(target, bin, modules)
    }

    private final File home
    private final File bin
    private final File modules

    private NpmUtil(final File home, final File bin, final File modules) {
        this.modules = modules
        this.home = home
        this.bin = bin
    }

    File getBin() {
        return this.bin
    }

    ArrayList<String> buildCommandLine(final Project project, final String baseCmd, final List<String> baseArgs, final String npmModule, final List<String> argsSuffix) {
        final commandLine = new ArrayList<String>()

        def nodeModulesDir = project.file("node_modules")

        final String exec = resolveCommand(nodeModulesDir, baseCmd)
        if (exec == null) {
            throw new RuntimeException((String) "Cannot find " + baseCmd + ".  Is it npm installed?")
        }

        commandLine.add(exec)

        if (baseArgs != null) {
            commandLine.addAll(baseArgs)
        }

        if (npmModule != null) {
            commandLine.addAll(npmModule.split())
        }

        if (argsSuffix != null) {
            commandLine.addAll(argsSuffix)
        }
        return commandLine
    }

    File resolveCommand(final File nodeModulesDir, final String command) {
        final String name
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            name = "${command}.cmd"
        } else {
            name = command
        }

        final File absoluteFile = new File(name)
        if (absoluteFile.exists()) {
            return absoluteFile
        }

        final File globalFile = new File(this.bin, name)
        if (globalFile.exists()) {
            return globalFile
        }


        final File localFile = new File(nodeModulesDir.toString() + File.separator + ".bin", name)
        if (localFile.exists()) {
            return localFile
        }

        return null
    }


    private static class NodeJsTarXzUnArchiver extends TarXZUnArchiver {

        @Override
        protected void extractFile(File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory, Integer mode, String symlinkDestination, FileMapper[] fileMappers) throws IOException, ArchiverException {
            final pos = entryName.indexOf("/")
            if (pos != -1) {
                super.extractFile(srcF, dir, compressedInputStream, entryName.substring(pos + 1), entryDate, isDirectory, mode, symlinkDestination)
            }
        }

    }

    private static class NodeJsTarGzUnArchiver extends TarGZipUnArchiver {

        @Override
        protected void extractFile(File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory, Integer mode, String symlinkDestination, FileMapper[] fileMappers) throws IOException, ArchiverException {
            final pos = entryName.indexOf("/")
            if (pos != -1) {
                super.extractFile(srcF, dir, compressedInputStream, entryName.substring(pos + 1), entryDate, isDirectory, mode, symlinkDestination)
            }
        }

    }

    private static class NodeJsZipUnArchiver extends ZipUnArchiver {

        @Override
        protected void extractFile(File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory, Integer mode, String symlinkDestination, FileMapper[] fileMappers) throws IOException, ArchiverException {
            final pos = entryName.indexOf("/")
            if (pos != -1 && pos < entryName.length() - 1) {
                super.extractFile(srcF, dir, compressedInputStream, entryName.substring(pos + 1), entryDate, isDirectory, mode, symlinkDestination)
            }
        }

    }
}
