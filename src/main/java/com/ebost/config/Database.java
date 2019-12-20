/**
 * 
 */
package com.ebost.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author nguyenduy
 *
 */
public class Database {

	private Connection connection = null;

	public static Database instance = null;

	private Database() {
		this.connection = newConnection();
	}

	public static synchronized Database getInstance() {
		if (instance == null) {
			instance = new Database();
			return instance;
		}

		instance.checkConnection();
		return instance;

	}

	private void checkConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = newConnection();
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private synchronized Connection newConnection() {
		String url = "jdbc:postgresql://localhost:5432/ebost?currentSchema=ebost";
		String username = "postgres";
		String password = "root1111";

		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void finalize() {
		try {
			connection.close();
		} catch (SQLException e) {

		}
		System.out.println("Connection closed");
	}

	public Connection getConnection() {
		return connection;
	}
}
