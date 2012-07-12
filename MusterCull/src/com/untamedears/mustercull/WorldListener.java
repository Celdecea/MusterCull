package com.untamedears.mustercull;

//TODO: Enable these when CLUSTER method is implemented.
//import org.bukkit.Chunk;
//import org.bukkit.entity.Entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

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
		
		//TODO: Enable this when CLUSTER method is implemented.
		//Chunk chunk = event.getChunk();
		//for (Entity entity : chunk.getEntities()) {
		//	getPluginInstance().notifyEntity(entity);
        //}
	}
}
