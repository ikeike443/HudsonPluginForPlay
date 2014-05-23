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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jenkins.model.Jenkins;

import org.apache.commons.lang.StringUtils;

/**
 * @author rafaelrezende
 * 
 */
public class ValidatePlayTarget {
	
	/**
	 * Maximum number of expected parts in the Play! version. (i.e. "2.2.1" has
	 * three parts). If the version has more than 6 parts, it's simply
	 * concatenated. So, this parameters can influence the comparison between
	 * versions.
	 */
	private static final int VERSION_RANGE_PARTS = 6;
	
	/**
	 * Identifies the Play! installation version according to the expected
	 * executable and ranges provided by the {@link PlayTarget}.
	 * 
	 * @param playPath
	 *            Corresponds to the PLAY_HOME
	 * 
	 * @return Corresponding Play version target.
	 */
	public static PlayTarget getPlayTarget(String playPath) {
		
		// compare the current version to the given targets until a suitable
		// one has been found.
		for (PlayTarget target : PlayTarget.values()) {
			
			// Check if the playPath has the expected executable for this target
			File executable = new File(playPath + "/" + target.getExecutable());
			
			System.out.println("######target.getExecutable(): " + target.getExecutable());
			System.out.println("######executable: " + executable);
			
			// For instance, if the target expects a "play" executable,
			// but doesn't find it, skip it
			if (!executable.exists())
				continue;
			
			// otherwise, check if the found version is in the range
			String playOutput = null;
			try {
				playOutput = getPlayVersion(executable);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// validating the play output
			if (playOutput == null)
				return PlayTarget.NONE;
			
			if (compareVersions(playOutput, target.getVersionRangeMin(), target.getVersionRangeMax()))
				return target;
		}
		
		// no suitable target has been found. Return none.
		return PlayTarget.NONE;
	}

	/**
	 * Execute the play command directly in Play!Framework path. Until now, all
	 * the Play! release are expected to output the version among other info.
	 * This method parses the output of the command for the version only, which
	 * is returned as String.
	 * 
	 * @param playPath Corresponds to the PLAY_HOME
	 * @return String with the version of the Play installation.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static String getPlayVersion(File playPath) throws IOException,
			InterruptedException {

		// Add play executable to the command line
		List<String> args = new LinkedList<String>();
		args.add(playPath.getAbsolutePath());
		
		System.out.println("########playPath.getAbsolutePath(): " + playPath.getAbsolutePath());
		System.out.println("########playPath: " + playPath);
		
		// Run the "play about" command in the root folder of Jenkins
		Process process = new ProcessBuilder(args)
				.directory(Jenkins.getInstance().getRootDir()).start();
		// Wait for it to finish
		process.waitFor();

		// Retrieve output (stdout only)
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bisr = new BufferedReader(isr);

		// Read line by line
		List<String> lines = new ArrayList<String>();
		String line;
		while ((line = bisr.readLine()) != null)
			lines.add(line);

		// Close streams and readers
		bisr.close();
		isr.close();
		is.close();

		// Merge lines into a single string
		String playOutput = StringUtils.join(lines, "\n");
		
		// Parse the version using regex. Example of expected exp: 'play 2.2.1', 'play! 1.2.3'
		// Only the number is extracted.
		Matcher versionMatch = Pattern.compile("play[!]?\\s(\\d[\\.\\d]+)", Pattern.CASE_INSENSITIVE).matcher(playOutput);
		
		// find the first pattern match and return null if nothing has been
		// found
		if (!versionMatch.find())
			return null;
		
		// return the version only in the regex above
		return versionMatch.group(1);
	}
	
	/**
	 * Checks if a given version fits in range. Both limit versions are
	 * inclusive. If the current version is empty, it means that there are no
	 * version requirements, so the result is true for any given range.
	 * 
	 * @param currVersion
	 *            Version to be compared.
	 * @param minVersion
	 *            Minimum version of the reference range.
	 * @param maxVersion
	 *            Maximum version of the reference range.
	 * @return True if the version fits in the range. False otherwise.
	 */
	public static boolean compareVersions(String currVersion, String minVersion, String maxVersion) {
		
		// If the current version is empty, it means that there are no
		// version requirements, so the result is true for any given range.
		if (currVersion.isEmpty())
			return true;
		
		// validate ranges.
		if (minVersion.isEmpty() || maxVersion.isEmpty())
			return false;
		
		long minVersionLong = versionToLong(minVersion, false);
		long maxVersionLong = versionToLong(maxVersion, true);
		long currVersionLong = versionToLong(currVersion, false);
		
		if (currVersionLong >= minVersionLong && currVersionLong <= maxVersionLong)
			return true;
		
		else return false;
	}
	
	/**
	 * This method converts a version in String format to a long format for easy
	 * comparison between versions.
	 * 
	 * It easier to compare versions as numbers. So, the ranges and the current
	 * versions are first normalized to the same amount of parts, which isn't
	 * expected to be more than {@link #VERSION_RANGE_PARTS}. Min: 1.1 =>
	 * 01.01.00.00.00.00 Max: 1.3.4 => 01.03.04.99.99.99 Cur: 1.2.2 =>
	 * 01.02.02.00.00.00
	 * 
	 * Then every part n is multiplied by 100^n and summed up. So: Min:
	 * 010100000000 Max: 010304999999 Cur: 010202000000
	 * 
	 * This way it's easier to compare the ranges.
	 * 
	 * @param version
	 *            Version to transform
	 * @param isMaxVersion
	 *            True if the version is the upper range (completed with 99),
	 *            false otherwise (completed with 00).
	 * @return Version in long format.
	 */
	public static long versionToLong(String version, boolean isMaxVersion) {
		
		// Holds the long version
		long versionLong = 0;
		
		// Holds the version as array of ints
		int[] versionSplitComplete = new int[VERSION_RANGE_PARTS];
		
		// Holds the version as array of Strings
		String[] versionSplit = version.split("\\.");
		
		for (int i = 0; i < versionSplitComplete.length; i++) {
			// fill up the versionSplitComplete while the versionSplit has parts
			if (i < versionSplit.length)
				versionSplitComplete[i] = Integer.parseInt(versionSplit[i]);
			// then fill up with 00 (min version) or 99 (max version)
			else if (!isMaxVersion)
				versionSplitComplete[i] = 0;
			else versionSplitComplete[i] = 99;
		}
		
		// Multiply and sum up every member by its 100 power 
		for (int i = 0; i<VERSION_RANGE_PARTS; i++)
			versionLong += versionSplitComplete[i]*Math.pow(100,(VERSION_RANGE_PARTS - 1 - i));
		
		return versionLong;
	}
}
