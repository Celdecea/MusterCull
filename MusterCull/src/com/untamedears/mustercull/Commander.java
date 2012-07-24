package com.untamedears.mustercull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles commands from players or server console users.
 * @author Celdecea
 *
 */
public class Commander implements CommandExecutor {

	
	/**
	 * Buffer for a reference to the plug-in instance.
	 */
	private MusterCull pluginInstance = null;

	/**
	 * Constructor which accepts a reference to the plug-in instance.
	 * @param pluginInstance A reference to the plug-in instance.
	 */
	public Commander(MusterCull pluginInstance) {
		this.pluginInstance= pluginInstance;
	}	
	
	
	/**
	 * Event handlefor commands sent by players and console users.
	 * @param sender A reference to a Bukkit CommandSender for this event.
	 * @param command A reference to a Bukkit Command for this event.
	 * @param caption The name of this event.
	 * @param argv A list of arguments for this event.
	 * @return Whether or not this event should be canceled.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String caption, String[] argv) {
		
		
		return false;
	}
}
