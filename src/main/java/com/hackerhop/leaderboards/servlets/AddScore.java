package com.hackerhop.leaderboards.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackerhop.leaderboards.api.Scores;
import com.hackerhop.leaderboards.api.models.Score;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AddScore extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		Score score = mapper.readValue(request.getReader(), Score.class);

		System.out.println("Received request to add score of " + score.score + " for '" + score.name + "'.");

		try {
			if(Scores.add(score)) {
				System.out.println("Score successfully added.");
			} else {
				throw new ServletException("The score could not be added.");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
}
