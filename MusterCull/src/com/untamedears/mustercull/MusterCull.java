package com.untamedears.mustercull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	 * Holds a list of chunks to damage with specific Bukkit EntityTypes.
	 */
	private List<Chunk> chunksToDamage = new ArrayList<Chunk>();
	
	/**
	 * Buffer for keeping track of the parallel Laborer task.
	 */
	private int laborTask = -1;
	
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
        
		if (this.config.hasDamageLimits()) {
			this.laborTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Laborer(this), this.config.getTicksBetweenChunkDamage(), this.config.getTicksBetweenChunkDamage());

			if (this.laborTask == -1) {
				getLogger().severe("Failed to start MusterCull laborer.");
			}
		}
		
		if (this.config.hasSpawnLimits() || this.config.hasDamageLimits()) {
			getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		}
		else {
			getLogger().info("MusterCull doesn't appear to have anything to do.");
		}
		
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
		return this.config.getNotificationEnabled();
	}
    
	
	/**
	 * Returns whether or not we have limits with CullType DAMAGE.
	 * @return Whether or not we have limits with CullType DAMAGE.
	 */
	public boolean hasDamageLimits() {
		return this.config.hasDamageLimits();
	}
	
	
	/**
	 * Returns whether or not we have limits with CullType SPAWN.
	 * @return Whether or not we have limits with CullType SPAWN.
	 */
	public boolean hasSpawnLimits() {
		return this.config.hasSpawnLimits();
	}
	
	
	
	
	/**
	 * Returns the amount of damage to apply to a crowded mob.
	 * @return The amount of damage to apply to a crowded mob. 
	 */
	public int getDamage() {
		return this.config.getDamage();
	}
	
	
	
	/**
	 * Adds a chunk to the list for damage monitoring.
	 * @param chunk A reference to a Bukkit Chunk to monitor for mob damage.
	 */
	public void addChunk(Chunk chunk) {
		if (this.config.hasDamageLimits()) {
			if (!this.chunksToDamage.contains(chunk)) {
				this.chunksToDamage.add(chunk);
			}
		}
	}
	
	/**
	 * Removes a chunk from the list for damage monitoring.
	 * @param chunk A reference to a Bukkit Chunk to no longer monitor for mob damage.
	 */
	public void removeChunk(Chunk chunk) {
		if (this.config.hasDamageLimits()) {
			this.chunksToDamage.remove(chunk);
		}	
	}
	
	
	/**
	 * Returns the next chunk for damaging.
	 * @return A reference to a Bukkit Chunk to damage.
	 */
	public Chunk getNextChunk() {
		
		return null;
	}
}
