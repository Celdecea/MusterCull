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
	 * @param event A reference to the associated Bukkit event.
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		// Grab a configured limit for event entity
		Entity entity = event.getEntity();
		ConfigurationLimit limit = this.getPluginInstance().getLimit(entity);
		
		if (limit == null) {
			return;
		}
		
		if (limit.getCulling() == CullType.SPAWN) {
			 
			// If the limit is 0, prevent all of this entity type from spawning 
			if (limit.getLimit() <= 0) {
				event.setCancelled(true);
				return;
			}
			
			// Loop through entities in range and count similar entities.
			int count = 0;
			
			for (Entity otherEntity : entity.getNearbyEntities(limit.getRange(), limit.getRange(), limit.getRange())) {
				if (0 == otherEntity.getType().compareTo(entity.getType())) {
					count += 1;
					
					// If we've reached a limit for this entity, prevent it from spawning.
					if (count >= limit.getLimit()) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		
		
		if (limit.getCulling() == CullType.DAMAGE) {
			getPluginInstance().addEntityLimitPair(new EntityLimitPair(entity, limit));
		}
		
	}

}
