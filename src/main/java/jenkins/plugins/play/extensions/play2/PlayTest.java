/**
 * 
 */
package jenkins.plugins.play.extensions.play2;

import jenkins.plugins.play.extensions.PlayExtension;
import jenkins.plugins.play.extensions.PlayExtensionDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * @author rafaelrezende
 *
 */
public class PlayTest extends PlayExtension {
	
	private final static String command = "test";
	
	@DataBoundConstructor
	public PlayTest() {
	}
	
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	@Extension
    public static class DescriptorImpl extends PlayExtensionDescriptor {
        @Override
        public String getDisplayName() {
            return "Execute all test cases [test]";
        }
	}
}
