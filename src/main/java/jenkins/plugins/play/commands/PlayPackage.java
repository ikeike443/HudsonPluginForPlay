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
		// overriding inherited command 
		this.command = "package";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
		public DescriptorImpl() {
			// This command is compatible with the following versions...
			this.compatibleVersions = Arrays.asList(PlayTarget.PLAY_1_X, PlayTarget.PLAY_2_X);
		}
		
        @Override
        public String getDisplayName() {
            return "Generate artifact [package]";
        }
    }
}
