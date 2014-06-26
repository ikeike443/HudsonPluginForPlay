package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play package command.
 */
public class PlayPackage extends PlayCommand {
	
	@DataBoundConstructor
	public PlayPackage() {
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Generate artifact [package]";
        }
        
        public String getCommandId() {
        	return "PLAY_PACKAGE";
        }
        
        public String getCommand() {
        	return "package";
        }
    }
}
