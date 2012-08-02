package com.untamedears.mustercull;

import org.bukkit.entity.Entity;

/**
 * Groups an entity and a limit for storage in perhaps a List.
 * 
 * @author Celdecea
 */
public class EntityLimitPair {

	/**
	 * Whether or not data has changed in this pair.
	 */
	private boolean dirty = false;
	
	/**
	 * The Bukkit entity for this pair.
	 */
	private Entity entity = null;
	
	/**
	 * The configured limit for this pair.
	 */
	private ConfigurationLimit limit = null;
	
	/**
	 * Constructor which sets the entity and limit for this pair.
	 * @param entity A reference to a Bukkit entity to set.
	 * @param limit A reference to a ConfigurationLimit to set.
	 */
	public EntityLimitPair(Entity entity, ConfigurationLimit limit) {
		this.setEntity(entity);
		this.setLimit(limit);
		this.clearDirty();
	}

	/**
	 * Sets the entity for this pair.
	 * @param entity A reference to a Bukkit entity to set.
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * Returns the entity for this pair.
	 * @return A reference to a Bukkit entity.
	 */
	public Entity getEntity() {
		return entity;
	}

	/**
	 * Sets the configured limit for this pair.
	 * @param limit A reference to a ConfigurationLimit object.
	 */
	public void setLimit(ConfigurationLimit limit) {
		this.limit = limit;
	}

	/**
	 * Returns the configured limit for this pair.
	 * @return A reference to a ConfigurationLimit object.
	 */
	public ConfigurationLimit getLimit() {
		return limit;
	}

	/**
	 * Returns whether or not data in this object has changed.
	 * @return The value of the encapsulated dirty property.
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * Clears the dirty flag for this object.
	 */
	public void clearDirty() {
		this.dirty = false;
	}
}
