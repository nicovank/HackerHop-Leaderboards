package com.hackerhop.leaderboards.sql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * Provides useful variables to services that need to access the SQL Database.
 * If a service needs to access the database, import this class and use its static methods.
 */
public abstract class SQLConnection {
	private static final String DB_PROPERTIES_PATH = "config/database.properties";

	private static SQLQueryManager manager;
	private static Properties databaseProperties;

	// Loads the sql directory by default.
	static {
		try {
			manager = new SQLQueryManager("sql");
		} catch (IOException e) {
			manager = null;
		}

		try {
			databaseProperties = new Properties();

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Objects.requireNonNull(loader.getResource(DB_PROPERTIES_PATH)).openStream();
			InputStream input = Objects.requireNonNull(loader.getResource(DB_PROPERTIES_PATH)).openStream();

			databaseProperties.load(input);
		} catch (IOException e) {
			databaseProperties = null;
		}
	}

	/**
	 * Initiates a connection with the properties stored in the configuration file.
	 *
	 * @return a connection to the database.
	 * @throws IOException            if there was a problem reading the properties.
	 * @throws ClassNotFoundException if there was a problem loading the JBDC driver.
	 * @throws SQLException           if there was a problem connecting to the database.
	 */
	public static Connection getSQLConnection() throws IOException, ClassNotFoundException, SQLException {
		if (databaseProperties == null) {
			throw new IOException();
		}

		Class.forName(databaseProperties.getProperty("jbdc_driver"));

		return DriverManager.getConnection(
				databaseProperties.getProperty("connection_url"),
				databaseProperties.getProperty("user"),
				databaseProperties.getProperty("password")
		);
	}

	/**
	 * Returns the SQL query manager if it loaded successfully, else throw an exception.
	 *
	 * @return the SQL query manager
	 * @throws IOException if the queries could not be loaded properly.
	 */
	public static SQLQueryManager getManager() throws IOException {
		if (manager != null) {
			return manager;
		}

		throw new IOException();
	}

	/**
	 * Loads the query in the given folder, overriding all existing queries.
	 *
	 * @param directory Where to search for the queries.
	 * @throws IOException if the queries could not be loaded properly.
	 */
	public static void loadQueries(String directory) throws IOException {
		manager = new SQLQueryManager(directory);
	}
}
