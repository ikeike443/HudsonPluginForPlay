package jenkins.plugins.play.commands;

import java.util.Arrays;

import jenkins.plugins.play.PlayCommand;
import jenkins.plugins.play.PlayCommandDescriptor;
import jenkins.plugins.play.PlayTarget;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

/**
 * Represents the Play compile command.
 */
public class PlayCompile extends PlayCommand {
	
	@DataBoundConstructor
	public PlayCompile() {
		// overriding inherited command 
		this.command = "compile";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
		public DescriptorImpl() {
			// This command is compatible with the following versions...
			this.compatibleVersions = Arrays.asList(PlayTarget.PLAY_1_X, PlayTarget.PLAY_2_X);
		}
		
        @Override
        public String getDisplayName() {
            return "Compile project [compile]";
        }
    }
}
