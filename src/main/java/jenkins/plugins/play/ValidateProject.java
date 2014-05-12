/**
 * 
 */
package jenkins.plugins.play;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * This class keeps the formatting parameters of the project information shown
 * in the Jenkins job configuration.
 */
public final class ValidateProject {

	/** Opening tag of the paragraphs shown in the project information */
	private static final String P_FORMAT = "<p style=\"color:green; margin-left:10px; margin-top:-5px; font-size:10px;\">";
	/** Closing tag of the paragraphs shown in the project information */
	private static final String P_FORMAT_CLOSE = "</p>";

	/** Opening tag of the title shown in the project information */
	private static final String TITLE_FORMAT = "<p style=\"margin-left:20px; margin-top:-10px; font-size:11px;\"><b>";
	/** Closing tag of the title shown in the project information */
	private static final String TITLE_FORMAT_CLOSE = "</b></p>";

	/** Title of the project information shown in the job configuration */
	private static final String TITLE = "Project info";

	/**
	 * Generates the Project information shown in the job configuration. This
	 * function should be invoked only when the project path exists. It
	 * validates if the project path is a Play! project. If yes, it returns the
	 * composed HTML containing the project information. Null otherwise.
	 * 
	 * @param playExecutable
	 *            Path of the selected Play!Framework installation.
	 * @param projectPath
	 *            Path of the Play! project.
	 * @return The HTML-formatted project information. It returns null if the
	 *         path is not a Play!Project.
	 */
	public static String formattedInfo(String playExecutable, String projectPath) {

		// This parameter is always present to remove color formatting
		// characters from the output.
		String noColorFormatting = "-Dsbt.log.noformat=true";

		// Compose the command-line to invoke the 'Play about' without color
		// formatting
		List<String> args = new LinkedList<String>();
		args.add(playExecutable);
		args.add(noColorFormatting);
		args.add("about");

		// Run the composed command
		Process process = null;
		try {
			process = new ProcessBuilder(args).directory(new File(projectPath))
					.start();
			// Wait for it to finish
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Process failed...
		if (process == null || process.exitValue() != 0)
			return null;

		// Retrieve output (no stderr is taken into account. If the exit code is
		// 0, the command has run successfully)
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bisr = new BufferedReader(isr);

		// Read line by line adding the html tags
		List<String> lines = new ArrayList<String>();
		try {
			String line;
			while ((line = bisr.readLine()) != null) {
				// If the String contains information about "resolving" dependencies, skip it.
				if (line.contains("Resolving"))
					continue;
				
				// Otherwise...
				// The [info] tag from Play output and the whitespaces at the
				// beginning and the end of String should be removed.
				lines.add(P_FORMAT + line.replace("[info]", "").trim()
						+ P_FORMAT_CLOSE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Removing the first line and the last two lines, which should not be
		// shown in the interface.
		// Note: run your own 'Play about' in a Play project to see which lines
		// are currently omitted.
		lines = lines.subList(1, lines.size() - 2);

		// Add title of the Project information
		lines.add(0, TITLE_FORMAT + TITLE + TITLE_FORMAT_CLOSE);

		// Join lines with no separator
		return StringUtils.join(lines, "");
	}

}
