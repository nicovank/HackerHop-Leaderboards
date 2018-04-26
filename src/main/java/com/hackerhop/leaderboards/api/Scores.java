package com.hackerhop.leaderboards.api;

import com.hackerhop.leaderboards.api.models.Score;
import com.hackerhop.leaderboards.sql.SQLConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Scores {

	/**
	 * Lists the scores, skipping over a certain amount of records, and stopping after a certain quantity of records has been listed.
	 *
	 * @param skip     The amount of scores to skip.
	 * @param quantity The number of scores to retrieve.
	 * @return a sorted ArrayList of scores
	 * @throws SQLException           if there was a problem communicating with the database.
	 * @throws IOException            if the properties file could not be read.
	 * @throws ClassNotFoundException if the JBDC driver could not be found.
	 */
	public static ArrayList<Score> list(int skip, int quantity) throws SQLException, IOException, ClassNotFoundException {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try {
			connection = SQLConnection.getSQLConnection();
			statement = connection.prepareStatement(SQLConnection.getManager().getSQLQuery("list"));
			statement.setInt(1, skip);
			statement.setInt(2, quantity);

			results = statement.executeQuery();
			ArrayList<Score> scores = new ArrayList<>();

			while (results.next()) {
				Score score = new Score();

				score.setCharacter(results.getString("Player"));
				score.duration = results.getLong("Duration");
				score.score = results.getLong("Score");
				score.name = results.getString("PlayerName");
				score.date = results.getTimestamp("Time_Recorded").toLocalDateTime();

				scores.add(score);
			}

			return scores;

		} catch (IOException | SQLException | ClassNotFoundException e) {
			throw e;
		} finally {
			if (results != null) results.close();
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}
	}

	/**
	 * Inserts a score into the database.
	 *
	 * @param score The score information to be added.
	 * @return true if the score was successfully saved, else false.
	 * @throws SQLException           if there was a problem communicating with the database.
	 * @throws IOException            if the properties file could not be read.
	 * @throws ClassNotFoundException if the JBDC driver could not be found.
	 */
	public static boolean add(Score score) throws SQLException, IOException, ClassNotFoundException {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = SQLConnection.getSQLConnection();
			statement = connection.prepareStatement(SQLConnection.getManager().getSQLQuery("add"));

			statement.setString(1, score.character.name());
			statement.setLong(2, score.score);
			statement.setLong(3, score.duration);
			statement.setString(4, score.name);

			return statement.executeUpdate() == 1;

		} catch (IOException | SQLException | ClassNotFoundException e) {
			throw e;
		} finally {
			if (statement != null) statement.close();
			if (connection != null) connection.close();
		}
	}
}
