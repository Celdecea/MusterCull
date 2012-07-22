package com.untamedears.mustercull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main class for the MusterCull Bukkit plug-in.
 * @author Celdecea
 *
 */
public class MusterCull extends JavaPlugin {

	/**
	 * Buffer for keeping track of the parallel Laborer task.
	 */
	private int laborTask;
	
	/**
	 * Buffer for holding a list of entities to damage from the thread.
	 */
	private Set<UUID> damageableEntities = new HashSet<UUID>();
	
	/**
	 * Buffer for holding configuration information for this plug-in.
	 */
	private Configuration config = new Configuration();
	
	/**
	 * Called when the plug-in is enabled by Bukkit.
	 */
	public void onEnable() {
		this.config.load(this);
        
		this.laborTask = getServer().getScheduler().scheduleAsyncDelayedTask(this, new Laborer());
		
		if (this.laborTask == -1) {
			getLogger().severe("Failed to start MusterCull laborer.");
		}
				
		getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }
     
	/**
	 * Called when the plug-in is disabled by Bukkit.
	 */
    public void onDisable() { 
    	if (this.laborTask != -1) {
    		getServer().getScheduler().cancelTask(this.laborTask);
    	}
    	
    	this.config.save(this);
    }

    
    
    
    /**
     * Called by the listeners to potentially add entities to the handler.
     * @param entity A reference to the Bukkit entity to test for inclusion.
     */
    public void notifyEntity(Entity entity) {
    	
    	if (entity.isDead()) {
    		return;
    	}
    	
    	if (!entity.getType().isAlive()) {
    		return;
    	}
    	
    	UUID uuid = entity.getUniqueId();
    	
    	if (damageableEntities.contains(uuid)) {
    		damageableEntities.remove(uuid);
    		((LivingEntity)entity).damage(this.config.getDamage());
    		return;
    	}    	
    }
    
    

    
    /**
     * Return a limit from the config file for the provided entity.
     * @param entity A reference to the Bukkit entity to return a limit for.
     * @return The ConfigurationLimit for the entity, or null if none is defined.
     */
    public ConfigurationLimit getLimit(Entity entity) {
    	return this.config.getLimit(entity.getType());
    }
    
    /**
     * Return a limit from the config file for the provided entityType.
     * @param entityType A Bukkit entityType to return a limit for.
     * @return The ConfigurationLimit for the entityType, or null if none is defined.
     */
    public ConfigurationLimit getLimit(EntityType entityType) {
    	return this.config.getLimit(entityType);
    }
    
    
    
    /**
	 * Returns whether notification is enabled for this plug-in.
	 * @return Whether notification is enabled for this plug-in.
	 */
	public boolean canNotify() {
		return this.config.getNotification();
	}
    
	
	
	
	/**
	 * Used by listeners to inform the plugin that a crowded chunk exists.
	 * @param chunk A reference to a Bukkit chunk which is crowded.
	 * @param type The Bukkit EntityType which is crowding this chunk.
	 */
	public void startDamagingChunk(Chunk chunk, EntityType type) {
		System.out.println("MusterCull wants to damage " + type.getName() + " mobs in chunk " + chunk.getX() + "," + chunk.getZ());
	}

	/**
	 * Used by listeners to inform the plugin that a chunk is much better now.
	 * @param chunk A reference to a Bukkit chunk which is no longer crowded.
	 * @param type The Bukkit EntityType which is not crowding this chunk anymore.
	 */
	public void stopDamagingChunk(Chunk chunk, EntityType type) {
		System.out.println("MusterCull is finished damaging " + type.getName() + " mobs in chunk " + chunk.getX() + "," + chunk.getZ());
	}
	
	/**
	 * Used by listeners to inform the plugin that a chunk has unloaded.
	 * @param chunk A reference to a Bukkit chunk which is unloaded.
	 */
	public void stopDamagingChunk(Chunk chunk) {
		System.out.println("MusterCull is unloading chunk " + chunk.getX() + "," + chunk.getZ());
	}

	
	
	
}
