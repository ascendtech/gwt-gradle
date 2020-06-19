
See below for npm plugin

# gwt.modern
A plugin for GWT 2.9.x projects using webpack and npm.  Eventually will support J2CL / GWT 3.x. Compiles to archive file for deploy on web server.

Work in progess examples can be found https://github.com/ascendtech/gwt-examples

## Basic Usage


build.gradle
```gradle

plugins {
  id "us.ascendtech.gwt.modern" version "0.5.1"
}

gwt {
    modules = ['com.company.SomeModule']   
}

```

gwt lib
```gradle

//gwt lib build.gradle
plugins {
    id "us.ascendtech.gwt.lib" version "0.5.1"
}

//app build.gradle
plugins {
  id "us.ascendtech.gwt.modern" version "0.5.1"
}
gwt {
    modules = ['com.company.SomeModule']   
}

dependencies {
    compile project(':someGwtLibProject')   
}

```


Compile to tar gz bundle for deploy on web server
```bash
gradle gwtArchive #archive in build/webapp
```

Run gwt devmode with webpack backend
```
gradlew gwtDev

#new terminal
gradlew webpackDev

#if using annotation processing (autorest or vue-gwt)
#new terminal
gradle compileJava —build-cache -t compileJava
```


# gwt.classic

A plugin for GWT 2.8 projects.  Compiles to war

## Basic Usage


build.gradle
```gradle

plugins {
  id "us.ascendtech.gwt.classic" version "0.5.1"
}

gwt {
    modules = ['com.company.SomeModule']   
}

```

gwt lib
```gradle

//gwt lib build.gradle
plugins {
    id "us.ascendtech.gwt.lib" version "0.5.1"
}

//app build.gradle
plugins {
  id "us.ascendtech.gwt.classic" version "0.5.1"
}
gwt {
    modules = ['com.company.SomeModule']   
}

dependencies {
    compile project(':someGwtLibProject')   
}

```

Create war
```bash
gradle war #war file in build/libs
```

Run gwt devmode
```
gradlew gwtDev
```

# npm-gradle 
A plugin that downloads and runs NPM and webpack.  Based on the work of https://github.com/solugo/gradle-nodejs-plugin.  NodeJS is downloaded to ~/.nodejs/version/.

NPM

build.gradle
```gradle
plugins {
    id "us.ascendtech.js.npm" version "0.5.1"
}

//all optional, defaults shown
npm {
   nodeJsVersion = "12.1.0"
   webpackInputBase = "./src/main/webapp/"
   contentBase = "./src/main/webapp/public/"
}

```

sample package.json
```js
{
  "name": "somename",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {},
  "keywords": [],
  "author": "",
  "license": "",
  "devDependencies": {
    "css-loader": "^0.28.11",
    "style-loader": "^0.21.0",
    "webpack": "^4.12.0",
    "webpack-cli": "^2.1.4",
    "webpack-dev-server": "^3.1.4"
  },
  "dependencies": {
    "ag-grid": "^17.1.1",
    "ag-grid-vue": "^17.1.0",
    "npm": "^6.1.0",
    "vue": "^2.5.16",
    "vue-router": "^3.0.1",
    "vuetify": "^1.0.18"
  }
}
```

sample webpack.config.js
```js
module.exports = {
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        },
        extensions: ['*', '.js', '.vue', '.json']
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader'
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
        proxy: {
            '/someapi': {
                target: 'http://localhost:12111',
                ws: true,
                changeOrigin: true
            },
            '/login': {
                target: 'http://localhost:12222',
                ws: true,
                changeOrigin: true
            }
        }
    }
}
```


NPM tasks
```bash
gradle npmClean #rm -rf node_modules

gradle npmInstall #npm install

gradle npmInstallSave --npmModule vue #npm install vue --save
gradle npmInstallSaveDev --npmModule vue #npm install vue --save-dev

gradle webpack #webpack-cli --mode=production --output-path build/js
gradle webpackDev #webpack-dev-server --mode development --content-base ${npm.contentBase}

