package com.untamedears.mustercull;

/**
 * The method used to cull the mobs
 * @author Celdecea
 */
enum CullType {
	
	/**
	 * Uses Bukkit events to deny a spawn.
	 */
	SPAWN,
	
	/**
	 * Uses clustering to find mobs to damage.
	 */
	CLUSTER;
	
	/**
	 * Returns a CullType representing the name provided.
	 * @param name A case-sensitive name to compare to.
	 * @return The CullType representing the name provided.
	 */
	public static CullType fromName(String name) {
		
		for (CullType culling : values()) {
			if (0 == name.compareTo(culling.name())) {
				return culling;
			}
		}
		
		return null;
	}
}

