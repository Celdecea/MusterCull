package com.untamedears.mustercull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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
		
		if (caption.equalsIgnoreCase("mcull") || caption.equalsIgnoreCase("mustercull")) {
			return commandControl(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("mcullstatus") || caption.equalsIgnoreCase("mustercullstatus")) {
			return commandStatus(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("mculllimit") || caption.equalsIgnoreCase("musterculllimit")) {
			return commandLimit(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("mcullentities") || caption.equalsIgnoreCase("mustercullentities")) {
			return commandEntities(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("mculltypes") || caption.equalsIgnoreCase("musterculltypes")) {
			return commandTypes(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("muster")) {
			return commandMuster(sender, argv);
		}
		
		if (caption.equalsIgnoreCase("cull")) {
			return commandCull(sender, argv);
		}
		
		return false;
	}
	
	
	/**
	 * Command handler which controls the plugin.
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandControl(CommandSender sender, String[] argv) {

		if (argv.length < 1) {
			return false;
		}

		CullType cullType = null;
		
		if (argv.length >= 2) {
			cullType = CullType.fromName(argv[1]);
		}
		
		if (argv[0].compareToIgnoreCase("pause") == 0) {
			if (cullType == null) {
				this.pluginInstance.pauseAllCulling();
				sender.sendMessage("MusterCull: culling paused for all culling types.");
			}
			else {
				this.pluginInstance.pauseCulling(cullType);
				sender.sendMessage("MusterCull: culling paused for " + cullType.toString() + ", if it wasn't already.");
			}
		}
		else if (argv[0].compareToIgnoreCase("continue") == 0 || 0 == argv[0].compareToIgnoreCase("resume")) {
			if (cullType == null) {
				this.pluginInstance.resumeAllCulling();
				sender.sendMessage("MusterCull: culling resumed for all culling types.");
			}
			else {
				this.pluginInstance.resumeCulling(cullType);
				sender.sendMessage("MusterCull: culling resumed for " + cullType.toString() + ", if it was disabled.");
			}
		}
		else if (argv[0].compareToIgnoreCase("reset") == 0) {
			this.pluginInstance.clearRemainingDamageEntities();
			sender.sendMessage("MusterCull: remaining entities cleared from the damage list.");
		}
		else {
			return false;
		}
			
		
		return true;
	}
	
	
	/**
	 * Command handler which returns status information.
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandStatus(CommandSender sender, String[] argv) {
		
		if (this.pluginInstance.hasDamageLimits()) {
			sender.sendMessage("MusterCull is using a laborer to damage " + this.pluginInstance.getRemainingDamageEntities() + " remaining entities before starting over.");
		}

		for (CullType cullType : CullType.values()) {
			if (this.pluginInstance.isPaused(cullType)) {
				sender.sendMessage("CullType " + cullType.toString() + " is paused.");
			}
		}

		int limit = 5;
		for (StatusItem status : this.pluginInstance.getStats()) {
			if (limit-- <= 0) {
				break;
			}
			else {
				sender.sendMessage("Player " + status.getEntity().getName() + " surrounded by " + status.getNearbyEntityCount() + " entities.");
			}
		}
		
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
			sender.sendMessage("MusterCull: invalid culling type: " + argv[0] + " (use /mculltypes)");
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
	
	
	/**
	 * Command handler which returns a list of Culling Types. 
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandTypes(CommandSender sender, String[] argv) {
		
		int string_count = 0;
		StringBuilder message = new StringBuilder();
		
		for (CullType cullType : CullType.values()) {
			
			if (cullType.toString() == "null") {
				continue;
			}
			
			if (string_count > 0) {
				message.append(", ");
			}
			
			message.append(cullType.toString());
			
			if (string_count++ > 4) {
				sender.sendMessage("Culling Types: " + message.toString());
				message = new StringBuilder();
				string_count = 0;
			}
			
		}
		
		if (string_count > 0) {
			sender.sendMessage("Culling Types: " + message.toString());
			message = new StringBuilder();
		}
		
		return true;
	}
	
	
	
	
	/**
	 * Command handler damages nearby mobs.
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandMuster(CommandSender sender, String[] argv) {
		
		if (argv.length < 3) {
			return false;
		}
		
		EntityType entityType = EntityType.fromName(argv[0]);
		
		if (entityType == null) {
			sender.sendMessage("MusterCull: invalid entity type: " + argv[0] + " (see /mcullentities)");
			return true;
		}
		
		Integer damage = Integer.parseInt(argv[1]);
		Integer range = Integer.parseInt(argv[2]);
		
		if (damage <= 0) {
			sender.sendMessage("MusterCull: damage must be greater than zero.");
		}
		
		if (range <= 0) {
			sender.sendMessage("MusterCull: range must be greater than zero.");
			return true;
		}
		
		for (Entity entity : this.pluginInstance.getNearbyEntities(sender.getName(), range, range, range)) {
			if (entity.getType() == entityType) {
				this.pluginInstance.damageEntity(entity, damage);
			}
		}
		
		return true;
	}
	
	/**
	 * Command handler which kills nearby mobs.
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandCull(CommandSender sender, String[] argv) {
		
		if (argv.length < 2) {
			return false;
		}
		
		EntityType entityType = EntityType.fromName(argv[0]);
		
		if (entityType == null) {
			sender.sendMessage("MusterCull: invalid entity type: " + argv[0] + " (see /mcullentities)");
			return true;
		}
		
		Integer range = Integer.parseInt(argv[1]);
		
		if (range <= 0) {
			sender.sendMessage("MusterCull: range must be greater than zero.");
			return true;
		}
		
		for (Entity entity : this.pluginInstance.getNearbyEntities(sender.getName(), range, range, range)) {
			if (entity.getType() == entityType) {
				this.pluginInstance.damageEntity(entity, 999);
			}
		}
		
		return true;
	}
	

	
	
	
}
