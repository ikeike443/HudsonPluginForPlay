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
public class Play1x extends PlayVersion {
	
	@DataBoundConstructor
	public Play1x(List<PlayCommand> commands) {
		super(commands);
	}

	@Extension
    public static class Play1xDescriptor extends PlayVersionDescriptor {

		public static final String[] COMMAND_LIST = { "PLAY_CLEAN", "PLAY_TEST", "PLAY_AUTOTEST", "PLAY_BUILD", "PLAY_JAVADOC", "PLAY_PRECOMPILE", "PLAY_WAR" };
		
		@Override
		public String getDisplayName() {
	        return "Play 1.x";
	    }
		
		/**
		 * Goals are Implemented as extensions. This methods returns the
		 * descriptor of every available extension.
		 * 
		 * @return Available goals.
		 */
		public List<PlayCommandDescriptor> getCommandDescriptors() {
			
			return super.getCommandDescriptors(Play1xDescriptor.COMMAND_LIST);
		}
    }
}
