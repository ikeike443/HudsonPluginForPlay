/**
 * 
 */
package jenkins.plugins.play.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenkins.model.Jenkins;
import jenkins.plugins.play.PlayBuilder;
import jenkins.plugins.play.PlayCommand;
import jenkins.plugins.play.PlayCommandDescriptor;
import jenkins.plugins.play.PlayTarget;
import jenkins.plugins.play.ValidatePlayTarget;
import jenkins.plugins.play.commands.PlayClean;
import jenkins.plugins.play.commands.PlayCompile;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Util;
import hudson.model.Saveable;
import hudson.util.DescribableList;
import hudson.util.DescriptorList;

/**
 * @author rafaelrezende
 *
 */
public class Play1x extends PlayVersion {
	
	
    private final String value;
	
	/** All the configured extensions attached to this. */
	private DescribableList<PlayCommand, PlayCommandDescriptor> extensions;
	
	/**
	 * 
	 */
	@DataBoundConstructor
	public Play1x(String value, 
			DescribableList<PlayCommand, PlayCommandDescriptor> extensions) {
		this.value = value;
		this.extensions = new DescribableList<PlayCommand, PlayCommandDescriptor>(
				Saveable.NOOP, Util.fixNull(extensions));
	}
	
	/**
	 * @return the value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * @return the extensions
	 */
	public final DescribableList<PlayCommand, PlayCommandDescriptor> getExtensions() {
		return extensions;
	}
	
	@Extension
    public static class Play1xDescriptor extends PlayVersionDescriptor {

		public static final String VERSION_ID = "PLAY_1X";
		
		protected static final String[] COMMAND_LIST = {"PLAY_CLEAN", "PLAY_COMPILE"};
    }
}
