package com.untamedears.mustercull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Manages the configuration for the plug-in.
 * 
 * @author Celdecea
 *
 */
public class Configuration {

	/**
	 * Whether or not configuration data needs to be saved.
	 */
	private boolean dirty = false;
	
	/**
	 * The amount of damage to apply to a crowded mob. 
	 */
	private int damage = 0;
	
	/**
	 * Mob limits loaded from the configuration file.
	 */
	private Map<EntityType, List<ConfigurationLimit>> mobLimits = new HashMap<EntityType, List<ConfigurationLimit>>();
	
	/**
	 * Whether or not we have limits with CullType DAMAGE.
	 * 
	 * This is used by the MusterCull class to determine if the tick laborer
	 * needs to be started.
	 */
	private boolean hasDamageLimits = false;
	
	/**
	 * Whether or not we have limits with CullTypes SPAWN or SPAWNER.
	 * 
	 * This is used by the MusterCull class to determine if the event listener
	 * needs to be registered.
	 */
	private boolean hasSpawnLimits = false;
	
	/**
	 * Number of ticks between calls to the chunk damage laborer.
	 */
	private long ticksBetweenDamage = 20L;
	
	/**
	 * Number of entities to damage every time the damage laborer runs.
	 */
	private int damageCalls = 1;
	
	/**
	 * Percent chance that a mob will be damaged when crowded.
	 */
	private int damageChance = 75;
	
	/**
	 * Hard number on mobs before the damage laborer cares to run.
	 */
	private int mobLimit = 1;
	
	/**
	 * Percentage of mobLimit each mob must be to trigger damage culling
	 */
	private int mobLimitPercent = 1;
	
	/**
	 * Whether or not to notify when entities have been damaged.
	 */
	private boolean damageNotify = false;
	
	/**
	 * Holds a reference to the Bukkit JavaPlugin for this project 
	 */
	private JavaPlugin pluginInstance = null;
	
	/**
	 * Constructor which stores a reference to the Bukkit JavaPlugin we are using.
	 * @param plugin A reference to a Bukkit JavaPlugin.
	 */
	Configuration(JavaPlugin plugin) {
		this.pluginInstance = plugin; 
	}

	/**
	 * Loads configuration values from the supplied plug-in instance.
	 * @param plugin A reference to the Bukkit plug-in to load from.
	 */
	public void load() {
		
		FileConfiguration config = this.pluginInstance.getConfig();
		
		this.setDamage(config.getInt("damage"));
		this.setDamageChance(config.getInt("damage_chance"));
		this.setDamageCalls(config.getInt("damage_count"));
		this.setTicksBetweenDamage(config.getInt("ticks_between_damage"));
		this.setMobLimit(config.getInt("mob_limit"));
		this.setMobLimitPercent(config.getInt("mob_limit_percent"));
		this.setDamageNotify(config.getBoolean("damage_notify"));
						
		List<?> list;
				
		list = config.getList("limits");
		
		if (list != null) {
			for (Object obj : list ) {
	
				if (obj == null) {
					this.pluginInstance.getLogger().warning("Possible bad limit in configuration file.");
					continue;
				}
				
				//TODO: Figure out how to do this without suppression.
	            @SuppressWarnings("unchecked")
	            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) obj;
	            
	            EntityType type = EntityType.fromName(map.get("type").toString().trim());

	            if (type == null) {
	            	this.pluginInstance.getLogger().warning("Unrecognized type '" + map.get("type").toString() + "' in configuration file.");
					continue;
	            }

	            int limit = (Integer)map.get("limit");
	            
	            CullType culling = CullType.fromName(map.get("culling").toString());

	            if (culling == null) {
	            	this.pluginInstance.getLogger().warning("Unrecognized culling '" + map.get("culling").toString() + "' in configuration file.");
					continue;
	            }
	            
	            int range = (Integer)map.get("range");
	            
	            
	            setLimit(type, new ConfigurationLimit(limit, culling, range));
	        }
		}
		
