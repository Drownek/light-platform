package me.drownek.example;

import com.velocitypowered.api.plugin.Plugin;
import eu.okaeri.platform.velocity.LightVelocityPlugin;
import me.drownek.platform.core.annotation.Scan;
import me.drownek.platform.core.plan.ExecutionPhase;
import me.drownek.platform.core.plan.Planned;

@Plugin(
	id = "velocityplugin",
	name = "VelocityPlugin",
	description = "A Velocity Plugin",
	version = "1.0.0",
	authors = { "Drownek" }
)
@Scan(deep = true, exclusions = "me.drownek.example.libs")
public final class VelocityPlugin extends LightVelocityPlugin {
	@Planned(ExecutionPhase.STARTUP)
	void startup() {
		log("Hello!");
	}
}