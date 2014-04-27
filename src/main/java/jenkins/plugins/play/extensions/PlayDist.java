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
public class PlayDist extends PlayExtension {
	
	private String command = "dist";
	
	@DataBoundConstructor
	public PlayDist() {
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
            return "Build an Akka kernel project";
        }
    }
}
