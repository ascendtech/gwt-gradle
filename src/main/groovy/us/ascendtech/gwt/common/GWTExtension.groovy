package us.ascendtech.gwt.common

/**
 * @author Matt Davis
 * Apache 2.0 License
 */
class GWTExtension {

    //2.8.2-rx1 is for rx-java, can be removed after next gwt 2.8.x that contains the patch to
    //JDT (https://bugs.eclipse.org/bugs/show_bug.cgi?id=521438)
    String gwtVersion = "2.8.2-rx1"

    //list of modules to compile or run
    Collection<String> modules = []

    //list of libs to include
    // current options "vue", "autorest", "elemento-core", "ast-highcharts"
    // vue is 1.0-beta-9 (https://github.com/VueGWT/vue-gwt)
    // autorest is 0.9 (https://github.com/intendia-oss/autorest)
    // elemento-core is 0.8.7 or 0.8.7-gwt2 depending on the includeGwtUser flag (https://github.com/hal/elemento)
    // ast-highcharts is 1.0.0 (https://github.com/ascendtech/gwt-highcharts)
    Collection<String> libs = []

    //Script output style: DETAILED, OBFUSCATED or PRETTY (defaults to OBFUSCATED)
    String style = "OBFUSCATED"

    //The level of logging detail: ERROR, WARN, INFO, TRACE, DEBUG, SPAM or ALL (defaults to INFO)
    String logLevel = 'INFO'

    //The number of local workers to use when compiling permutations
    int localWorkers = Runtime.getRuntime().availableProcessors()

    //webpack backend address when using with webpack
    String proxy = "http://localhost:8080"

    //include gwt user
    boolean includeGwtUser = true
}
