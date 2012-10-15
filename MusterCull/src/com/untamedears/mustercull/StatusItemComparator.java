package com.untamedears.mustercull;

import java.util.Comparator;

/**
 * Provides comparison features for two StatusItem objects
 * @author Celdecea
 *
 */
public class StatusItemComparator implements Comparator<StatusItem> {
	
	/**
	 * Handles comparison between the two StatusItem objects
	 */
    @Override
    public int compare(StatusItem o1, StatusItem o2) {
    	return new Integer(o1.getNearbyEntityCount()).compareTo(o2.getNearbyEntityCount());
    }
}