		this.dirty = false;
	}

	/**
	 * Saves configuration values to the supplied plug-in instance.
	 * @param plugin  A reference to the Bukkit plug-in to save to.
	 */
	public void save() {
		
		if (!this.dirty) {
			return;
		}
		
		FileConfiguration config = this.pluginInstance.getConfig();
		
		config.set("damage", this.damage);
		config.set("damage_chance", this.damageChance);
		config.set("damage_count", this.damageCalls);
		config.set("ticks_between_damage", this.ticksBetweenDamage);
		config.set("mob_limit", this.mobLimit);
		config.set("mob_limit_percent", this.mobLimitPercent);
		config.set("damage_notify", this.damageNotify);
				
		this.pluginInstance.saveConfig();
		
		this.dirty = false;
	}


	
	/**
	 * Returns the amount of damage to apply to a crowded mob.
	 * @return The amount of damage to apply to a crowded mob. 
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * Sets the amount of damage to apply to a crowded mob.
	 * @param damage The amount of damage to apply to a crowded mob.
	 */
	public void setDamage(int damage) {
		
		if (damage <= 0) {
			this.pluginInstance.getLogger().info("Warning: damage is <= 0, possibly wasting cpu cycles.");
		}
		
		this.damage = damage;
		this.dirty = true;
	}
	
	
	/**
	 * Sets the ConfigurationLimit for the specified mob type. Don't add 
	 * limits you don't need.
	 * 
	 * @param type The type of entity to set a ConfigurationLimit for.
	 * @param limit The limit for the entity type.
	 */
	public void setLimit(EntityType type, ConfigurationLimit limit) {
		
		switch (limit.getCulling()) {
		case DAMAGE:
			this.hasDamageLimits = true;
			break;
		case SPAWN:
		case SPAWNER:
			this.hasSpawnLimits = true;
			break;
		}
		
		if (mobLimits.containsKey(type)) {
			List<ConfigurationLimit> otherLimits = mobLimits.get(type);
			
			boolean foundOneToEdit = false;
			
			for (ConfigurationLimit otherLimit : otherLimits) {
				if (0 == otherLimit.getCulling().compareTo(limit.getCulling())) {
					otherLimit.setLimit(limit.getLimit());
					otherLimit.setRange(limit.getRange());
					
					foundOneToEdit = true;
					break;
				}
			}
			
			if (!foundOneToEdit) {
				otherLimits.add(limit);
			}
		}
		else {
			List<ConfigurationLimit> otherLimits = new ArrayList<ConfigurationLimit>();
			otherLimits.add(limit);
			mobLimits.put(type, otherLimits);
		}

		this.dirty = true;
		this.pluginInstance.getLogger().info("Culling " + type.toString() + " using " + limit.getCulling().toString() + "; limit=" + limit.getLimit() + " range=" + limit.getRange());
	}
	
	/**
	 * Returns the ConfigurationLimits for the specified mob type. 
	 * @param type The type of entity to get a ConfigurationLimit for.
	 * @return The limits for the entity type, or null.
	 */
	public List<ConfigurationLimit> getLimits(EntityType type) {
		return mobLimits.get(type);
	}




	/**
	 * Returns whether or not we have limits with CullType SPAWN or SPAWNER.
	 * @return true if there are any mobs with SPAWN or SPAWNER CullTypes, otherwise false.
	 */
	public boolean hasSpawnLimits() {
		return hasSpawnLimits;
	}


	/**
	 * Returns whether or not we have limits with CullType DAMAGE.
	 * @return true if there are any mobs with DAMAGE CullType, otherwise false.
	 */
	public boolean hasDamageLimits() {
		return hasDamageLimits;
	}


	/**
	 * Returns the number of ticks between calls to the damage laborer.
	 * @return Number of ticks between calls to the damage laborer.
	 */
	public long getTicksBetweenDamage() {
		return ticksBetweenDamage;
	}

	/**
	 * Sets the number of ticks between calls to the damage laborer.
	 * @param ticksBetweenDamage Number of ticks between calls to the damage laborer.
	 */
	public void setTicksBetweenDamage(long ticksBetweenDamage) {

		this.pluginInstance.getLogger().info("MusterCull will damage something every " + ticksBetweenDamage + " ticks.");

		if (ticksBetweenDamage < 20) {
			this.pluginInstance.getLogger().info("Warning: ticks_between_damage is < 20, probably won't run that fast.");
		}

		this.ticksBetweenDamage = ticksBetweenDamage;
		this.dirty = true;
	}
	
	/**
	 * Returns the number of entities to take damage each time the laborer is called.
	 * @return Number of entities to take damage each time the laborer is called.
	 */
	public int getDamageCalls() {
		return damageCalls;
	}
	
	/**
	 * Sets the number of entities to take damage each time the laborer is called. 
	 * @param damageCalls Number of entities to take damage each time the laborer is called.
	 */
	public void setDamageCalls(int damageCalls) {
		if (damageCalls <= 0) {
			this.pluginInstance.getLogger().info("Warning: damage_count is <= 0, possibly wasting cpu cycles.");
		}
		else if (damageCalls > 5) {
			this.pluginInstance.getLogger().info("Notice: damage_count is > 5, possibly killing performance.");
		}
		
		this.damageCalls = damageCalls;
		this.dirty = true;
	}


	/**
	 * Returns the percent chance that a mob will be damaged when crowded.
	 * @return Percent chance that a mob will be damaged when crowded.
	 */
	public int getDamageChance() {
		return damageChance;
	}
	
	/**
	 * Sets the percent chance that a mob will be damaged when crowded.
	 * @param damageChange Percent chance that a mob will be damaged when crowded.
	 */
	public void setDamageChance(int damageChance) {
		if (damageChance <= 0) {
			this.pluginInstance.getLogger().info("Warning: damage_chance is <= 0, possibly wasting cpu cycles.");
		}
		else if (damageChance > 100) {
			this.pluginInstance.getLogger().info("Notice: damage_chance is > 100 when 100 is the limit. Pedantry.");
		}
		
		this.damageChance = damageChance;
		this.dirty = true;
	}
	
	/**
	 * Returns the limit on mobs before the damage laborer cares to act.
	 * @return The limit on mobs before the damage laborer cares to act.
	 */
	public int getMobLimit() {
		return this.mobLimit;
	}
	
	/**
	 * Sets the limit on mobs before the damage laborer cares to act.
	 * @param mobLimit The limit on mobs before the damage laborer cares to act.
	 */
	public void setMobLimit(int mobLimit) {

		if (mobLimit < 0) {
			this.pluginInstance.getLogger().info("Warning: mob_limit is < 0 when 0 is the limit. Pedantry.");
		}
		
		if (mobLimit > 5000) {
			this.pluginInstance.getLogger().info("Warning: mob_limit is > 5000. Damage laborer may never run.");
		}
		
		this.mobLimit = mobLimit;
		this.dirty = true;
	}
	
	/**
	 * Returns the percent part per total before the damage laborer queues mobs.
	 * @return The percent part per total before the damage laborer queues mobs.
	 */
	public int getMobLimitPercent() {
		return this.mobLimitPercent;
	}
	
	/**
	 * Sets the percent part per total before the damage laborer queues mobs.
	 * @param mobLimitPercent The percent part per total before the damage laborer queues mobs.
	 */
	public void setMobLimitPercent(int mobLimitPercent) {

		if (mobLimitPercent < 0) {
			this.pluginInstance.getLogger().info("Warning: mob_limit_percent is < 0 when 0 is the limit. Pedantry.");
		}
		
		if (mobLimitPercent > 100) {
			this.pluginInstance.getLogger().info("Warning: mob_limit_percent is > 100 when 100 is the limit. Pedantry.");
		}
		
		this.mobLimitPercent = mobLimitPercent;
		this.dirty = true;
	}
	
	
	/**
	 * Gets whether to notify when an entity is damaged by this plugin.
	 */
	public boolean getDamageNotify() {
		return this.damageNotify;
	}
	
	/**
	 * Sets whether to notify when an entity is damaged by this plugin.
	 * @param damageNotify Whether to notify when an entity is damaged by this plugin.
	 */
	public void setDamageNotify(boolean damageNotify) {
		
		this.damageNotify = damageNotify;
		this.dirty = true;
	}
	
	
}
