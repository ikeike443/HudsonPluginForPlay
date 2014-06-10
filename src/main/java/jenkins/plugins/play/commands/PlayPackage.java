package jenkins.plugins.play.commands;

import java.util.Arrays;

import jenkins.plugins.play.PlayCommand;
import jenkins.plugins.play.PlayCommandDescriptor;
import jenkins.plugins.play.PlayTarget;

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
    }
}
