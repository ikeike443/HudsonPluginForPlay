/**
 * 
 */
package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represent the Play distribute command.
 */
public class PlayDist extends PlayCommand {
	
	@DataBoundConstructor
	public PlayDist() {
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Build an Akka kernel project [dist]";
        }
        
        public String getCommandId() {
        	return "PLAY_DIST";
        }
    }
}
