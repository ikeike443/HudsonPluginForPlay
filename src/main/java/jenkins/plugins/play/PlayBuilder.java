/**
 * 
 */
package jenkins.plugins.play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.DescriptorExtensionList;
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
import hudson.util.ListBoxModel;

/**
 * Provides the several of the functionalities of Play!Framework in a Jenkins
 * plugin. This class is responsible for the Play!Framework module in the job
 * configuration.
 * 
 */
public class PlayBuilder extends Builder {

	/** The Play installation path selected by the user. */
	private final String playToolHome;
	/** Absolute or relative project path. */
	private final String projectPath;
	/** Parameters provided by the user. */
	private String additionalParam;
	/** All the configured extensions attached to this. */
	private DescribableList<PlayCommand, PlayCommandDescriptor> extensions;

	/**
	 * Constructor used by Jenkins to handle the Play! job.
	 * 
	 * @param playToolHome
	 *            Path of Play! installation
	 * @param projectPath
	 *            Project path
	 * @param additionalParam
	 *            Additional parameters
	 * @param extensions
	 *            Build goals
	 */
	@DataBoundConstructor
	public PlayBuilder(String playToolHome, String projectPath,
			String additionalParam, List<PlayCommand> extensions) {
		this.playToolHome = playToolHome;
		this.projectPath = projectPath;
		this.additionalParam = additionalParam;
		this.extensions = new DescribableList<PlayCommand, PlayCommandDescriptor>(
				Saveable.NOOP, Util.fixNull(extensions));
	}

	/**
	 * Get the path of the Play! installation.
	 * 
	 * @return the playToolHome
	 */
	public String getPlayToolHome() {
		return playToolHome;
	}

	/**
	 * Get the project path.
	 * 
	 * @return the projectPath
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * Get additional parameters.
	 * 
	 * @return the additionalParam
	 */
	public final String getAdditionalParam() {
		return additionalParam;
	}

	/**
	 * Get the complete path of the Play! executable. It assumes the executable
	 * is always "play".
	 * 
	 * @return the Play! executable.
	 */
	public File getPlayExecutable() {
		
		for (PlayTarget target : PlayTarget.values()) {
			File executable = new File(this.playToolHome + target.getExecutable());
			
			if (executable.exists())
				return executable;
		}
		return null;
	}

	/**
	 * List of Play! goals.
	 * 
	 * @return list of extensions
	 */
	public DescribableList<PlayCommand, PlayCommandDescriptor> getExtensions() {
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

		// Add the additional parameters to the list of parameters
		commandParameters.add(additionalParam);

		// add extension actions to command-line one by one
		for (PlayCommand playExt : this.extensions) {

			// Every command parameter is surrounded by quotes, have them
			// additional parameters or not.
			// HOWEVER, the launcher already adds single quotes automatically
			// whenever the parameter is composed of two or more strings.
			// Therefore, no need to add the quotes here.
			String commandPattern = "%s %s";
			String command = String.format(commandPattern,
					playExt.getCommand(), playExt.getParameter());

			// Trim the String to remove leading and trailing whitespace (just
			// esthetical reason)
			// Add generated parameter to the array of parameters
			commandParameters.add(command.trim());
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
		File playExecutable = this.getPlayExecutable();

		// Check if play executable exists
		if (playExecutable == null) {
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

		// Creates the complete list of parameters including goals
		List<String> commandParameters = generatePlayParameters();

		// Launch Play!Framework
		Proc proc = launcher
				.launch()
				.cmds(playExecutable,
						commandParameters.toArray(new String[commandParameters
								.size()])).pwd(this.getProjectPath())
				.writeStdin().stdout(listener.getLogger())
				.stderr(listener.getLogger()).start();

		return proc.join() == 0;
	}

	/**
	 * Descriptor to retrieve and validate fields from the interface.
	 */
	@Extension
	public static final class PlayDescriptor extends
			BuildStepDescriptor<Builder> {

		public PlayDescriptor() {
			load();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.tasks.BuildStepDescriptor#isApplicable(java.lang.Class)
		 */
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Invoke Play!Framework";
		}

		/**
		 * Goals are Implemented as extensions. This methods returns the
		 * descriptor of every available extension.
		 * 
		 * @return Available goals.
		 */
		public List<PlayCommandDescriptor> getExtensionDescriptors(@QueryParameter String playToolHome) {
			
			System.out.println("###############getExtensionDescriptors " + playToolHome);
			
			DescriptorExtensionList<PlayCommand, PlayCommandDescriptor> descriptors = PlayCommandDescriptor.all(ValidatePlayTarget.getPlayTarget(playToolHome));
			for (PlayCommandDescriptor playCommandDescriptor : descriptors) {
				System.out.println("#!!!!: " + playCommandDescriptor.getDisplayName());
			}
			
			return descriptors;
		}
		
//		problem here: the selection of goals uses an hetero-list, which requires a list of extensionDescriptors.
//		The only way to fill the Goals automatically (afaik) is using a doFillGoalsItemis, that instead, 
//		requires a ListBoxModel.
//		So, what I need here: a way to pass the playToolHome to my getExtensionDescriptors, and reload it
//		everytime a new play tool is selected...
		
		/**
		 * Get available Play! installations.
		 * 
		 * @return Array of Play installations
		 */
		public PlayInstallation[] getInstallations() {
			return Jenkins.getInstance()
					.getDescriptorByType(PlayInstallation.Descriptor.class)
					.getInstallations();
		}
		
		/**
		 * Check if the project path field is not empty and exists.
		 * 
		 * @param projectPath
		 *            Project path
		 * @return Form validation
		 */
		public FormValidation doCheckProjectPath(
				@QueryParameter String projectPath) {
			
			System.out.println("### CHECK PROJECT ###");

			// If field is empty, call the required validator
			if (projectPath.isEmpty())
				return FormValidation.validateRequired(projectPath);

			// Otherwise, check if the project path is valid
			File projectPathDir = new File(projectPath);
			if (!projectPathDir.exists())
				return FormValidation.error("Project path has not been found!");

			return FormValidation.ok();

		}

		/**
		 * Retrieve information about the project by running the 'play about'
		 * command. Helps to identify that the project is a Play! project and
		 * that the chosen Play! version is compliant with it. Also helpful to
		 * check if the Play! installation is valid.
		 * This method is invoked by a button in the Jenkins jelly interface.
		 * 
		 * @param playToolHome
		 *            Chosen Play! installation
		 * @param projectPath
		 *            Project path
		 * @return Form validation
		 */
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
				return FormValidation.error("Not recognized as a valid project for the selected Play! tool.");

			return FormValidation.okWithMarkup(aboutProject);
		}
	}
}
