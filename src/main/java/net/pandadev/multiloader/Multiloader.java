/*
 * Copyright (C) 2024 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.pandadev.multiloader;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import net.minecraftforge.gradle.userdev.UserDevPlugin;
import net.pandadev.multiloader.utils.PlatformUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.language.jvm.tasks.ProcessResources;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Multiloader implements Plugin<Project> {
	private Configuration embedConfiguration;
	private Configuration modEmbedConfiguration;

	@Override
	public void apply(@NotNull Project project) {
		project.getPluginManager().apply("java");

		configuration(project);

		setupShadowJar(project);
		processResources(project);
	}

	public void setupShadowJar(Project project) {
		project.getPluginManager().apply("com.gradleup.shadow");

		project.getTasks().withType(ShadowJar.class).configureEach(shadowJar -> {
			shadowJar.setConfigurations(List.of(embedConfiguration));
			shadowJar.getArchiveClassifier().set("dev-shadow");
		});
	}

	public void processResources(Project project) {
		ProcessResources processResources = (ProcessResources) project.getTasks().getByName("processResources");

		Properties properties = new Properties();
		try (FileInputStream inputStream = new FileInputStream(project.getRootDir() + "/gradle.properties")) {
			properties.load(inputStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Map<String, String> propertyMap = new HashMap<>();
		properties.forEach((key, value) -> propertyMap.put(((String) key).replace(".", "_"), (String) value));

		processResources.getInputs().properties(propertyMap);
		processResources.filesMatching(
				List.of("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "*.mixins.json"),
				(details) -> details.expand(propertyMap)
		);
	}

	public void configuration(Project project) {
		ConfigurationContainer configurations = project.getConfigurations();
		embedConfiguration = configurations.create("embed");
		modEmbedConfiguration = configurations.create("modEmbed");

		project.getConfigurations().named("implementation").get().extendsFrom(embedConfiguration);

		PlatformUtils.runOnFabric(project, javaPlugin -> {
			project.getConfigurations().named("modImplementation").get().extendsFrom(getModEmbedConfiguration());
			project.getConfigurations().named("include").get().extendsFrom(getModEmbedConfiguration());
		});

		PlatformUtils.runOnForge(project, javaPlugin -> {
			project.getConfigurations().getByName(UserDevPlugin.OBF).extendsFrom(getModEmbedConfiguration());
			project.getConfigurations().named("implementation").get().extendsFrom(getModEmbedConfiguration());
			project.getConfigurations().named("jarJar").get().extendsFrom(getModEmbedConfiguration());
		});

		PlatformUtils.runOnNeoForge(project, javaPlugin -> {
			project.getConfigurations().named("implementation").get().extendsFrom(getModEmbedConfiguration());
			project.getConfigurations().named("jarJar").get().extendsFrom(getModEmbedConfiguration());
		});
	}

	public Configuration getEmbedConfiguration() {
		return embedConfiguration;
	}

	public Configuration getModEmbedConfiguration() {
		return modEmbedConfiguration;
	}
}
