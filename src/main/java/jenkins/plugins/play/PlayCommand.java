/**
 * 
 */
package jenkins.plugins.play;

import hudson.model.AbstractDescribableImpl;

/**
 * Abstract representation of a Play command. Every command implementing this
 * class must provide the respective command line (i.e.: 'clean', 'compile').
 */
public abstract class PlayCommand extends AbstractDescribableImpl<PlayCommand> {

	/** Play command (i.e.: test, package, dist). */
	protected String command = null;

	/**
	 * Additional parameter when required by the command (i.e.: the class name
	 * in test-only command).
	 */
	protected String parameter = "";

	/**
	 * @return Play command.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return Additional parameter of the command.
	 */
	public String getParameter() {
		return parameter;
	}

}
