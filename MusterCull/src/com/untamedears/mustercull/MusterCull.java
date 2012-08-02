package com.untamedears.mustercull;

import java.util.Stack;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the main class for the MusterCull Bukkit plug-in.
 * @author Celdecea
 *
 */
public class MusterCull extends JavaPlugin {

	/**
	 * Holds a list of entities to monitor.
	 */
	private Stack<EntityLimitPair> knownEntities = new Stack<EntityLimitPair>();
	
	/**
	 * Buffer for keeping track of the parallel Laborer task.
	 */
	private int laborTask = -1;
	
	/**
	 * Buffer for holding configuration information for this plug-in.
	 */
	private Configuration config = null;
	
	/**
	 * Called when the plug-in is enabled by Bukkit.
	 */
	public void onEnable() {
		
		this.config = new Configuration(this);
		this.config.load();
        
		
		if (this.config.hasDamageLimits()) {
			
			this.laborTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Laborer(this), this.config.getTicksBetweenDamage(), this.config.getTicksBetweenDamage());

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
		
		Commander commander = new Commander(this);
		
		for (String command : getDescription().getCommands().keySet()) {
			getCommand(command).setExecutor(commander);
		}
    }
     
	/**
	 * Called when the plug-in is disabled by Bukkit.
	 */
    public void onDisable() { 
    	if (this.laborTask != -1) {
    		getServer().getScheduler().cancelTask(this.laborTask);
    	}
    	
    	this.config.save();
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
	 * Adds an entity to the list for damage monitoring.
	 * @param entity A reference to a Bukkit Entity to monitor for mob damage.
	 */
	public void addEntityLimitPair(EntityLimitPair entityLimitPair) {
		if (this.config.hasDamageLimits()) {
			this.knownEntities.push(entityLimitPair);
		}
	}
	
	/**
	 * Returns the next entity for monitoring.
	 * @return A reference to an EntityLimitPair.
	 */
	public EntityLimitPair getNextEntity() {
		
		if (this.knownEntities.empty()) {
			
			for (World world : getServer().getWorlds()) {
				for (Entity entity : world.getEntities()) {
					ConfigurationLimit limit = this.config.getLimit(entity.getType());
					
					if (limit != null && limit.culling == CullType.DAMAGE) { 
						addEntityLimitPair(new EntityLimitPair(entity, limit));
					}
				}
			}
			
			getLogger().info("Grabbed " + this.knownEntities.size() + " entities this round.");
			return null;
		}

		EntityLimitPair entityLimitPair = this.knownEntities.pop(); 
		
		System.out.println("Returning entity " + entityLimitPair.getEntity().getEntityId());
		return entityLimitPair;
	}
	
	
	/**
	 * Returns the percent chance that a mob will be damaged when crowded.
	 * @return Percent chance that a mob will be damaged when crowded.
	 */
	public int getDamageChance() {
		return this.config.getDamageChance();
	}

}
