package com.hackerhop.leaderboards.sql;

import com.hackerhop.leaderboards.sql.utils.ArrayPlus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

/**
 * This helper class will allow to put SQL queries in separate files,
 * and call them using the filename they are stored in.
 * <p>
 * <p>
 * The given directory will be recursively traversed, and the id of any given file will
 * be its path from the root, with directories concatenated using periods.
 * <p>
 * <p>
 * For example, a file stored in <code>ROOT/init/table.sql</code> can be fetched using
 * <code>getSQLQuery("init.table")</code>.
 */
public class SQLQueryManager {

	private final static class Node {
		String directory;
		HashMap<String, Node> children = new HashMap<>();
		HashMap<String, String> queries = new HashMap<>();

		public Node(String directory) {
			this.directory = directory;
		}
	}

	private Node root;

	private static final String FILE_EXTENSION = ".sql";

	/**
	 * Creates a new SQL query manager.
	 *
	 * @param path the path where the SQL queries are stored.
	 * @throws IOException if the given path is not a directory, or cannot be found, or other problem reading the files.
	 */
	public SQLQueryManager(String path) throws IOException {
		// Start loading queries from the root directory.
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			URI absolutePathURI = Objects.requireNonNull(loader.getResource(path)).toURI();
			Path absolutePath = Paths.get(absolutePathURI);

			root = new Node(null);
			walkDirectory(absolutePath, root);
		} catch (URISyntaxException e) {
			throw new IOException();
		}
	}

	/**
	 * Returns the query associated with the given id, or null if it does not exist.
	 *
	 * @param id the id to look up for.
	 * @return the associated query, or null if it does not exist.
	 */
	public String getSQLQuery(String id) {
		return getSQLQuery(new ArrayPlus<>(id.split("\\.")), root);
	}

	public String getSQLQuery(ArrayPlus<String> path, Node node) {
		if (path.size() == 1) {
			return node.queries.get(path.get(0));
		} else {
			Node child = node.children.get(path.get(0));
			if (child != null) {
				return getSQLQuery(path.slice(1), child);
			}
		}

		return null;
	}

	/**
	 * Walks the given directory adding all files ending with the proper extension to the HashMap.
	 * The id is built from the prefix and the filename.
	 *
	 * @param directory The directory to start loading from.
	 * @param node      The current node / directory being visited.
	 * @throws IOException if the given path is not a directory, or cannot be found, or other problem reading the files.
	 */
	private void walkDirectory(Path directory, Node node) throws IOException {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					// Recursively walk through the directories
					String directoryName = entry.getFileName().toString();
					Node newNode = new Node(directoryName);
					node.children.put(directoryName, newNode);
					walkDirectory(entry, newNode);

				} else {
					if (entry.toString().endsWith(FILE_EXTENSION)) {
						String id = entry.getFileName().toString().replace(FILE_EXTENSION, "");

						String content = readFile(entry.toAbsolutePath().toString());
						node.queries.put(id, content);
					}
				}
			}
		}
	}

	/**
	 * Reads a file and returns its content as a String decoded with the UTF-8 charset.
	 *
	 * @param path The path to the file to read.
	 * @return the contents of the file.
	 * @throws IOException if there is an error accessing or reading the file.
	 */
	private static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, "UTF-8");
	}
}