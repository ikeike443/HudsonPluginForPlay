/**
 * 
 */
package jenkins.plugins.play.extensions;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * @author rafaelrezende
 *
 */
public class PlayTest extends PlayExtension {
	
	private String command = "test";
	
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
            return "Test project";
        }
    }
}
