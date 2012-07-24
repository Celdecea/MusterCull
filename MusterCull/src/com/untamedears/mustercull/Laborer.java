package com.untamedears.mustercull;

import java.util.Random;

import org.bukkit.entity.Ageable;
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
		
		if (entity.isDead()) {
			return;
		}
	
		ConfigurationLimit limit = this.pluginInstance.getLimit(entity);
		
		if (limit == null) {
			// Still nothing to do
			return;
		}
		
		if (limit.culling != CullType.DAMAGE) {
			return;
		}
			
		if (limit.range <= 0) {
			return;
		}

		Random random = new Random();
		
		// Loop through entities in range and count similar entities.
		int count = 0;
		
		for (Entity otherEntity : entity.getNearbyEntities(limit.range, limit.range, limit.range)) {
			if (0 == otherEntity.getType().compareTo(entity.getType())) {
				
				count += 1;
				
				// If we've reached a limit for this entity, go ahead and damage it.
				if (count >= limit.limit) {
					
					if (random.nextInt(100) < this.pluginInstance.getDamageChance()) {
						if (Ageable.class.isAssignableFrom(entity.getClass())) {
							Ageable agingEntity = (Ageable)entity;
							
							if (agingEntity.isAdult()) {
								agingEntity.damage(this.pluginInstance.getDamage());
							}
							else {
								agingEntity.damage(2 * this.pluginInstance.getDamage());
							}
						}
						else if (LivingEntity.class.isAssignableFrom(entity.getClass())) {
							LivingEntity livingEntity = (LivingEntity)entity;
							livingEntity.damage(this.pluginInstance.getDamage());
						}
					}
					
					return;
				}
			}
		}
	}
	

	
	
}
