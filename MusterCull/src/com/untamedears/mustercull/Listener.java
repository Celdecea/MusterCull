package com.untamedears.mustercull;

/**
 * Provides a base class for listeners to extend.
 * 
 * @author Celdecea
 *
 */
public class Listener implements org.bukkit.event.Listener {
	
	/**
	 * Buffer for a reference to the plug-in instance.
	 */
	private MusterCull pluginInstance = null;
	
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

	/**
	 * Constructor which accepts a reference to the plug-in instance.
	 * @param pluginInstance A reference to the plug-in instance.
	 */
    public Listener(MusterCull pluginInstance) {
        this.setPluginInstance(pluginInstance);
    }

	
}
