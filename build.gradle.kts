plugins {
	`java-gradle-plugin`
	`maven-publish`
}

version = "0.1"
group = "net.pandadev.multiloader"

gradlePlugin {
	plugins {
		register("multiloader") {
			id = "net.pandadev.multiloader"
			implementationClass = "net.pandadev.multiloader.Multiloader"
		}
	}
}

repositories {
	gradlePluginPortal()

	maven("https://repo.spongepowered.org/repository/maven-public/")
	maven("https://maven.fabricmc.net/")
	maven("https://maven.minecraftforge.net/")
	maven("https://maven.neoforged.net/releases/")
}

dependencies {
	gradleApi()
	localGroovy()
	implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.0")

	implementation("org.spongepowered.gradle.vanilla:org.spongepowered.gradle.vanilla.gradle.plugin:0.2.1-SNAPSHOT")
	implementation("fabric-loom:fabric-loom.gradle.plugin:1.7-SNAPSHOT")
	implementation("net.minecraftforge.gradle:net.minecraftforge.gradle.gradle.plugin:6.0.+")
	implementation("net.neoforged.moddev:net.neoforged.moddev.gradle.plugin:2.0.+")

	implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.1.8")
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = "net.pandadev"
			artifactId = "multiloader"
			version = project.version as String

			from(components["java"])
		}
	}
}