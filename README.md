# quarkus-native-app-starter

This project uses Quarkus command-mode to showcase the possibilities of packaging a native executable for an Electron-style Desktop application (frontend is a webapp running in your browser) and building/releasing it via Github Actions.

For details read the related blog-post on [quarkus.io/blog](https://quarkus.io/blog/).

## Building the application

To build build the native executable you need to have GraalVM installed.

For details how to build on your OS, you may also check the build-jobs under [.github/workflows]](../blob/master/.github/workflows). It contains the prerequisits and commands to build for Linux, Mac and Windows.

Build the native executable with

```
./mvnw package -Dnative
```

## Running the application

Run the native executable with webapp. You might have to provide your Chrome executable path as there is a bug in native-mode when using `Desktop.getDesktop().browse(...)`.

```
target/quarkus-ls-1.0.0-SNAPSHOT-runner -Dls.output=web -Dls.chrome.exe=/usr/bin/google-chrome
```
The application should start chrome window and display the packaged webapp via opening the [index.html](../blob/master/src/main/resources/META-INF/resources/index.html).