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
public class PlayPackage extends PlayExtension {
	
	private String command = "package";
	
	@DataBoundConstructor
	public PlayPackage() {
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
            return "Generate artifact [package]";
        }
    }
}
