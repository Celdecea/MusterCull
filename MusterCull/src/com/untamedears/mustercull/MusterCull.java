package com.untamedears.mustercull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
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
	 * Buffer for holding a list of entities to send to the thread.
	 */
	private Map<UUID, SharedEntity> waitingEntities = new HashMap<UUID, SharedEntity>();

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

    	if (waitingEntities.containsKey(entity.getUniqueId())) {
    		return;
    	}
    	
    	this.waitingEntities.put(uuid, new SharedEntity(entity));
    }
    
    

    
    /**
     * Return a limit from the config file for the provided entity.
     * @param entity A reference to the Bukkit entity to return a limit for.
     * @return The ConfigurationLimit for the entity, or null if none is defined.
     */
    public ConfigurationLimit getLimit(Entity entity) {
    	return this.config.getLimit(entity.getType());
    }
    
}
