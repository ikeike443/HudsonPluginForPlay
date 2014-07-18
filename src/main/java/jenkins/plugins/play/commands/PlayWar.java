package jenkins.plugins.play.commands;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;

/**
 * Represents the Play test command.
 */
public class PlayWar extends PlayCommand {
	
	@DataBoundConstructor
	public PlayWar(String parameter) {
		super();
		this.parameter = parameter;
	}
	
	@Override
	public String getCommand() {
		return "war -o";
	}
	
	@Extension
    public static class DescriptorImpl extends PlayCommandDescriptor {
		
        @Override
        public String getDisplayName() {
            return "Export the application as a standalone WAR archive [war]";
        }
        
        public String getCommandId() {
        	return "PLAY_WAR";
        }
        
        public FormValidation doCheckParameter (@QueryParameter String parameter) {
        	return FormValidation.validateRequired(parameter);
        }
        
	}
}
