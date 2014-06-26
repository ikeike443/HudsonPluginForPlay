/**
 * 
 */
package jenkins.plugins.play.version;

import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCommandDescriptor;
import hudson.Extension;

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
		
		public static final String[] COMMAND_LIST = { "PLAY_CLEAN", "PLAY_COMPILE" };
		
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
