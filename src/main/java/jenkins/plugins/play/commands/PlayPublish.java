package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play publish command.
 */
public class PlayPublish extends PlayCommand {
	
	@DataBoundConstructor
	public PlayPublish() {
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Publish artifact to repository [publish]";
        }
        
        public String getCommandId() {
        	return "PLAY_PUBLISH";
        }
        
        public String getCommand() {
        	return "publish";
        }
    }
}
