package com.untamedears.mustercull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * This class provides event handlers for game entities.
 * @author Celdecea
 *
 */
public class EntityListener extends Listener {

	/**
	 * This constructor wraps the parent Listener's constructor.
	 * @param pluginInstance A reference to the plug-in instance.
	 */
	public EntityListener(MusterCull pluginInstance) {
		super(pluginInstance);
	}

	/**
	 * This handler is called when an entity is spawning.
	 * @param event A reference to the associated Bukkit event.
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		/* TODO Will this prevent player spawning? */
		/* If over hard mob limit, stop all mob spawning.
		 * This entity isn't included in overHardMobLimit so account for this. */
		if (this.getPluginInstance().overHardMobLimit() + 1 > 0) {
			/* Always let a player join. */
			if (! (event.getEntity() instanceof Player)) {
				event.setCancelled(true);
				return;
			}
		}
		
		Entity entity = event.getEntity();
		ConfigurationLimit limit = null;
		
		if (!this.getPluginInstance().isPaused(CullType.SPAWNER)) {
			if (event.getSpawnReason() == SpawnReason.SPAWNER) {
				
				limit = this.getPluginInstance().getLimit(entity.getType(), CullType.SPAWNER);
				
				if (limit != null) {
					event.setCancelled(this.getPluginInstance().runEntityChecks(entity, limit));
					return;
				}	
			}
		}
		
		if (!this.getPluginInstance().isPaused(CullType.SPAWN)) {
			if (event.getSpawnReason() != SpawnReason.SPAWNER) {
				limit = this.getPluginInstance().getLimit(entity.getType(), CullType.SPAWN);
				
				if (limit != null) {
					event.setCancelled(this.getPluginInstance().runEntityChecks(entity, limit));
					return;
				}
			}
		}
	}
	
}
