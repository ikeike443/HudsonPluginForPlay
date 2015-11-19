package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play test command.
 */
public class PlayJavadoc extends PlayCommand {
	
	@DataBoundConstructor
	public PlayJavadoc() {
		super();
	}
	
	@Override
	public String getCommand() {
		return "javadoc";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Generate application Javadoc [javadoc]";
        }
        
        public String getCommandId() {
        	return "PLAY_JAVADOC";
        }
        
	}
}
