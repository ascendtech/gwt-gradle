package us.ascendtech.js.gwt

/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTExtension {

    //2.8.2-rx1 is for rx-java, can be removed after next gwt 2.8.x that contains the patch to
    //JDT (https://bugs.eclipse.org/bugs/show_bug.cgi?id=521438)
    String gwtVersion = "2.8.2-rx1"

    Collection<String> modules = []

    //Script output style: DETAILED, OBFUSCATED or PRETTY (defaults to OBFUSCATED)
    String style = "OBFUSCATED"

    //The level of logging detail: ERROR, WARN, INFO, TRACE, DEBUG, SPAM or ALL (defaults to INFO)
    String logLevel = 'INFO'

    //The number of local workers to use when compiling permutations
    int localWorkers = Runtime.getRuntime().availableProcessors()

    String proxy = "http://localhost:8080"
}
