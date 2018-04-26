package com.hackerhop.leaderboards.servlets;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.hackerhop.leaderboards.api.Scores;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.HashMap;

public class DisplayScores extends HttpServlet {

	private MustacheFactory mustache;
	private Mustache template;

	@Override
	public void init() throws ServletException {
		mustache = new DefaultMustacheFactory();
		template = mustache.compile("templates/scores.html");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Writer out = response.getWriter();

		try {

			HashMap<String, Object> context = new HashMap<>();
			context.put("scores", Scores.list(0, 50));
			template.execute(out, context);

		} catch (IOException | SQLException | ClassNotFoundException e) {
			throw new ServletException(e);
		}
	}
}
