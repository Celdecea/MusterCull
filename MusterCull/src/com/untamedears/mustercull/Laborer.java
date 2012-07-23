package com.untamedears.mustercull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
	 * 
	 * Each time this method is called it is expected to find a specific
	 * mob in a specific chunk and damage it. While doing this it should
	 * check to see if we should stop damaging that particular chunk.
	 */
	@Override
	public void run() {
		
		Chunk chunk = this.pluginInstance.getNextChunk();
		
		if (chunk == null) {
			// Nothing to do
			return;
		}
			
		
		Map<EntityType, Integer> entityCounts = new HashMap<EntityType, Integer>();
		Map<EntityType, List<Entity>> entityTable = new HashMap<EntityType, List<Entity>>(); 
		
		for (Entity entity : chunk.getEntities()) {
			Integer count =	entityCounts.get(entity.getType());
			
			if (count == null) {
				entityCounts.put(entity.getType(), 1);
				
				List<Entity> list = new ArrayList<Entity>();
				list.add(entity);
				
				entityTable.put(entity.getType(), list);
			}
			else {
				entityCounts.put(entity.getType(), count + 1);
				
				List<Entity> list = entityTable.get(entity.getType());
				list.add(entity);
				
				entityTable.put(entity.getType(), list);
			}
        }
		
		
		Random random = new Random();
		
		for (Entry<EntityType, Integer> entry : entityCounts.entrySet()) {
			
			ConfigurationLimit limit = this.pluginInstance.getLimit(entry.getKey());

			if (limit != null && limit.culling == CullType.DAMAGE) {
				if (limit.limit <= entry.getValue()) {
				
					List<Entity> list = entityTable.get(entry.getKey());
					Entity entity = list.get(random.nextInt(list.size()));
					
					final LivingEntity livingEntity = (LivingEntity)entity;
					livingEntity.damage(pluginInstance.getDamage());
				}
			}
		}
	}
	

	
	
}
