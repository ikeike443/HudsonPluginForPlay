/**
 * 
 */
package jenkins.plugins.play.version;

import java.io.File;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import jenkins.plugins.play.PlayBuilder;
import jenkins.plugins.play.ValidateProject;
import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCommandDescriptor;
import hudson.Extension;
import hudson.util.FormValidation;

/**
 * @author rafaelrezende
 * 
 */
public class Play2x extends PlayVersion {

	@DataBoundConstructor
	public Play2x(List<PlayCommand> commands) {
		super(commands);
	}
	
	@Extension
	public static class Play2xDescriptor extends PlayVersionDescriptor {
		
		public static final String[] COMMAND_LIST = { "PLAY_CLEAN", "PLAY_COMPILE", "PLAY_DIST", "PLAY_PACKAGE", "PLAY_PUBLISH", "PLAY_TEST", "PLAY_TESTONLY", "PLAY_CUSTOM" };
		
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
		public List<PlayCommandDescriptor> getCommandDescriptors() {
			
			return super.getCommandDescriptors(Play2xDescriptor.COMMAND_LIST);
		}
		
		
		/**
		 * Retrieve information about the project by running the 'play about'
		 * command. Helps to identify that the project is a Play project and
		 * that the chosen Play version is compliant with it. Also helpful to
		 * check if the Play installation is valid.
		 * This method is invoked by a button in the Jenkins jelly interface.
		 * 
		 * @param playToolHome
		 *            Chosen Play installation
		 * @param projectPath
		 *            Project path
		 * @return Form validation
		 */
		@Deprecated
		public FormValidation doValidateProject(
				@QueryParameter String playToolHome,
				@QueryParameter String projectPath) {
			
			File playFile = PlayBuilder.getPlayExecutable(playToolHome, "");
			
			// If the field is empty or invalid, silently return OK, because the
			// validation is already performed by the doCheckProjectPath method in PlayBuilder.
			if (projectPath.isEmpty())
				return FormValidation.ok();

			File projectPathDir = new File(projectPath);
			if (!projectPathDir.exists())
				return FormValidation.ok();

			// Check if play executable exists
			if (!playFile.exists()) {
				return FormValidation
						.error("Cannot validate project! The assigned Play Framework installation is invalid!");
			}

			// Generate informational content for the user
			String aboutProject = ValidateProject.formattedInfo(playFile,
					projectPath);

			// Oops, there is no information. Project isn't a Play project.
			if (aboutProject == null)
				return FormValidation.error("Not recognized as a valid project for the selected Play tool.");

			return FormValidation.okWithMarkup(aboutProject);
		}
		
		
	}
}
