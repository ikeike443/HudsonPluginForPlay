package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play clean command.
 */
public class PlayClean extends PlayCommand {
	
	@DataBoundConstructor
	public PlayClean() {
		super();
	}

	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Clean project [clean]";
        }
        
        public String getCommandId() {
        	return "PLAY_CLEAN";
        }
    }
}
