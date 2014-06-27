package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play test command.
 */
public class PlayWar extends PlayCommand {
	
	@DataBoundConstructor
	public PlayWar() {
		super();
	}
	
	@Override
	public String getCommand() {
		return "war";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Export the application as a standalone WAR archive [war]";
        }
        
        public String getCommandId() {
        	return "PLAY_WAR";
        }
        
	}
}
