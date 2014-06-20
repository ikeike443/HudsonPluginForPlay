/**
 * 
 */
package jenkins.plugins.play.version;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import jenkins.model.Jenkins;
import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCommandDescriptor;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.util.DescribableList;

/**
 * @author rafaelrezende
 * 
 */
public class Play2x extends PlayVersion {

	@DataBoundConstructor
	public Play2x(List<PlayCommand> extensions) {
		super(extensions);
		System.out.println("####### Constructor Play2x here!");
		// TODO Auto-generated constructor stub
	}

	@Extension
	public static class Play2xDescriptor extends PlayVersionDescriptor {

		public static final String[] COMMAND_LIST = { "PLAY_CLEAN", "PLAY_COMPILE" };
		
		@Override
		public String getDisplayName() {
	        return "Play 2.x";
	    }
		
		/**
		 * Goals are Implemented as extensions. This methods returns the
		 * descriptor of every available extension.
		 * 
		 * @return Available goals.
		 */
		public List<PlayCommandDescriptor> getExtensionDescriptors() {
			
			System.out.println("############ Play2x.getExtensionDescriptors");
			
			List<String> associatedCommands = Arrays.asList(COMMAND_LIST);
			
			DescriptorExtensionList<PlayCommand, PlayCommandDescriptor> list = Jenkins.getInstance().getDescriptorList(PlayCommand.class);
			
			// Iterate over commands to filter those compatible to the desired
			// version
			for (Iterator<PlayCommandDescriptor> iterator = list.iterator(); iterator
					.hasNext();) {

				PlayCommandDescriptor playExtensionDescriptor = iterator.next();

				// Remove from the list if the command isn't compatible with the
				// version
				if (!associatedCommands.contains(playExtensionDescriptor.getCommandId()))
					list.remove(playExtensionDescriptor);
			}
//			return list;
			return Jenkins.getInstance().getDescriptorList(PlayCommand.class);
		}
	}
}
