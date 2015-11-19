package jenkins.plugins.play.commands;

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
		super();
		this.parameter = parameter;
	}
	
	@Override
	public String getCommand() {
		return "testOnly";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
		@Override
        public String getDisplayName() {
            return "Execute single test case [test-only]";
        }
		
		public String getCommandId() {
			return "PLAY_TESTONLY";
		}
		
        public FormValidation doCheckParameter (@QueryParameter String parameter) {
        	return FormValidation.validateRequired(parameter);
        }
    }
}
