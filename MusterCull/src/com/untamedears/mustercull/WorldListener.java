package com.untamedears.mustercull;

//TODO: Enable these when CLUSTER method is implemented.
//import org.bukkit.Chunk;
//import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;


/**
 * This class provides event handlers for the game world.
 * 
 * @author Celdecea
 *
 */
public class WorldListener extends Listener {

	
	/**
	 * This constructor wraps the parent Listener's constructor.
	 * @param pluginInstance A reference to the plug-in instance.
	 */
	public WorldListener(MusterCull pluginInstance) {
		super(pluginInstance);
	}

	/**
	 * This handler is called once a chunk has finished loading.
	 * @param event A reference to the associated event.
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onChunkLoad(ChunkLoadEvent event) {
		
		Map<EntityType, Integer> entityCounts = new HashMap<EntityType, Integer>();
		
		for (Entity entity : event.getChunk().getEntities()) {
			Integer count =	entityCounts.get(entity.getType());
			
			if (count == null) {
				entityCounts.put(entity.getType(), 1);
			}
			else {
				entityCounts.put(entity.getType(), count + 1);
			}
        }
		
		
		for (Map.Entry<EntityType, Integer> entry : entityCounts.entrySet()) {
			
			ConfigurationLimit limit = this.getPluginInstance().getLimit(entry.getKey());

			if (limit != null && limit.culling == CullType.DAMAGE) {
				if (limit.limit <= entry.getValue()) {
					this.getPluginInstance().startDamagingChunk(event.getChunk(), entry.getKey());
				}
				else {
					this.getPluginInstance().stopDamagingChunk(event.getChunk(), entry.getKey());
				}
				
			}
		}
	}
	
	
	
	/**
	 * This handler is called once a chunk has unloaded.
	 * @param event A reference to the associated event.
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onChunkUnload(ChunkUnloadEvent event) {
		this.getPluginInstance().stopDamagingChunk(event.getChunk());
	}
	
}


