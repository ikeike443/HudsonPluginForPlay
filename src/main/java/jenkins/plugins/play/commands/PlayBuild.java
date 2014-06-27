package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play package command.
 */
public class PlayBuild extends PlayCommand {
	
	@DataBoundConstructor
	public PlayBuild() {
		super();
	}
	
	@Override
	public String getCommand() {
		return "build-module";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Build and package a module [build-module]";
        }
        
        public String getCommandId() {
        	return "PLAY_BUILD";
        }
        
    }
}
