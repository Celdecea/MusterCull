package com.untamedears.mustercull;

/**
 * Represents a mob spawn limit from the configuration file.
 * @author Celdecea
 *
 */
public class ConfigurationLimit {

	/**
	 * Max number of specific mob types to spawn in an area.
	 */
	public int limit = 0;
	
	
	/**
	 * Stores the method used to cull the mobs by this particular Configuration_Limit instance.
	 */
	CullType culling = CullType.SPAWN;
	
	/**
	 * The maximum 3d range to check from the spawn.
	 */
	int range = 0;
	
	/**
	 * Constructor which sets all properties of this class.
	 * @param limit The maximum number of mobs of a specific type to allow
	 * @param culling The method used to cull the mob.
	 * @param range The 3d range to check
	 */
	ConfigurationLimit(int limit, CullType culling, int range) {
		this.limit = limit;
		this.culling = culling;
		this.range = range;
	}
}
