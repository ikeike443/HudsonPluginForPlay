package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;

/**
 * Represents the Play test command.
 */
public class PlayCustom extends PlayCommand {
	
	@DataBoundConstructor
	public PlayCustom(String parameter) {
		super();
		this.parameter = parameter;
	}
	
	@Override
	public String getCommand() {
		return "";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Custom parameter";
        }
        
        public String getCommandId() {
        	return "PLAY_CUSTOM";
        }
        
        public FormValidation doCheckParameter (@QueryParameter String parameter) {
        	return FormValidation.validateRequired(parameter);
        }
        
	}
}
