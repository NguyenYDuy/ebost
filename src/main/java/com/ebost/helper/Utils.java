/**
 * 
 */
package com.ebost.helper;

import java.util.Base64;


import org.mindrot.jbcrypt.BCrypt;

import com.ebost.model.User;

/**
 * @author nguyenduy
 *
 */
public class Utils {

	public static String hashPassword(String password) {
		
		String salt = BCrypt.gensalt(10);
		return BCrypt.hashpw(password, salt);
	}
	
	public static String encodeToken(User user) {
		byte[] encodedBytes = Base64.getEncoder().encode(user.getUsername().getBytes());
		return new String(encodedBytes);
	}
	
	public static String decodeToken(String token) {
		byte[] decodedBytes = Base64.getDecoder().decode(token);
		String decode = new String(decodedBytes);
		return decode;
	}
}
