package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play test command.
 */
public class PlayPrecompile extends PlayCommand {
	
	@DataBoundConstructor
	public PlayPrecompile() {
		super();
	}
	
	@Override
	public String getCommand() {
		return "precompile";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Precompile all Java sources and templates [precompile]";
        }
        
        public String getCommandId() {
        	return "PLAY_PRECOMPILE";
        }
        
	}
}
