package com.untamedears.mustercull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

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
		this.pluginInstance = pluginInstance;
	}	
	
	
	/**
	 * Event handler for commands sent by players and console users.
	 * @param sender A reference to a Bukkit CommandSender for this event.
	 * @param command A reference to a Bukkit Command for this event.
	 * @param caption The name of this event.
	 * @param argv A list of arguments for this event.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String caption, String[] argv) {
		
		if (caption.equalsIgnoreCase("mcullstatus") || caption.equalsIgnoreCase("mustercullstatus")) {
			return commandStatus(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("mculllimit") || caption.equalsIgnoreCase("musterculllimit")) {
			return commandLimit(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("mcullentities") || caption.equalsIgnoreCase("mustercullentities")) {
			return commandEntities(sender, argv);
		}
		
		return false;
	}
	
	
	/**
	 * Command handler which returns status information.
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandStatus(CommandSender sender, String[] argv) {
		
		sender.sendMessage("MusterCull has " + this.pluginInstance.getRemainingDamageEntities() + " entities left to check.");
		
		return true;
	}
	
	/**
	 * Command handler which manipulates the limit table.
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandLimit(CommandSender sender, String[] argv) {

		if (argv.length < 4) {
			return false;
		}
		
		CullType cullingType = CullType.fromName(argv[0]);
		
		if (cullingType == null) {
			sender.sendMessage("MusterCull: invalid culling type: " + argv[0]);
			return true;
		}
		
		EntityType entityType = EntityType.fromName(argv[1]);
		
		if (entityType == null) {
			sender.sendMessage("MusterCull: invalid entity type: " + argv[1] + " (see /mcullentities)");
			return true;
		}
		
		Integer limit = Integer.parseInt(argv[2]);
		Integer range = Integer.parseInt(argv[3]);
		
		if (range <= 0) {
			sender.sendMessage("MusterCull: range must be greater than zero.");
			return true;
		}
		
		ConfigurationLimit configLimit = new ConfigurationLimit(limit, cullingType, range);

		this.pluginInstance.setLimit(entityType, configLimit);
		
		return true;
	}
	
	
	/**
	 * Command handler which returns a list of Bukkit entities. 
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandEntities(CommandSender sender, String[] argv) {
		
		int string_count = 0;
		StringBuilder message = new StringBuilder();
		
		for (EntityType entityType : EntityType.values()) {
			
			if (entityType.getName() == "null") {
				continue;
			}
			
			if (entityType.isAlive()) {
				if (string_count > 0) {
					message.append(", ");
				}
				
				message.append(entityType.getName());
				
				if (string_count++ > 4) {
					sender.sendMessage("Entity Types: " + message.toString());
					message = new StringBuilder();
					string_count = 0;
				}
			}
			
		}
		
		if (string_count > 0) {
			sender.sendMessage("Entity Types: " + message.toString());
			message = new StringBuilder();
		}
		
		return true;
	}
}
