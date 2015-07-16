/*
 * © Koninklijke Philips N.V., 2015.
 *   All rights reserved.
 */

package com.philips.cdp.dicommclient.networknode;

/**
 *
 * @author Jeroen Mols
 * @date 28 Apr 2014
 */
public enum ConnectionState {
	CONNECTED_LOCALLY("local"), CONNECTED_REMOTELY("remote"), DISCONNECTED("disconnected");
	
	private String description = null;
	ConnectionState(String description) {
		this.description = description;
	}
	
	public String toString() {
		return description;
	}
}
