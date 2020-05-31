/**
 * 
 */
package jenkins.plugins.play.version;

import java.io.File;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import jenkins.plugins.play.PlayBuilder;
import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCommandDescriptor;
import hudson.Extension;
import hudson.util.FormValidation;

/**
 * @author rafaelrezende
 * 
 */
public class Play2x extends PlayVersion {

	@DataBoundConstructor
	public Play2x(List<PlayCommand> commands) {
		super(commands);
	}
	
	@Extension
	public static class Play2xDescriptor extends PlayVersionDescriptor {
		
		public static final String[] COMMAND_LIST = { "PLAY_CLEAN", "PLAY_COMPILE", "PLAY_DIST", "PLAY_PACKAGE", "PLAY_PUBLISH", "PLAY_TEST", "PLAY_TESTONLY", "PLAY_CUSTOM" };
		
		@Override
		public String getDisplayName() {
	        return "Play 2.x";
	    }
		
		/**
		 * Goals are Implemented as extensions. This methods returns the
		 * descriptor of every available extension.
		 * 
		 * @return Available goals.
		 */
		public List<PlayCommandDescriptor> getCommandDescriptors() {
			
			return super.getCommandDescriptors(Play2xDescriptor.COMMAND_LIST);
		}
	}
}
