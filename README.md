# SchulScheduler
SchulScheduler ist eine Anwendung zur Berechnung von Stundenplänen für Grundschulen und weiterführende Schulen.
Die Anwendung wurde ursprünglich im Rahmen eines Studienprojekts an der Universität Stuttgart entwickelt.
Dieses GitHub Repository enthält einen Fork des [ursprünglichen Repository](https://bitbucket.org/Philipp91/schulscheduler), basierend auf Gradle, JavaFX 13 und ohne Xtend.

## Development Environment

### Java and JavaFX

Install OpenJDK 13+ and JavaFX 13+ (following [this guide](https://openjfx.io/openjfx-docs/#install-java)).
This essentially means downloading the zipped distributions from http://jdk.java.net/ and https://gluonhq.com/products/javafx/, unzipping them to appropriate locations and pointing the `JAVA_HOME`/`PATH_TO_FX` environment variables there (for JavaFX it might be the `lib/` subdirectory).
Also make sure that `$JAVA_HOME/bin` is on the `PATH`.
See [this tutorial](https://java.tutorials24x7.com/blog/how-to-install-openjdk-13-on-ubuntu) for linux command line instructions.
In case of WSL, be sure to use `/etc/profile` instead of `~/.bashrc` so that Windows/MinGW does not see the changes.

### Gradle

Install Gradle as described [here](https://gradle.org/install/), e.g. through SDKMAN!

### IntelliJ

This should work with the free Community version.

### SCIP

The instructions below are for building SCIP from source.
It might be possibile to download and install the platform-specific SCIP binaries instead, especially when JSCIPOpt doesn't have to be built anymore but can be used as a jar file.

Download `scipoptsuite-x.y.z.tgz` from [here](https://scip.zib.de/index.php#download) and extract it to `./scip/scipoptsuite`.
Follow these installation [instructions](https://github.com/SCIP-Interfaces/JSCIPOpt/blob/6855f7025d12504bda6556f264caa36f93fb427b/INSTALL.md).
That is, first build the shared library in the downloaded `scipoptsuite` directory, then download and build JSCIPOpt (on WSL if host is Windows):
```
cd .../schulscheduler/scip
git clone https://github.com/SCIP-Interfaces/JSCIPOpt.git
cd JSCIPOpt

mkdir build
cd build
cmake .. -DSCIP_DIR="$(realpath ../..)/scipoptsuite/build"
make

# Check if it works
cd Release
java -cp scip.jar:examples.jar Quadratic
```

If on Windows, additionally do this in PowerShell after installing [CMake](https://cmake.org/download/) and possible some MSVC stuff:
```
cd ...\schulscheduler\scip\scipoptsuite
cmake -G "Visual Studio 14 2015 Win64" -S. -Bbuild-windows
cmake --build build-windows --config Release
# This may produce many compiler warnings.

cd ...\schulscheduler\scip\JSCIPOpt
mkdir build-windows
cd build-windows
cmake .. -G "Visual Studio 14 2015 Win64" -DSCIP_DIR=C:\...\schulscheduler\scip\scipoptsuite\build-windows
cmake --build . --config Release

# Check if it works (after this, close your shell to have a cleaner PATH again)
$env:Path +=";C:\Users\Philipp\repos\schulscheduler\scip\scipoptsuite\build-windows\bin\Release"
cd Release
java -cp "scip.jar;examples.jar" Quadratic
```
