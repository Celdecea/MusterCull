package com.untamedears.mustercull;

/**
 * Represents a mob spawn limit from the configuration file.
 * @author Celdecea
 *
 */
public class ConfigurationLimit {

	/**
	 * Whether or not data has changed in this pair.
	 */
	private boolean dirty = false;
	
	
	/**
	 * Max number of specific mob types to spawn in an area.
	 */
	private int limit = 0;
	
	
	/**
	 * Stores the method used to cull the mobs by this particular Configuration_Limit instance.
	 */
	private CullType culling = CullType.SPAWN;
	
	/**
	 * The maximum range to check from the spawn.
	 */
	private int range = 0;
	
	/**
	 * Constructor which sets all properties of this class.
	 * @param limit The maximum number of mobs of a specific type to allow
	 * @param culling The method used to cull the mob.
	 * @param range The range to check
	 */
	ConfigurationLimit(int limit, CullType culling, int range) {
		this.setLimit(limit);
		this.setCulling(culling);
		this.setRange(range);
	}

	/**
	 * Sets the max number of specific mob types to spawn in an area.
	 * @param limit The max number of specific mob types to spawn in an area.
	 */
	public void setLimit(int limit) {
		this.limit = limit;
		this.dirty = true;
	}

	/**
	 * Returns the max number of specific mob types to spawn in an area.
	 * @return The max number of specific mob types to spawn in an area.
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Sets the method used to cull the mobs by this particular Configuration_Limit instance.
	 * @param culling The method used to cull the mobs by this particular Configuration_Limit instance.
	 */
	public void setCulling(CullType culling) {
		this.culling = culling;
		this.dirty = true;
	}

	/**
	 * Returns the method used to cull the mobs by this particular Configuration_Limit instance.
	 * @return The method used to cull the mobs by this particular Configuration_Limit instance.
	 */
	public CullType getCulling() {
		return culling;
	}

	/**
	 * Sets the maximum range to check from the spawn.
	 * @param range The maximum range to check from the spawn.
	 */
	public void setRange(int range) {
		
		if (range <= 0) {
			throw new IllegalArgumentException("Range must be greater than zero.");
		}
		
		this.range = range;
		this.dirty = true;
	}

	/**
	 * Returns the maximum range to check from the spawn.
	 * @return The maximum range to check from the spawn.
	 */
	public int getRange() {
		return range;
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
