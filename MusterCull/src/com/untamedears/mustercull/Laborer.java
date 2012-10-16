package com.untamedears.mustercull;

import java.util.Random;

import org.bukkit.entity.Entity;

/**
 * This class handles routinely dealing damage to chunks.
 * 
 * @author Celdecea
 *
 */
public class Laborer implements Runnable {

	/**
	 * Buffer for a reference to the main plug-in class.
	 */
	private MusterCull pluginInstance = null;
	
	
	/**
	 * Constructor which takes a reference to the main plug-in class.
	 * @param pluginInstance A reference to the main plug-in class.
	 */
	public Laborer(MusterCull pluginInstance) {
		this.pluginInstance = pluginInstance;
	}
	
	/**
	 * Repeating damage method for the class.
	 */
	@Override
	public void run() {
		
		if (this.pluginInstance.isPaused(CullType.DAMAGE)) {
			return;
		}
		
		EntityLimitPair entityLimitPair = this.pluginInstance.getNextEntity();
		
		if (entityLimitPair == null) {
			return;
		}
		
		Entity entity = entityLimitPair.getEntity();
		
		if (entity == null || entity.isDead()) {
			return;
		}
		
		ConfigurationLimit limit = entityLimitPair.getLimit();
		
		if (limit.getCulling() != CullType.DAMAGE) {
			return;
		}
		
		Random random = new Random();
		
		// Loop through entities in range and count similar entities.
		int count = 0;
		
		for (Entity otherEntity : entity.getNearbyEntities(limit.getRange(), limit.getRange(), limit.getRange())) {
			if (0 == otherEntity.getType().compareTo(entity.getType())) {
				
				count += 1;
				
				// If we've reached a limit for this entity, go ahead and damage it.
				if (count >= limit.getLimit()) {
					
					if (random.nextInt(100) < this.pluginInstance.getDamageChance()) {
						this.pluginInstance.damageEntity(entity, this.pluginInstance.getDamage());
					}
					
					return;
				}
			}
		}
	}
	

	
	
}
