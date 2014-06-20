package jenkins.plugins.play.version;

import java.util.ArrayList;
import java.util.List;

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
	
	/** All the configured extensions attached to this. */
	public List<PlayCommand> extensions;
	
	/**
	 * 
	 */
	@DataBoundConstructor
	public PlayVersion(List<PlayCommand> extensions) {
		this.extensions = extensions == null ? new ArrayList<PlayCommand>() : new ArrayList<PlayCommand>(extensions);
	}
	
	/**
	 * @return the extensions
	 */
	public final List<PlayCommand> getExtensions() {
		return extensions;
	}

}
