/**
 * 
 */
package jenkins.plugins.play;

/**
 * @author rafaelrezende
 * 
 */
public enum PlayTarget {

	// Default target
	NONE("", ""),

	// ERROR MESSAGES
	PLAY_1_X("play", "1."),
	PLAY_2_X("play", "2.2."),
	PLAY_2_3("activator", "2.2.3");

	/**
	 * Play executable. (i.e.: 'play' from first version until v2.2.3. Then 'activator')
	 */
	private String executable;
	/**
	 * Regular expression to identify the Play! version
	 */
	private String versionRegex;
	
	/**
	 * @return the executable
	 */
	public final String getExecutable() {
		return executable;
	}

	/**
	 * @return the versionRegex
	 */
	public final String getVersionRegex() {
		return versionRegex;
	}

	/**
	 * Private constructor. No external object can create targets.
	 * 
	 * @param code 
	 * @param message
	 */
	private PlayTarget(String code, String message) {
		this.executable = code;
		this.versionRegex = message;
	}

	@Override
	public String toString() {
		return executable + ": " + versionRegex;
	}
}
