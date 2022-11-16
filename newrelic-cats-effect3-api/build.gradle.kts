import com.nr.builder.publish.PublishConfig

plugins {
    `maven-publish`
    signing
    id("com.github.prokod.gradle-crossbuild-scala" )

}
evaluationDependsOn(":newrelic-api")

crossBuild {
    scalaVersionsCatalog = mapOf("2.12" to "2.12.13", "2.13" to "2.13.10")
    builds {
        register("scala") {
            scalaVersions = setOf("2.12", "2.13")
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation("org.scala-lang:scala-library:2.13.10")
    implementation("org.typelevel:cats-effect_2.13:3.3.5")
    implementation(project(":newrelic-api"))
    testImplementation(project(":instrumentation-test"))
    testImplementation(project(path = ":newrelic-agent", configuration = "tests"))
}

val crossBuildScala_212Jar by tasks.getting
val crossBuildScala_213Jar by tasks.getting

val javadocJar by tasks.getting
val sourcesJar by tasks.getting

mapOf(
    "2.12" to crossBuildScala_212Jar,
    "2.13" to crossBuildScala_213Jar
).forEach { (scalaVersion, versionedClassJar) ->
    PublishConfig.config(
        "crossBuildScala_${scalaVersion.replace(".", "")}",
        project,
        "New Relic Java agent Scala $scalaVersion Cats effect API",
        "The public Scala $scalaVersion API of the Java agent for Cats effect."
    ) {
        artifact(sourcesJar)
        artifact(javadocJar)
        artifact(versionedClassJar)
    }
}

tasks {
    //functional test setup here until scala 2.13 able to be used in functional test project
    test {
        dependsOn("jar")
        setForkEvery(1)
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        minHeapSize = "256m"
        maxHeapSize = "768m"
        val functionalTestArgs = listOf(
            "-javaagent:${com.nr.builder.JarUtil.getNewRelicJar(project(":newrelic-agent")).absolutePath}",
            "-Dnewrelic.config.file=${project(":newrelic-agent").projectDir}/src/test/resources/com/newrelic/agent/config/newrelic.yml",
            "-Dnewrelic.unittest=true",
            "-Dnewrelic.debug=true",
            "-Dnewrelic.sync_startup=true",
            "-Dnewrelic.send_data_on_exit=true",
            "-Dnewrelic.send_data_on_exit_threshold=0",
            "-Dnewrelic.config.log_level=finest",
            "-Dnewrelic.config.startup_log_level=warn",
            "-Dcats.effect.tracing.mode=full",
            "-Dcats.effect.tracing.buffer.size=1024"
        )
        jvmArgs(functionalTestArgs + "-Dnewrelic.config.extensions.dir=${projectDir}/src/test/resources/xml_files")
    }
    //no scaladoc jar task, instead this work around makes scaladoc destination folder javadoc to ensure included in jar
    javadoc {
        dependsOn("scaladoc")
    }
    scaladoc {
        val javadocDir = (
                destinationDir.absolutePath
                    .split("/")
                    .dropLast(1)
                    .plus("javadoc")
                ).joinToString("/")
        destinationDir = File(javadocDir)
    }
}

