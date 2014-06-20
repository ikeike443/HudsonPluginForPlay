package jenkins.plugins.play.version;

import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCommandDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Saveable;
import hudson.util.DescribableList;

/**
 * Abstract representation of a Play command. Every command implementing this
 * class must provide the respective command line (i.e.: 'clean', 'compile').
 */
public abstract class PlayVersion extends AbstractDescribableImpl<PlayVersion> {
	
private final String value;
	
	/** All the configured extensions attached to this. */
	private DescribableList<PlayCommand, PlayCommandDescriptor> extensions;
	
	/**
	 * 
	 */
	@DataBoundConstructor
	public PlayVersion(String value, 
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

}
