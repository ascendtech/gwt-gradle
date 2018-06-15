# gwt-gradle

This is intended for use with modern GWT projects.  Currently it aims to support projects that will 
be ready for GWT 3.x.

NPM/Webpack has been adapted from work by https://github.com/solugo/gradle-nodejs-plugin


## Basic Usage


build.gradle
```gradle

plugins {
    id "us.ascendtech.js.gwt" version "0.1.4"
}

gwt {
    modules = ['com.company.SomeModule']   
}

```

Compile to tar gz bundle for deploy on web server
```bash
gradle gwtArchive
```

Run gwt devmode with webpack backend
```
gradlew gwtDev

#new terminal
gradlew webpackDev

#if using annotation processing (autorest or vue-gwt)
#new terminal
gradle compileJava -t
```

