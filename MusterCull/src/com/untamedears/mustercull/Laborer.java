package com.untamedears.mustercull;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

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
		
		Entity entity = this.pluginInstance.getNextEntity();
		
		if (entity == null) {
			// Nothing to do
			return;
		}
	
		ConfigurationLimit limit = this.pluginInstance.getLimit(entity);
		
		if (limit == null) {
			// Still nothing to do
			return;
		}
		
		if (limit.culling == CullType.DAMAGE) {
			
			if (limit.range <= 0) {
				return;
			}

			LivingEntity livingEntity = (LivingEntity)entity;
			Random random = new Random();
			
			// If the limit is 0, damage it 
			if (limit.limit <= 0) {
				if (random.nextInt(100) < this.pluginInstance.getDamageChance()) {
					livingEntity.damage(this.pluginInstance.getDamage());
				}
				return;
			}
			
			// Loop through entities in range and count similar entities.
			int count = 0;
			
			for (Entity otherEntity : entity.getNearbyEntities(limit.range, limit.range, limit.range)) {
				if (0 == otherEntity.getType().compareTo(entity.getType())) {
					count += 1;
					
					// If we've reached a limit for this entity, go ahead and damage it.
					if (count >= limit.limit) {
						if (random.nextInt(100) < this.pluginInstance.getDamageChance()) {
							livingEntity.damage(this.pluginInstance.getDamage());
						}
						return;
					}
				}
			}
		}
	}
	

	
	
}
