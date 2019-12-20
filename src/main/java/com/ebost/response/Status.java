/**
 * 
 */
package com.ebost.response;

/**
 * @author nguyenduy
 *
 */
public enum Status {

	SUCCESSFUL(200),
	NOTFOUND(404),
	UNAUTHORIZED(401);
	
	public final int status;
	
	private Status (int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
}
