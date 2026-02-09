# gwt-gradle

Gradle plugins for GWT (Google Web Toolkit) projects with optional npm/webpack integration.

Requires Java 11+ and Gradle 7.x - 9.x.

Examples can be found at https://github.com/ascendtech/gwt-examples

## Plugins

| Plugin ID | Description |
|---|---|
| `us.ascendtech.gwt.modern` | GWT projects with webpack/npm frontend. Compiles to archive for deploy. |
| `us.ascendtech.gwt.classic` | Traditional GWT projects. Compiles to war. |
| `us.ascendtech.gwt.lib` | GWT library projects shared across modules. |
| `us.ascendtech.gwt.dep` | Non-GWT modules that should be included in GWT compilation. |
| `us.ascendtech.js.npm` | Standalone npm/webpack support for Gradle. |

## gwt.modern

A plugin for GWT projects using webpack and npm.

### Basic Usage

build.gradle
```gradle
plugins {
    id "us.ascendtech.gwt.modern" version "0.12.2"
}

gwt {
    modules = ['com.company.SomeModule']
}
```

### With a GWT library project

```gradle
// gwt lib build.gradle
plugins {
    id "us.ascendtech.gwt.lib" version "0.12.2"
}

// app build.gradle
plugins {
    id "us.ascendtech.gwt.modern" version "0.12.2"
}

gwt {
    modules = ['com.company.SomeModule']
}

dependencies {
    implementation project(':someGwtLibProject')
}
```

### Tasks

Compile to tar.gz bundle for deploy on web server:
```bash
gradle gwtArchive  # archive in build/webapp
```

Run GWT dev mode with webpack backend:
```bash
gradle gwtDev

# new terminal
gradle webpack5Dev

# if using annotation processing (autorest, vue-gwt, simplerest)
# new terminal
gradle compileJava --build-cache -t compileJava
```

## gwt.classic

A plugin for traditional GWT projects. Compiles to war.

### Basic Usage

build.gradle
```gradle
plugins {
    id "us.ascendtech.gwt.classic" version "0.12.2"
}

gwt {
    modules = ['com.company.SomeModule']
}
```

### With a GWT library project

```gradle
// gwt lib build.gradle
plugins {
    id "us.ascendtech.gwt.lib" version "0.12.2"
}

// app build.gradle
plugins {
    id "us.ascendtech.gwt.classic" version "0.12.2"
}

gwt {
    modules = ['com.company.SomeModule']
}

dependencies {
    implementation project(':someGwtLibProject')
}
```

### Tasks

Create war:
```bash
gradle war  # war file in build/libs
```

Run GWT dev mode (Super Dev Mode):
```bash
gradle gwtDev
```

## gwt {} Extension Options

| Option | Default | Description |
|---|---|---|
| `gwtVersion` | `"2.12.2"` | GWT SDK version |
| `modules` | `[]` | List of GWT modules to compile or run |
| `libs` | `[]` | Shorthand library dependencies (see below) |
| `style` | `"OBFUSCATED"` | Script output style: `DETAILED`, `OBFUSCATED`, or `PRETTY` |
| `logLevel` | `"INFO"` | Logging detail: `ERROR`, `WARN`, `INFO`, `TRACE`, `DEBUG`, `SPAM`, or `ALL` |
| `localWorkers` | available processors | Number of local workers for compiling permutations |
| `proxy` | `"http://localhost:8080"` | Webpack backend address (modern plugin) |
| `includeGwtUser` | `true` | Include gwt-user on the classpath |
| `extra` | `true` | Produce GWT "extra" output directory |
| `forceRecompile` | `true` | Force recompilation (disable up-to-date checks) |
| `sourceLevel` | `"17"` | Java source level for GWT compilation |
| `incremental` | `false` | Enable incremental compilation |
| `persistentunitcache` | `true` | Enable GWT persistent unit cache |

### Available libs

Use the `libs` option to add common GWT library dependencies:

```gradle
gwt {
    libs = ["elemento-core", "simplerest"]
}
```

