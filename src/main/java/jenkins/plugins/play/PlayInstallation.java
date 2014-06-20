/**
 * 
 */
package jenkins.plugins.play;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.TaskListener;
import hudson.model.Node;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolProperty;
import hudson.tools.ToolInstallation;

/**
 *	Represents the Play installation in the global configuration of Jenkins.
 */
public final class PlayInstallation extends ToolInstallation implements NodeSpecific<PlayInstallation>, EnvironmentSpecific<PlayInstallation>{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(PlayInstallation.class.getName());

	/**
	 * @param name Play installation ID
	 * @param home Play installation path
	 */
	@DataBoundConstructor
	public PlayInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
		super(name, home, properties);
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.EnvironmentSpecific#forEnvironment(hudson.EnvVars)
	 */
	@Override
	public PlayInstallation forEnvironment(EnvVars environment) {
		return new PlayInstallation(getName(), environment.expand(getHome()), getProperties().toList());
	}
	

	/* (non-Javadoc)
	 * @see hudson.slaves.NodeSpecific#forNode(hudson.model.Node, hudson.model.TaskListener)
	 */
	@Override
	public PlayInstallation forNode(Node node, TaskListener log)
			throws IOException, InterruptedException {
		return new PlayInstallation(getName(), translateFor(node, log), getProperties().toList());
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.AbstractDescribableImpl#getDescriptor()
	 */
	@Override
    public PlayToolDescriptor getDescriptor() {
        return (PlayToolDescriptor) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }
	
	/**
	 * Play installation descriptor.
	 */
	@Extension
	public static class PlayToolDescriptor extends ToolDescriptor<PlayInstallation> {
		
		public PlayToolDescriptor() {
            super();
            load();
        }
		
		/* (non-Javadoc)
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Play!";
		}
		
		/* (non-Javadoc)
		 * @see hudson.tools.ToolDescriptor#configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
		 */
		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			setInstallations(req.bindJSONToList(clazz, json.get("tool")).toArray(new PlayInstallation[0]));
			save();
			return true;
		}
		
		/* (non-Javadoc)
		 * @see hudson.model.Descriptor#newInstance(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
		 */
		@Override
        public PlayInstallation newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return (PlayInstallation) super.newInstance(req, formData.getJSONObject("playInstallation"));
        }
		
		/**
		 * Get the Play installation instance assigned by the given name.
		 * 
		 * @param name Play installation ID.
		 * @return Corresponding Play installation instance.
		 */
//		public PlayInstallation getInstallation(String name) {
//			
//			if (name == null || name.isEmpty())
//				return null;
//			
//			for (PlayInstallation i: getInstallations()) {
//				if (i.getName().equals(name))
//					return i;
//			}
//			
//			LOGGER.log(Level.WARNING, "Invalid play installation: ", name);
//			return null;
//		}
	}

}
