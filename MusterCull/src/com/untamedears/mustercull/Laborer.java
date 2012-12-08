package com.untamedears.mustercull;

/**
 * This class handles routinely dealing damage to mobs.
 * 
 * @author Celdecea
 *
 */
public abstract class Laborer implements Runnable {

	/**
	 * Buffer for a reference to the main plug-in class.
	 */
	private MusterCull pluginInstance = null;

	
	/**
	 * Constructor which takes a reference to the main plug-in class.
	 * @param pluginInstance A reference to the main plug-in class.
	 */
	public Laborer(MusterCull pluginInstance) {
		this.setPluginInstance(pluginInstance);
	}
	
	/**
	 * Access method for the pluginInstance property.
	 * @return The value of the pluginInstance property.
	 */
	public MusterCull getPluginInstance() {
		return pluginInstance;
	}

	/**
	 * Mutate method for the pluginInstance property.
	 * @param pluginInstance The new value for the pluginInstance property.
	 */
	public void setPluginInstance(MusterCull pluginInstance) {
		this.pluginInstance = pluginInstance;
	}

	
}
