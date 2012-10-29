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
	 * Uses Bukkit events to prevent a spawner from operating.
	 */
	SPAWNER,

	/**
	 * Uses damage on mobs in crowded chunks.
	 */
	DAMAGE;
	
	/**
	 * Returns a CullType representing the name provided.
	 * @param name A case-insensitive name to compare to.
	 * @return The CullType representing the name provided, or null.
	 */
	public static CullType fromName(String name) {
		
		if (name == null) {
			return null;
		}
		
		for (CullType culling : values()) {
			if (0 == name.compareToIgnoreCase(culling.name())) {
				return culling;
			}
		}
		
		return null;
	}	
}

