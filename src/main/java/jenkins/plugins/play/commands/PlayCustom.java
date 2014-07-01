package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play test command.
 */
public class PlayCustom extends PlayCommand {
	
	@DataBoundConstructor
	public PlayCustom() {
		super();
	}
	
	@Override
	public String getCommand() {
		return "";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Custom parameter";
        }
        
        public String getCommandId() {
        	return "PLAY_CUSTOM";
        }
        
	}
}
