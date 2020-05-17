package us.ascendtech.gwt.common

/**
 * @author Matt Davis
 * @author Luc Girardin
 * Apache 2.0 License
 */
class GWTExtension {

    String gwtVersion = "2.9.0"

    //list of modules to compile or run
    Collection<String> modules = []

    //list of libs to include
    // current options "vue", "autorest", "elemento-core", "ast-highcharts"
    // vue is 1.0-beta-9 (https://github.com/VueGWT/vue-gwt)
    // autorest is 0.9 (https://github.com/intendia-oss/autorest)
    // elemento-core is 0.9.0 or 0.9.0-gwt2 depending on the includeGwtUser flag (https://github.com/hal/elemento)
    // ast-highcharts is 1.1.0 (https://github.com/ascendtech/gwt-highcharts)
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

    //Produce "extra" directory
    boolean extra = true

    //Force recompilation
    boolean forceRecompile = true

    boolean incremental = false
    boolean persistentunitcache = true
}
