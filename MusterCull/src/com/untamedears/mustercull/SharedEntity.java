package com.untamedears.mustercull;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Provides a place to store information about an entity to share between threads.
 * @author Celdecea
 *
 */
public class SharedEntity {
	
	/**
	 * X position of the entity.
	 */
	private double x;
	
	/**
	 * Y position of the entity.
	 */
	private double y;
	
	/**
	 * Z position of the entity.
	 */
	private double z;
	
	/**
	 * The type of entity, such as CREEPER, SKELETON, et cetera.
	 */
	private EntityType type;
	
	/**
	 * Consructor which populates this SharedEntity object from a Bukkit entity.
	 * @param entity A reference to a Bukkit entity to copy from.
	 */
	SharedEntity(Entity entity) {
		
		this.type = entity.getType();
		
		Location location = entity.getLocation();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}
	
	
}
