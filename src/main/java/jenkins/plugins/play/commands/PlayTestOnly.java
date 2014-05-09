package jenkins.plugins.play.commands;

import java.util.Arrays;

import jenkins.plugins.play.PlayCommand;
import jenkins.plugins.play.PlayCommandDescriptor;
import jenkins.plugins.play.PlayTarget;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;

/**
 * Represents the Play test-only command.
 */
public class PlayTestOnly extends PlayCommand {
	
	@DataBoundConstructor
	public PlayTestOnly(String parameter) {
		this.parameter = parameter;
		// overriding inherited command and supported versions
		this.command = "test-only";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
		public DescriptorImpl() {
			// This command is compatible with the following versions...
			this.compatibleVersions = Arrays.asList(PlayTarget.PLAY_1_X, PlayTarget.PLAY_2_X);
		}

		@Override
        public String getDisplayName() {
            return "Execute single test case [test-only]";
        }
        
        public FormValidation doCheckParameter (@QueryParameter String parameter) {
        	return FormValidation.validateRequired(parameter);
        }
    }
}
