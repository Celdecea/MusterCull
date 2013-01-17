package com.untamedears.mustercull;

/**
 * The method used to cull the mobs
 * @author ngozi
 */
enum GlobalCullType {

	/**
	 * Kills mobs over the mob cap.
	 */
	HARDCAP;
	
	/**
	 * Returns a CullType representing the name provided.
	 * @param name A case-insensitive name to compare to.
	 * @return The CullType representing the name provided, or null.
	 */
	public static GlobalCullType fromName(String name) {
		
		if (name == null) {
			return null;
		}
		
		for (GlobalCullType culling : values()) {
			if (0 == name.compareToIgnoreCase(culling.name())) {
				return culling;
			}
		}
		
		return null;
	}	
}

