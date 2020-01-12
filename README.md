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

## Gradle

Install Gradle as described [here](https://gradle.org/install/), e.g. through SDKMAN!

## IntelliJ

This should work with the free Community version.
