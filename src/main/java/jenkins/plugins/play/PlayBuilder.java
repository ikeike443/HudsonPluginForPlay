/**
 * 
 */
package jenkins.plugins.play;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jenkins.model.Jenkins;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

/**
 * @author rafaelrezende
 * 
 */
public class PlayBuilder extends Builder {

	private final String playToolName;

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
	 * @param projectPath
	 * @param playClean
	 * @param playTest
	 * @param additionalParam
	 * @param overwriteParam
	 */
	@DataBoundConstructor
	public PlayBuilder(String playToolName, String projectPath,
			boolean playClean, boolean playTest, boolean playTestOnly,
			String testOnlyClass, boolean playPackage, boolean playPublish,
			boolean playDist, String additionalParam, boolean overwriteParam) {
		this.playToolName = playToolName;
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
	}

	/**
	 * @return the playToolName
	 */
	public String getPlayToolName() {
		return playToolName;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.Builder#getDescriptor()
	 */
	@Override
	public PlayDescriptor getDescriptor() {
		return (PlayDescriptor) super.getDescriptor();
	}

	public PlayInstallation getPlayTool() {
		for (PlayInstallation playTool : getDescriptor().getInstallations()) {
			if (playToolName != null && playToolName.equals(playTool.getName()))
				return playTool;
		}
		return null;
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
		File playFile = new File(this.getPlayTool().getPlayExe());

		// Check if play executable exists
		if (!playFile.exists()) {
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
				.cmds(playFile,
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
				@QueryParameter String projectPath,
				@QueryParameter String playToolName) {

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
				@QueryParameter String playToolName,
				@QueryParameter String projectPath) {

			// If the field is empty or invalid, silently return OK, because the
			// validation is already performed by the doCheckProjectPath method.
			if (projectPath.isEmpty())
				return FormValidation.ok();

			File projectPathDir = new File(projectPath);
			if (!projectPathDir.exists())
				return FormValidation.ok();

			// The used tool installation is required to check the information
			// about the project
			PlayInstallation playInstallation = Jenkins.getInstance()
					.getDescriptorByType(PlayInstallation.Descriptor.class)
					.getInstallation(playToolName);

			// Check if play executable exists
			File playFile = new File(playInstallation.getPlayExe());
			if (!playFile.exists()) {
				return FormValidation
						.error("Cannot validate project! The assigned Play!Framework installation is invalid!");
			}

			// Generate informational content for the user
			String aboutProject = ProjectDetails.formattedInfo(
					playInstallation.getPlayExe(), projectPath);

			// Oops, there is no information. Project isn't a Play project.
			if (aboutProject == null)
				return FormValidation.error("Not a Play!Framework project!");

			return FormValidation.okWithMarkup(aboutProject);
		}
	}

	private void printConfiguration(PrintStream logger) {

		logger.println("Build Configuration" + "\n\tPLAY_HOME : "
				+ this.getPlayTool().getPlayExe() + "["
				+ this.getPlayToolName() + "]" + "\n\tProject path : "
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
