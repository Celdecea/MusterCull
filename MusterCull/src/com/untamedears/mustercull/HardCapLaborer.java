package com.untamedears.mustercull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * This class performs damage to mobs using the DAMAGE CullType.
 * @author ngozi
 */
public class HardCapLaborer extends Laborer {

	/**
	 * Constructor which takes a reference to the main plug-in class.
	 * @param pluginInstance A reference to the main plug-in class.
	 */
	public HardCapLaborer(MusterCull pluginInstance) {
		super(pluginInstance);
	}

	/**
	 * Repeating method for the class.
     * Kills N mobs where N is the number of mobs over the mob cap.
     * Recently dead mobs are counted toward the mob cap since filtering them out is costly.
     * Therefore, this should not run too quickly (set config).
     *
     * If a lot of mobs recently died when this runs then this will kill too many mobs. Possible far too many.
     * Luckily this can only happen when the cap is lowered or old chunks are loaded.
	 */

	public void run() {

        if (this.getPluginInstance().isPaused(GlobalCullType.HARDCAP)) {
            return;
        }

		int overHardMobLimit = getPluginInstance().overHardMobLimit();

		if (overHardMobLimit > 0) {

			List<LivingEntity> mobs = getPluginInstance().getAllMobs();
            Collections.shuffle(mobs);

            int toKill = overHardMobLimit;
            for (LivingEntity mob : mobs) {
                if (   (! (mob instanceof Player))
                    && (! mob.isDead())) {
                    toKill--;
                    getPluginInstance().damageEntity(mob, 100);
                }
                if (toKill == 0)
                    break;
            }
		}


	}
	


}
