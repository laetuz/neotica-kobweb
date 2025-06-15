# Alexandria Website

[Alexandria](https://alexandrianeotica.id/) 

This website is built using [Kotlin](https://kotlinlang.org/), [Kobweb](https://github.com/varabyte/kobweb), and [Compose for Web](https://compose-web.ui.jetbrains.org/).

## Running the Project

This project uses [Gradle](https://gradle.org/) as its build tool. To run the development server, use the following command:

```bash
./gradlew site:jsRun
```

This will start a local server, and you can view the website in your browser, typically at `http://localhost:8080`.

## Project Structure

- `site/src/jsMain/kotlin`: Contains the Kotlin source code for the website, built with Kobweb and Compose for Web.
  - `id/neotica/neotica/pages`: Defines the different pages of the website.
  - `id/neotica/neotica/components`: Contains reusable UI components.
  - `id/neotica/neotica/AppEntry.kt`: The main entry point for the Kobweb application.
- `site/src/jsMain/resources`: Contains static resources like images and fonts.
- `build.gradle.kts` and `settings.gradle.kts`: Gradle build configuration files.

## Deploying the Project

To deploy the website, you need to generate the static files. You can do this using the following Gradle command:

```bash
./gradlew site:jsExport
```

This command will build the project and output the static files to the `site/build/kobweb/export` directory. You can then deploy these files to any static web hosting service.