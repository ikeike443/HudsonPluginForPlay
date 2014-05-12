/**
 * 
 */
package jenkins.plugins.play;

/**
 * Play targets are used to identify which commands are available for a provided
 * Play! installation in the job configuration of Jenkins. Every play target has
 * a range of versions and a specific executable name.
 * 
 * For instance, if the selected Play! installation is identified to be "2.2.1",
 * it will fit in the range of 2-2.2 below and the 'play' executable will be
 * used.
 * A range "1-1" includes every version starting with 1 (i.e.: '1.2.3')
 */
public enum PlayTarget {

	// Default target
	NONE("", "0.0.0", "0.0.0"),

	// CUSTOM TARGETS
	// Any version started with "1."
	PLAY_1_X("play", "1", "1"),
	// Any version started with "2.x.", with x from 0 to 2
	PLAY_2_X("play", "2", "2.2"),
	// Any version started with "2.x." with x from 3 to 99.
	PLAY_2_3("activator", "2.3", "2.3");

	/**
	 * Play executable. (i.e.: 'play' from first version until v2.2.3. Then
	 * 'activator')
	 */
	private String executable;

	private String versionRangeMin;
	
	private String versionRangeMax;

	/**
	 * @return the executable
	 */
	public final String getExecutable() {
		return executable;
	}

	/**
	 * @return the versionRangeMin
	 */
	public final String getVersionRangeMin() {
		return versionRangeMin;
	}

	/**
	 * @return the versionRangeMax
	 */
	public final String getVersionRangeMax() {
		return versionRangeMax;
	}

	/**
	 * Private constructor. No external object can create targets.
	 * 
	 * @param code
	 * @param message
	 */
	private PlayTarget(String code, String versionRangeMin, String versionRangeMax) {
		this.executable = code;
		this.versionRangeMin = versionRangeMin;
		this.versionRangeMax = versionRangeMax;
	}

	@Override
	public String toString() {
		return executable + ": " + versionRangeMin;
	}
}
