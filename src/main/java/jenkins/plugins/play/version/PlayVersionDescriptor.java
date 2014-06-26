/**
 * 
 */
package jenkins.plugins.play.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jenkins.model.Jenkins;
import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCommandDescriptor;
import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;

/**
 * Abstract descriptor for each play command. Every command should provide a
 * list of Play versions it's compatible with.
 */
public abstract class PlayVersionDescriptor extends Descriptor<PlayVersion> {
	
	protected String displayName;
	
	public static final String[] COMMAND_LIST = {};
	
	@Override
	public String getDisplayName() {
        return "None";
    }
	
	/**
	 * Goals are Implemented as extensions. This methods returns the
	 * descriptor of every available extension.
	 * 
	 * @return Available goals.
	 */
	public List<PlayCommandDescriptor> getCommandDescriptors(String[] commandList) {
		
		List<String> associatedCommands = Arrays.asList(commandList);
		List<PlayCommandDescriptor> filteredList = new ArrayList<PlayCommandDescriptor>();
		
		List<PlayCommandDescriptor> list = Jenkins.getInstance().getDescriptorList(PlayCommand.class);
		// Iterate over commands to filter those compatible to the desired version
		for (Iterator<PlayCommandDescriptor> iterator = list.iterator(); iterator
				.hasNext();) {

			PlayCommandDescriptor playExtensionDescriptor = iterator.next();

			// Remove from the list if the command isn't compatible with the
			// version
			if (associatedCommands.contains(playExtensionDescriptor.getCommandId()))
				filteredList.add(playExtensionDescriptor);
		}
		return filteredList;
	}
	
}
