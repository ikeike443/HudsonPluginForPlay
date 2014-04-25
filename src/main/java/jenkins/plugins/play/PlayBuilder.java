/**
 * 
 */
package jenkins.plugins.play;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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

	private final boolean playClean;

	private final boolean playTest;

	private final boolean playTestOnly;

	private final String testOnlyClass;

	private boolean playPackage;

	private boolean playPublish;

	private boolean playDist;

	private String additionalParam;

	private boolean overwriteParam;
	
	/**
     * All the configured extensions attached to this.
     */
    private DescribableList<PlayExtension,PlayExtensionDescriptor> extensions;

	/**
	 * @param projectPath
	 * @param playClean
	 * @param playTest
	 * @param additionalParam
	 * @param overwriteParam
	 */
	@DataBoundConstructor
	public PlayBuilder(String playToolHome, String projectPath,
			boolean playClean, boolean playTest, boolean playTestOnly,
			String testOnlyClass, boolean playPackage, boolean playPublish,
			boolean playDist, String additionalParam, boolean overwriteParam, List<PlayExtension> extensions) {
		this.playToolHome = playToolHome;
		this.projectPath = projectPath;
		this.playClean = playClean;
		this.playTest = playTest;
		this.playTestOnly = playTestOnly;
		this.testOnlyClass = testOnlyClass;
		this.playPackage = playPackage;
		this.playPublish = playPublish;
		this.playDist = playDist;
		this.additionalParam = additionalParam;
		this.overwriteParam = overwriteParam;
		this.extensions = new DescribableList<PlayExtension, PlayExtensionDescriptor>(Saveable.NOOP,Util.fixNull(extensions));
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
	 * @return the playClean
	 */
	public final boolean isPlayClean() {
		return playClean;
	}

	/**
	 * @return the playTest
	 */
	public final boolean isPlayTest() {
		return playTest;
	}

	/**
	 * @return the playTestOnly
	 */
	public final boolean isPlayTestOnly() {
		return playTestOnly;
	}

	/**
	 * @return the testOnlyClass
	 */
	public final String getTestOnlyClass() {
		return testOnlyClass;
	}

	/**
	 * @return the playPackage
	 */
	public final boolean isPlayPackage() {
		return playPackage;
	}

	/**
	 * @return the playPublish
	 */
	public final boolean isPlayPublish() {
		return playPublish;
	}

	/**
	 * @return the playDist
	 */
	public final boolean isPlayDist() {
		return playDist;
	}

	/**
	 * @return the additionalParam
	 */
	public final String getAdditionalParam() {
		return additionalParam;
	}

	/**
	 * @return the overwriteParam
	 */
	public final boolean isOverwriteParam() {
		return overwriteParam;
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

		// Add clean parameter
		if (isPlayClean())
			commandParameters.add(PlayCommands.PLAY_CLEAN);

		// Add test parameter
		if (isPlayTest())
			commandParameters.add(PlayCommands.PLAY_TEST);

		// Add test-only parameter
		if (isPlayTestOnly()) {
			// Validate the class parameter
			if (!getTestOnlyClass().isEmpty())
				commandParameters.add("\"" + PlayCommands.PLAY_TEST_ONLY + " "
						+ getTestOnlyClass() + "\"");
		}

		// Add package parameter
		if (isPlayPackage())
			commandParameters.add(PlayCommands.PLAY_PACKAGE);

		// Add distribute parameter
		if (isPlayDist())
			commandParameters.add(PlayCommands.PLAY_DIST);

		// Add publish parameter
		if (isPlayPublish())
			commandParameters.add(PlayCommands.PLAY_PUBLISH);

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

		// print both global and job configuration
		printConfiguration(listener.getLogger());

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
			String aboutProject = ProjectDetails.formattedInfo(
					playExecutable, projectPath);

			// Oops, there is no information. Project isn't a Play project.
			if (aboutProject == null)
				return FormValidation.error("Not a Play!Framework project!");

			return FormValidation.okWithMarkup(aboutProject);
		}
	}

	private void printConfiguration(PrintStream logger) {

		logger.println("Build Configuration" + "\n\tPLAY_HOME : "
				+ this.getPlayToolHome() + "\n\tProject path : "
				+ this.getProjectPath() + "\n\tClean only : "
				+ (this.isPlayClean() ? "yes" : "no") + "\n\tTest only : "
				+ (this.isPlayTest() ? "yes" : "no")
				+ "\n\tGenerate artifact : "
				+ (this.isPlayPackage() ? "yes" : "no")
				+ "\n\tPublish artifact : "
				+ (this.isPlayPublish() ? "yes" : "no")
				+ "\n\tBuild Akka project : "
				+ (this.isPlayDist() ? "yes" : "no")
				+ "\n\tAdditional parameters : " + this.getAdditionalParam()
				+ "\n\tOverwrite parameters : "
				+ (this.isOverwriteParam() ? "yes" : "no"));
	}
}
