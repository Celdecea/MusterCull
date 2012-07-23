package com.untamedears.mustercull;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

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
	@EventHandler(priority = EventPriority.HIGH)
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		// Grab a configured limit for event entity
		Entity entity = event.getEntity();
		ConfigurationLimit limit = this.getPluginInstance().getLimit(entity);
		
		if (limit == null) {
			return;
		}
		else if (limit.culling == CullType.DAMAGE) {
			this.getPluginInstance().addEntity(entity);
		}
		else if (limit.culling == CullType.SPAWN) {
			 
			if (limit.range <= 0) {
				return;
			}
			 
			// If the limit is 0, prevent all of this entity type from spawning 
			if (limit.limit <= 0) {
				notifyRemoval(entity);
				event.setCancelled(true);
				return;
			}
			
			// Loop through entities in range and count similar entities.
			int count = 0;
			
			for (Entity otherEntity : entity.getNearbyEntities(limit.range, limit.range, limit.range)) {
				if (0 == otherEntity.getType().compareTo(entity.getType())) {
					count += 1;
					
					// If we've reached a limit for this entity, prevent it from spawning.
					if (count >= limit.limit) {
						notifyRemoval(entity);
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * This handler is called when an entity has been removed.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event) {
		this.getPluginInstance().removeEntity(event.getEntity());
	}
	
	
	/**
	 * Displays a notification method when a mob is removed if enabled.
	 * @param entity A reference to the Bukkit entity where status information is coming from.
	 */
	public void notifyRemoval(Entity entity) {
		if (this.getPluginInstance().canNotify()) {
			System.out.println("Removing " + entity.getType().toString() + " from the world near " + entity.getLocation().toString());
		}
	}
}
