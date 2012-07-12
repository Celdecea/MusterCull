package com.untamedears.mustercull;

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
	 * Mob limits per chunk
	 */
	private Map<EntityType, ConfigurationLimit> mobLimits = new HashMap<EntityType, ConfigurationLimit>();
	
	/**
	 * Constructor which really has nothing to do.
	 */
	Configuration() {
		// Nothing to do here.
	}
	
	
	/**
	 * Loads configuration values from the supplied plug-in instance.
	 * @param plugin A reference to the Bukkit plug-in to load from.
	 */
	public void load(JavaPlugin plugin) {
		
		plugin.reloadConfig();
        
		FileConfiguration config = plugin.getConfig();
        
		config.options().copyDefaults(true);
        
		this.setDamage(config.getInt("damage"));
		
		List<?> list;
		
		
		list = config.getList("zones");
		
		if (list != null) {
			
			for (Object obj : list) {
				
				if (obj == null) {
					System.err.println("Possible bad zone in configuration file.");
					continue;
				}
				
				//TODO: Figure out how to do this without suppression.
	            @SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) obj;
	            
	            addZone((Integer)map.get("left"), (Integer)map.get("top"), (Integer)map.get("right"), (Integer)map.get("bottom"));
	        }
		}
		
				
		list = config.getList("limits");
		
		if (list != null) {
			for (Object obj : list ) {
	
				//TODO: Figure out how to do this without suppression.
	            @SuppressWarnings("unchecked")
	            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) obj;
	            
	            ConfigurationLimit limit = new ConfigurationLimit((Integer)map.get("limit"), CullType.fromName(map.get("culling").toString()), (Integer)map.get("range"));
	            
	            setLimit(EntityType.fromName((String)map.get("type")), limit);
	        }
		}
		
		this.dirty = false;
	}

	/**
	 * Saves configuration values to the supplied plug-in instance.
	 * @param plugin  A reference to the Bukkit plug-in to save to.
	 */
	public void save(JavaPlugin plugin) {
		
		if (!this.dirty) {
			return;
		}
		
		FileConfiguration config = plugin.getConfig();
		
		config.set("damage", this.damage);
		
		plugin.saveConfig();
		
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
	 * Adds a zone to the configuration for checking.
	 * @param left Smaller x chunk coordinate of the zone.
	 * @param top Smaller z chunk coordinate of the zone.
	 * @param right Larger x chunk coordinate of the zone.
	 * @param bottom Larger z chunk coordinate of the zone.
	 */
	public void addZone(int left, int top, int right, int bottom) {
		
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
		mobLimits.put(type, limit);
		System.out.println("Culling " + type.toString() + " using " + limit.culling.toString() + " with a limit of " + limit.limit + " and a range of " + limit.range);
		this.dirty = true;
	}
	
	/**
	 * Returns the ConfigurationLimit for the specified mob type. 
	 * @param type The type of entity to get a ConfigurationLimit for.
	 * @return The limit for the entity type, or null.
	 */
	public ConfigurationLimit getLimit(EntityType type) {
		return mobLimits.get(type);
	}
}
