 /**
 * 
 */
package jenkins.plugins.play.extensions.play2;

import jenkins.plugins.play.extensions.PlayExtension;
import jenkins.plugins.play.extensions.PlayExtensionDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;

/**
 * @author rafaelrezende
 *
 */
public class PlayTestOnly extends PlayExtension {
	
	private final static String command = "test-only";
	
	private final String parameter; 
	
	@DataBoundConstructor
	public PlayTestOnly(String parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * @return the testOnlyName
	 */
	public final String getParameter() {
		return parameter;
	}

	@Extension
    public static class DescriptorImpl extends PlayExtensionDescriptor {
        @Override
        public String getDisplayName() {
            return "Execute single test case [test-only]";
        }
        
        public FormValidation doCheckParameter (@QueryParameter String parameter) {
        	return FormValidation.validateRequired(parameter);
        }
    }
}