| Lib name | Library |
|---|---|
| `elemento-core` | [Elemento](https://github.com/hal/elemento) 1.6.11 |
| `elemento-core-legacy` | Elemento 0.9.6 (gwt2 variant when `includeGwtUser` is true) |
| `vue` | [Vue GWT](https://github.com/AXELLience/vue-gwt) 1.0.1 (includes annotation processor) |
| `autorest` | [AutoREST](https://github.com/intendia-oss/autorest) 0.11 (includes annotation processor) |
| `simplerest` | SimpleREST 0.6.0 (includes annotation processor) |
| `ast-highcharts` | [GWT Highcharts](https://github.com/AscendTech/gwt-highcharts) 1.3.3 |
| `ast-aggrid` | AG Grid GWT bindings 0.3.2 |
| `ast-momentjs` | Moment.js GWT bindings 0.4.0 |
| `ast-wordcloud` | WordCloud2.js GWT bindings 2.1.0 |
| `core` | org.gwtproject.core:gwt-core 1.0.0-RC2 |
| `event` | org.gwtproject.event:gwt-event 1.0.0-RC1 |
| `places` | org.gwtproject.places:gwt-places 1.0.0-RC1 |
| `history` | org.gwtproject.user.history:gwt-history 1.0.0-RC1 |
| `timer` | org.gwtproject.timer:gwt-timer 1.0.0-RC1 |

## gwt.lib

A plugin for GWT library projects. Apply this to modules that contain GWT source to be shared across projects. The `gwt.modern` and `gwt.classic` plugins automatically apply this.

```gradle
plugins {
    id "us.ascendtech.gwt.lib" version "0.12.2"
}

gwt {
    libs = ["elemento-core"]
}
```

Library dependencies (both `libs` and npm `dependencies`) are automatically propagated to dependent projects.

## gwt.dep

A plugin for modules that should be included in GWT compilation but that aren't GWT modules themselves.

```gradle
plugins {
    id "us.ascendtech.gwt.dep" version "0.12.2"
}
```

## npm-gradle

A plugin that downloads and runs npm and webpack. NodeJS is downloaded to `~/.nodejs/<version>/`. Supports Linux, macOS, and Windows on x64 and ARM64 architectures.

### Basic Usage

build.gradle
```gradle
plugins {
    id "us.ascendtech.js.npm" version "0.12.2"
}

// all optional, defaults shown
npm {
    nodeJsVersion = "16.13.0"
    webpackInputBase = "./src/main/webapp/"
    contentBase = "./src/main/webapp/public/"
}
```

### npm {} Extension Options

| Option | Default | Description |
|---|---|---|
| `nodeJsVersion` | `"16.13.0"` | Node.js version to download and use |
| `webpackInputBase` | `"./src/main/webapp/"` | Webpack input directory (used for up-to-date checks) |
| `contentBase` | `"./src/main/webapp/public/"` | Content base for `webpack5LegacyDev` task |
| `webpackOutputBase` | `"build/js"` | Webpack output directory (not configurable) |
| `dependencies` | `[]` | npm dependencies to install via Gradle |
| `devDependencies` | `[]` | npm dev dependencies to install via Gradle |

### Managing npm dependencies from Gradle

```gradle
npm {
    dependencies = ["vue@2.7.16", "vuetify@2.7.2"]
    devDependencies = ["webpack@5.90.0", "webpack-cli@5.1.4"]
}
```

### Custom NpmTask (Kotlin DSL)

```kotlin
tasks.register<us.ascendtech.js.npm.NpmTask>("npmAuditFix") {
    dependsOn("npmInstallDep", "npmInstall")
    baseCmd.set("npm")
    baseArgs.addAll("audit", "fix")
}
```

### NPM Tasks

```bash
gradle npmClean            # rm -rf node_modules
gradle npmInstall          # npm install
gradle npmInstallSave --npmModule vue      # npm install vue --save
gradle npmInstallSaveDev --npmModule vue   # npm install vue --save-dev

gradle webpack             # webpack-cli --mode=production --output-path build/js
gradle webpack5Dev         # webpack serve --mode=development
gradle webpack5LegacyDev  # webpack serve --mode=development --content-base <contentBase>
```

### Sample webpack.config.js

```js
const path = require('path');

module.exports = {
    resolve: {
        extensions: ['*', '.js', '.json']
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            }
        ]
    },
    entry: {
        app: ["./src/main/webapp/js/index.js"]
    },
    output: {
        filename: "bundle.js"
    },
    devServer: {
        port: 8080,
        static: {
            directory: path.resolve(__dirname, "src", "main", "webapp", "public"),
            publicPath: "/",
            serveIndex: true,
            watch: true,
        },
        proxy: {
            '/api': {
                target: 'http://localhost:12111',
                ws: true,
                changeOrigin: true
            }
        }
    }
}
```

## License

Apache 2.0
