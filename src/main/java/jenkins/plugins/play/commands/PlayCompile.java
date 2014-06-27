package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play compile command.
 */
public class PlayCompile extends PlayCommand {
	
	@DataBoundConstructor
	public PlayCompile() {
		super();
	}
	
	@Override
	public String getCommand() {
		return "compile";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Compile project [compile]";
        }
        
        public String getCommandId() {
        	return "PLAY_COMPILE";
        }
        
    }
}
