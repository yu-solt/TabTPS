plugins {
  id("fabric-loom") version "0.6-SNAPSHOT"
  id("com.github.johnrengelman.shadow")
}

val shade: Configuration by configurations.creating

val mcVersion = "1.16.5"

dependencies {
  minecraft("com.mojang", "minecraft", mcVersion)
  mappings(minecraft.officialMojangMappings())
  modImplementation("net.fabricmc", "fabric-loader", "0.11.1")
  modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.29.4+1.16")

  shade(implementation(project(":tabtps-common")) {
    exclude("cloud.commandframework")
    exclude("net.kyori")
    exclude("org.slf4j")
  })

  val cloudVersion = "1.5.0-SNAPSHOT"
  modImplementation(include("cloud.commandframework", "cloud-fabric", cloudVersion))
  implementation(include("cloud.commandframework", "cloud-minecraft-extras", cloudVersion))

  modImplementation(include("net.kyori", "adventure-platform-fabric", "4.0.0-SNAPSHOT"))
  implementation(include("net.kyori", "adventure-text-feature-pagination", "4.0.0-SNAPSHOT"))
  implementation(include("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT"))
  val adventureVersion = "4.5.1"
  implementation(include("net.kyori", "adventure-text-serializer-legacy", adventureVersion))
  shade(implementation("net.kyori", "adventure-serializer-configurate4", adventureVersion) {
    exclude("*")
  })

  implementation(include("org.slf4j", "slf4j-api", "1.7.30"))
  implementation(include("org.apache.logging.log4j", "log4j-slf4j-impl", "2.8.1"))
}

tasks {
  shadowJar {
    configurations = listOf(shade)
    from(rootProject.projectDir.resolve("license.txt"))
    minimize()
    listOf(
      "net.kyori.adventure.serializer.configurate4",
      "org.apache.logging",
      "org.spongepowered.configurate",
      "com.typesafe.config",
      "org.checkerframework"
    ).forEach { pkg ->
      relocate(pkg, "${rootProject.group}.${rootProject.name.toLowerCase()}.lib.$pkg")
    }
    dependencies {
      exclude { it.moduleGroup.contains("leangen") } // provided by cloud-platform-fabric
    }
  }
  remapJar {
    dependsOn(shadowJar)
    archiveClassifier.set("")
    archiveFileName.set("${project.name}-mc$mcVersion-${project.version}.jar")
    destinationDirectory.set(rootProject.rootDir.resolve("build").resolve("libs"))
    input.set(shadowJar.get().outputs.files.singleFile)
  }
  processResources {
    filesMatching("fabric.mod.json") {
      mapOf(
        "{project.name}" to project.name,
        "{rootProject.name}" to rootProject.name,
        "{version}" to version.toString(),
        "{description}" to project.description
      ).entries.forEach { (k, v) -> filter { it.replace(k, v as String) } }
    }
  }
}