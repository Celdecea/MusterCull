package com.untamedears.mustercull;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

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
	 * @param event A reference to the associated event.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		Entity entity = event.getEntity();
		
		ConfigurationLimit limit = this.getPluginInstance().getLimit(entity);
		
		if (limit == null || limit.range <= 0) {
			return;
		}
		
		if (limit.limit <= 0) {
			notifyRemoval(entity);
			event.setCancelled(true);
			return;
		}
		
		int count = 0;
		
		for (Entity otherEntity : entity.getNearbyEntities(limit.range, limit.range, limit.range)) {
			if (0 == otherEntity.getType().compareTo(entity.getType())) {
				count += 1;
				
				if (count >= limit.limit) {
					notifyRemoval(entity);
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	
	/**
	 * Displays a notification method when a mob is removed if enabled.
	 * @param entity A reference to the Bukkit entity where status information is coming from.
	 */
	public void notifyRemoval(Entity entity) {
		//TODO: Only show this based on a configuration setting.
		System.out.println("Removing " + entity.getType().toString() + " from the world near " + entity.getLocation().toString());
	}
}
