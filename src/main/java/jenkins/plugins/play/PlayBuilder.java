/**
 * 
 */
package jenkins.plugins.play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jenkins.model.Jenkins;
import jenkins.plugins.play.extensions.PlayExtension;
import jenkins.plugins.play.extensions.PlayExtensionDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.Launcher;
import hudson.Proc;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Saveable;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import hudson.util.FormValidation;

/**
 * @author rafaelrezende
 * 
 */
public class PlayBuilder extends Builder {

	private final String playToolHome;

	private final String projectPath;

	private String additionalParam;

	/**
	 * All the configured extensions attached to this.
	 */
	private DescribableList<PlayExtension, PlayExtensionDescriptor> extensions;

	/**
	 * @param projectPath
	 * @param playClean
	 * @param playTest
	 * @param additionalParam
	 * @param overwriteParam
	 */
	@DataBoundConstructor
	public PlayBuilder(String playToolHome, String projectPath,
			String additionalParam,
			List<PlayExtension> extensions) {
		this.playToolHome = playToolHome;
		this.projectPath = projectPath;
		this.additionalParam = additionalParam;
		this.extensions = new DescribableList<PlayExtension, PlayExtensionDescriptor>(
				Saveable.NOOP, Util.fixNull(extensions));
	}
	
	/**
	 * @return the playToolHome
	 */
	public String getPlayToolHome() {
		return playToolHome;
	}

	/**
	 * @return the projectPath
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * @return the additionalParam
	 */
	public final String getAdditionalParam() {
		return additionalParam;
	}

	/**
	 * @return the playToolHome
	 */
	public String getPlayExecutable() {
		return playToolHome + "/play";
	}

	/**
	 * @return list of extensions
	 */
	public DescribableList<PlayExtension, PlayExtensionDescriptor> getExtensions() {
		return extensions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.Builder#getDescriptor()
	 */
	@Override
	public PlayDescriptor getDescriptor() {
		return (PlayDescriptor) super.getDescriptor();
	}

	/**
	 * Generate the list of command parameters according to the user selection
	 * on Jenkins interface.
	 * 
	 * @return List of parameters
	 */
	public List<String> generatePlayParameters() {

		List<String> commandParameters = new ArrayList<String>();

		// This parameter is always present to remove color formatting
		// characters from the output.
		String noColorFormatting = "-Dsbt.log.noformat=true";
		commandParameters.add(noColorFormatting);

		// add extension actions to command-line one by one
		for (PlayExtension playExt : this.extensions) {

			// Every command parameter is surrounded by quotes, have them
			// additional parameters or not.
			// HOWEVER, the launcher already adds single quotes automatically
			// whenever the parameter is composed of two or more strings.
			// Therefore, no need to add the quotes here.
			String commandPattern = "%s %s";
			String command = String.format(commandPattern,
					playExt.getCommand(), playExt.getParameter());

			System.out.println("########  " + command);
			// Add generated parameter to the array of parameters
			commandParameters.add(command);
		}

		for (String string : commandParameters) {
			System.out.println("#### " + string);
		}

		return commandParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild
	 * , hudson.Launcher, hudson.model.BuildListener)
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		// Create file from play path String
		File playExecutable = new File(this.getPlayExecutable());

		// Check if play executable exists
		if (!playExecutable.exists()) {
			listener.getLogger().println("ERROR! Play executable not found!");
			return false;
		}

		// Create file from project path String
		File projectFile = new File(this.getProjectPath());

		// Check if project folder exists
		if (!projectFile.exists()) {
			listener.getLogger().println("ERROR! Project path not found!");
			return false;
		}

		List<String> commandParameters = generatePlayParameters();

		Proc proc = launcher
				.launch()
				.cmds(playExecutable,
						commandParameters.toArray(new String[commandParameters
								.size()])).pwd(this.getProjectPath())
				.writeStdin().stdout(listener.getLogger())
				.stderr(listener.getLogger()).start();

		return proc.join() == 0;
	}

	@Extension
	public static final class PlayDescriptor extends
			BuildStepDescriptor<Builder> {

		public PlayDescriptor() {
			load();
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Invoke Play!Framework";
		}

		public List<PlayExtensionDescriptor> getExtensionDescriptors() {
			return PlayExtensionDescriptor.all();
		}

		/**
		 * This method is required by the interface to list Play installations
		 * 
		 * @return Array of Play installations
		 */
		public PlayInstallation[] getInstallations() {
			return Jenkins.getInstance()
					.getDescriptorByType(PlayInstallation.Descriptor.class)
					.getInstallations();
		}

		public FormValidation doCheckOverwriteParam(
				@QueryParameter boolean overwriteParam) {
			if (overwriteParam)
				return FormValidation
						.warning("The above checkboxes will not have any effect.");
			else
				return FormValidation.ok();
		}

		public FormValidation doCheckProjectPath(
				@QueryParameter String projectPath) {

			// If field is empty, call the required validator
			if (projectPath.isEmpty())
				return FormValidation.validateRequired(projectPath);

			// Otherwise, check if the project path is valid
			File projectPathDir = new File(projectPath);
			if (!projectPathDir.exists())
				return FormValidation.error("Project path has not been found!");

			return FormValidation.ok();

		}

		public FormValidation doValidateProject(
				@QueryParameter String playToolHome,
				@QueryParameter String projectPath) {

			String playExecutable = playToolHome + "/play";

			// If the field is empty or invalid, silently return OK, because the
			// validation is already performed by the doCheckProjectPath method.
			if (projectPath.isEmpty())
				return FormValidation.ok();

			File projectPathDir = new File(projectPath);
			if (!projectPathDir.exists())
				return FormValidation.ok();

			// Check if play executable exists
			File playFile = new File(playExecutable);
			if (!playFile.exists()) {
				return FormValidation
						.error("Cannot validate project! The assigned Play!Framework installation is invalid!");
			}

			// Generate informational content for the user
			String aboutProject = ValidateProject.formattedInfo(playExecutable,
					projectPath);

			// Oops, there is no information. Project isn't a Play project.
			if (aboutProject == null)
				return FormValidation.error("Not a Play!Framework project!");

			return FormValidation.okWithMarkup(aboutProject);
		}
	}
}
