package com.untamedears.mustercull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
		
		if (caption.length() <= 0) {
			return false;
		}
		
		caption = caption.toLowerCase().replace("mustercull", "mcull");
		
		if (caption.equals("mcull")) {
			return commandControl(sender, argv);
		}
		else if (caption.equals("muster")) {
			return commandMuster(sender, argv);
		}
		else if (caption.equals("cull")) {
			return commandCull(sender, argv);
		}
		else if (caption.equals("mcullstatus")) {
			return commandStatus(sender, argv);
		}
		else if (caption.equals("mculllimit")) {
			return commandLimit(sender, argv);
		}
		else if (caption.equals("mcullentities")) {
			return commandEntities(sender, argv);
		}
		else if (caption.equals("mculltypes")) {
			return commandTypes(sender, argv);
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Command handler which allows pausing/resuming individual culling types
	 * and resetting the list of entities to damage (if any).
	 * @param sender A reference to a Bukkit CommandSender for this handler.
	 * @param argv A list of arguments for this handler.
	 * @return Whether or not this event was handled and should be canceled.
	 */
	public boolean commandControl(CommandSender sender, String[] argv) {

		if (argv.length < 1) {
			return false;
		}
		
		if (argv[0].compareToIgnoreCase("pause") == 0) {
			
			CullType cullType = null;
			
			if (argv.length >= 2) {
				cullType = CullType.fromName(argv[1]);
				
				if (cullType == null) {
					sender.sendMessage("MusterCull: unknown culling type '" + argv[1] + "' provided. Use /mculltypes to get a list.");
					return true;
				}
			}
			
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
			
			CullType cullType = null;
			
			if (argv.length >= 2) {
				cullType = CullType.fromName(argv[1]);
				
				if (cullType == null) {
					sender.sendMessage("MusterCull: unknown culling type '" + argv[1] + "' provided. Use /mculltypes to get a list.");
					return true;
				}
			}
			
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
		else if (argv[0].compareToIgnoreCase("damage") == 0) {
			
			int damage = 0;
			
			if (argv.length >= 1) {
				try {
					damage = Integer.parseInt(argv[1]);
				}
				catch (NumberFormatException e) {
					sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[1]);
					return true;
				}
			}
			
			this.pluginInstance.setDamage(damage);
			
			sender.sendMessage("MusterCull: setting damage to " + damage + ".");
		}
		else if (argv[0].compareToIgnoreCase("count") == 0) {
			
			int count = 0;
			
			if (argv.length >= 1) {
				try {
					count = Integer.parseInt(argv[1]);
				}
				catch (NumberFormatException e) {
					sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[1]);
					return true;
				}
			}
			
			this.pluginInstance.setDamageCalls(count);
			
			sender.sendMessage("MusterCull: setting damage call count to " + count + ".");
		}
		else if (argv[0].compareToIgnoreCase("chance") == 0) {
	
			int chance = 0;
			
			if (argv.length >= 1) {
				try {
					chance = Integer.parseInt(argv[1]);
				}
				catch (NumberFormatException e) {
					sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[1]);
					return true;
				}
			}
			
			this.pluginInstance.setDamageChance(chance);
			
			sender.sendMessage("MusterCull: setting damage chance count to " + chance + ".");
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
		
		boolean reported = false;
		
		if (this.pluginInstance.hasDamageLimits()) {
			sender.sendMessage("MusterCull is using a laborer to damage " + this.pluginInstance.getRemainingDamageEntities() + " remaining entities before starting over.");
			reported = true;
		}

		for (CullType cullType : CullType.values()) {
			if (this.pluginInstance.isPaused(cullType)) {
				sender.sendMessage("CullType " + cullType.toString() + " is paused.");
				reported = true;
			}
		}

		int limit = 5;
		for (StatusItem status : this.pluginInstance.getStats()) {
			if (limit-- <= 0) {
				break;
			}
			else {
				sender.sendMessage("Player " + status.getEntity().getName() + " surrounded by " + status.getNearbyEntityCount() + " entities.");
				reported = true;
			}
		}
		
		// Last message
		if (!reported) {
			sender.sendMessage("MusterCull has nothing to report.");
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
		
		int limit = 0;
		
		try {
			limit = Integer.parseInt(argv[2]);
		}
		catch (NumberFormatException e) {
			sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[2]);
			return true;
		}
		
		if (limit < 0) {
			sender.sendMessage("MusterCull: limit must be greater than or equal to zero.");
			return true;
		}
		
		int range = 0;
		
		try {
			range = Integer.parseInt(argv[3]);
		}
		catch (NumberFormatException e) {
			sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[3]);
			return true;
		}
		
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
					sender.sendMessage("MusterCull Entity Types: " + message.toString());
					message = new StringBuilder();
					string_count = 0;
				}
			}
			
		}
		
		if (string_count > 0) {
			sender.sendMessage("MusterCull Entity Types: " + message.toString());
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
				sender.sendMessage("MusterCull Culling Types: " + message.toString());
				message = new StringBuilder();
				string_count = 0;
			}
			
		}
		
		if (string_count > 0) {
			sender.sendMessage("MusterCull Culling Types: " + message.toString());
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
		
		int damage = 0;
		
		try {
			damage = Integer.parseInt(argv[1]);
		}
		catch (NumberFormatException e) {
			sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[1]);
			return true;
		}
		
		if (damage <= 0) {
			sender.sendMessage("MusterCull: damage must be greater than zero.");
		}
		 
		int range = 0;
		
		try {
			range = Integer.parseInt(argv[2]);
		}
		catch (NumberFormatException e) {
			sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[2]);
			return true;
		}
		
		if (range <= 0) {
			sender.sendMessage("MusterCull: range must be greater than zero.");
			return true;
		}
		
		String searchName = null;
		
		if (argv.length > 3) {
			searchName = argv[3];
		}
		else {
			if (sender instanceof Player) {
				searchName = sender.getName();
			}
			else {
				sender.sendMessage("MusterCull: Please add a player name to end of the command.");
				return true;
			}
		}

		int count = this.pluginInstance.damageEntitiesAroundPlayer(searchName, entityType, damage, range);
		
		if (count > 0) {
			sender.sendMessage("MusterCull: damaged up to " + count + " entities of type " + entityType + " around player " + searchName + ".");
		}
		else {
			sender.sendMessage("MusterCull: Could not find any entities of type " + entityType + " to damage around player "+ searchName + ".");
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
		
		int range = 0;
		
		try {
			range = Integer.parseInt(argv[1]);
		}
		catch (NumberFormatException e) {
			sender.sendMessage("MusterCull: parameter must be a number, you entered: " + argv[1]);
			return true;
		}
		
		if (range <= 0) {
			sender.sendMessage("MusterCull: range must be greater than zero.");
			return true;
		}
		
		String searchName = null;
		
		if (argv.length > 2) {
			searchName = argv[2];
		}
		else {
			if (sender instanceof Player) {
				searchName = sender.getName();
			}
			else {
				sender.sendMessage("MusterCull: Please add a player name to end of the command.");
				return true;
			}
		}

		int count = this.pluginInstance.damageEntitiesAroundPlayer(searchName, entityType, 999, range);
		
		if (count > 0) {
			sender.sendMessage("MusterCull: damaged up to " + count + " entities of type " + entityType + " around player " + searchName + ".");
		}
		else {
			sender.sendMessage("MusterCull: Could not find any entities of type " + entityType + " to damage around player "+ searchName + ".");
		}
		
		return true;
	}
	


	
	
}
