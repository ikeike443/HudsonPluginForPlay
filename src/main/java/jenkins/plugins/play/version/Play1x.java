/**
 * 
 */
package jenkins.plugins.play.version;

import org.kohsuke.stapler.DataBoundConstructor;

import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCommandDescriptor;
import hudson.Extension;
import hudson.util.DescribableList;

/**
 * @author rafaelrezende
 *
 */
public class Play1x extends PlayVersion {
	
	@DataBoundConstructor
	public Play1x(String value,
			DescribableList<PlayCommand, PlayCommandDescriptor> extensions) {
		super(value, extensions);
		// TODO Auto-generated constructor stub
	}

	@Extension
    public static class Play1xDescriptor extends PlayVersionDescriptor {

		protected static final String[] COMMAND_LIST = {"PLAY_CLEAN", "PLAY_COMPILE"};
		
		@Override
		public String getDisplayName() {
	        return "Play 1.x";
	    }
    }
}
