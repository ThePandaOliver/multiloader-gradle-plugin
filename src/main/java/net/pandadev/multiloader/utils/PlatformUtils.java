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

package net.pandadev.multiloader.utils;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.plugins.AppliedPlugin;

public class PlatformUtils {
	public static void runOnFabric(Project project, Action<AppliedPlugin> action) {
		project.getPluginManager().withPlugin("fabric-loom", action);
	}

	public static void runOnForge(Project project, Action<AppliedPlugin> action) {
		project.getPluginManager().withPlugin("net.minecraftforge.gradle", action);
	}

	public static void runOnNeoForge(Project project, Action<AppliedPlugin> action) {
		project.getPluginManager().withPlugin("net.neoforged.moddev", action);
	}
}
