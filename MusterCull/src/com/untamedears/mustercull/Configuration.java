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
	 */
	private boolean hasDamageLimits = false;
	
	/**
	 * Whether or not we have limits with CullType SPAWN.
	 */
	private boolean hasSpawnLimits = false;
	
	/**
	 * Number of ticks between calls to the chunk damage laborer. 
	 */
	private long ticksBetweenDamage = 20L;
	
	
	/**
	 * Percent chance that a mob will be damaged when crowded.
	 */
	private int damageChance = 75;
	
	
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
		this.setTicksBetweenDamage(config.getInt("ticks_between_damage"));
		
		List<?> list;
				
		list = config.getList("limits");
		
		if (list != null) {
			for (Object obj : list ) {
	
				if (obj == null) {
					System.err.println("Possible bad limit in configuration file.");
					continue;
				}
				
				//TODO: Figure out how to do this without suppression.
	            @SuppressWarnings("unchecked")
	            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) obj;
	            
	            EntityType type = EntityType.fromName(map.get("type").toString().trim());

	            if (type == null) {
            		System.err.println("Unrecognized type '" + map.get("type").toString() + "' in configuration file.");
					continue;
	            }

	            int limit = (Integer)map.get("limit");
	            
	            CullType culling = CullType.fromName(map.get("culling").toString());

	            if (culling == null) {
            		System.err.println("Unrecognized culling '" + map.get("culling").toString() + "' in configuration file.");
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
		config.set("ticks_between_damage", this.ticksBetweenDamage);
				
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
		
		if (limit.getCulling() == CullType.DAMAGE){
			this.hasDamageLimits = true;
		}
		
		if (limit.getCulling() == CullType.SPAWN) {
			this.hasSpawnLimits = true;
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
	 * Returns whether or not we have limits with CullType SPAWN.
	 * @return Whether or not we have limits with CullType SPAWN.
	 */
	public boolean hasSpawnLimits() {
		return hasSpawnLimits;
	}


	/**
	 * Returns whether or not we have limits with CullType DAMAGE.
	 * @return Whether or not we have limits with CullType DAMAGE.
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
		this.ticksBetweenDamage = ticksBetweenDamage;
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
		this.damageChance = damageChance;
		this.dirty = true;
	}
}
