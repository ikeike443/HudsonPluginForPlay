package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play test command.
 */
public class PlayAutoTest extends PlayCommand {
	
	@DataBoundConstructor
	public PlayAutoTest() {
		super();
	}
	
	@Override
	public String getCommand() {
		return "auto-test";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Automatically run all application tests [auto-test]";
        }
        
        public String getCommandId() {
        	return "PLAY_AUTOTEST";
        }
        
	}
}
