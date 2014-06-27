package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play clean command.
 */
public class PlayInstall extends PlayCommand {
	
	@DataBoundConstructor
	public PlayInstall() {
		super();
	}

	@Override
	public String getCommand() {
		return "install";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Install a module [install]";
        }
        
        public String getCommandId() {
        	return "PLAY_INSTALL";
        }
        
    }
}
