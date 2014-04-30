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
public class PlayPublish extends PlayExtension {
	
	private String command = "publish";
	
	@DataBoundConstructor
	public PlayPublish() {
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
            return "Publish artifact to repository [publish]";
        }
    }
}
