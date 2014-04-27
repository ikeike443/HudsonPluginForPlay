/**
 * 
 */
package jenkins.plugins.play.extensions;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;

/**
 * @author rafaelrezende
 *
 */
public class PlayTestOnly extends PlayExtension {
	
	private final static String command = "test";
	
	private final String testOnlyId; 
	
	@DataBoundConstructor
	public PlayTestOnly(String testOnlyId) {
		this.testOnlyId = testOnlyId;
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
	public final String getTestOnlyId() {
		return testOnlyId;
	}

	@Extension
    public static class DescriptorImpl extends PlayExtensionDescriptor {
        @Override
        public String getDisplayName() {
            return "Execute single test case";
        }
        
        public FormValidation doCheckTestOnlyId (@QueryParameter String testOnlyId) {
        	return FormValidation.validateRequired(testOnlyId);
        }
    }
}
