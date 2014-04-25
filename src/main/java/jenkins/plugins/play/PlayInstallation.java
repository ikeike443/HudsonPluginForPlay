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
 * @author rafaelrezende
 *
 */
public final class PlayInstallation extends ToolInstallation implements NodeSpecific<PlayInstallation>, EnvironmentSpecific<PlayInstallation>{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(PlayInstallation.class.getName());

	@DataBoundConstructor
	public PlayInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
		super(name, home, properties);
	}
	
	@Override
	public PlayInstallation forEnvironment(EnvVars environment) {
		return new PlayInstallation(getName(), environment.expand(getHome()), getProperties().toList());
	}
	
	@Override
	public PlayInstallation forNode(Node node, TaskListener log)
			throws IOException, InterruptedException {
		return new PlayInstallation(getName(), translateFor(node, log), getProperties().toList());
	}
	
	@Override
    public Descriptor getDescriptor() {
        return (Descriptor) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }
	
	@Extension
	public static class Descriptor extends ToolDescriptor<PlayInstallation> {
		
		public Descriptor() {
            super();
            load();
        }
		
		@Override
		public String getDisplayName() {
			return "Play";
		}
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			setInstallations(req.bindJSONToList(clazz, json.get("tool")).toArray(new PlayInstallation[0]));
			save();
			return true;
		}
		
		@Override
        public PlayInstallation newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return (PlayInstallation) super.newInstance(req, formData.getJSONObject("playInstallation"));
        }
		
		public PlayInstallation getInstallation(String name) {
			
			if (name == null || name.isEmpty())
				return null;
			
			for (PlayInstallation i: getInstallations()) {
				if (i.getName().equals(name))
					return i;
			}
			
			LOGGER.log(Level.WARNING, "Invalid play installation: ", name);
			return null;
		}
	}

}
