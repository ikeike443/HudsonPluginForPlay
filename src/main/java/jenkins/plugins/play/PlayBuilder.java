/**
 * 
 */
package jenkins.plugins.play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jenkins.model.Jenkins;
import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.version.Play1x;
import jenkins.plugins.play.version.PlayVersion;
import jenkins.plugins.play.version.PlayVersionDescriptor;
import jenkins.tasks.SimpleBuildStep;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

/**
 * Provides the several of the functionalities of Play Framework in a Jenkins
 * plugin. This class is responsible for the Play Framework module in the job
 * configuration.
 * 
 */
public class PlayBuilder extends Builder implements SimpleBuildStep {

	/** The Play installation path selected by the user. */
	private final String playToolHome;
	/** Absolute or relative project path. */
	private final String projectPath;
	/** Play version used in the job. Indicates the command set available. */
	private PlayVersion playVersion;
	
	/**
	 * Constructor used by Jenkins to handle the Play job.
	 * 
	 * @param playToolHome Path of Play installation.
	 * @param projectPath Project path.
	 * @param playVersion Play version.
	 */
	@DataBoundConstructor
	public PlayBuilder(String playToolHome, String projectPath, PlayVersion playVersion) {
		this.playToolHome = playToolHome;
		this.projectPath = projectPath;
		this.playVersion = playVersion;
	}
	
	/**
	 * Get the path of the Play installation.
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
	 * @return the Play version.
	 */
	public final PlayVersion getPlayVersion() {
		return playVersion;
	}
	
	/**
	 * Get the complete path of the Play executable. First looks for a 'play' executable, then 'activator'.
	 * 
	 * @param playToolHome Path of the Play tool home.
	 * @param fileExtension Usually .bat for Windows. No extensions for Unix.
	 * @return the Play executable path.
	 */
	public static File getPlayExecutable(String playToolHome, String fileExtension) {
		
		// Try play executable first
		File playExecutable = new File(playToolHome + "/play" + fileExtension);
		if (playExecutable.exists())
			return playExecutable;
		
		// Try activator executable
		playExecutable = new File(playToolHome + "/activator" + fileExtension);
		if (playExecutable.exists())
			return playExecutable;
		
		// There is no potential executor here. Return null.
		return null;
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
		// characters from the output. (Except for Play 1.x)
		if (!(this.playVersion instanceof Play1x))
			commandParameters.add("-Dsbt.log.noformat=true");

		// add extension actions to command-line one by one
		for (PlayCommand playExt : this.playVersion.getCommands()) {
			
			// Every command parameter can be surrounded by quotes, have them
			// additional parameters or not.
			// HOWEVER, the launcher already adds quotes automatically
			// whenever the parameter is composed of two or more strings.
			// Therefore, no need to add the quotes here.
			String command = playExt.getCommand() + " " + playExt.getParameter();

			// Trim the String to remove leading and trailing whitespace (just
			// esthetical reason)
			// Add generated parameter to the array of parameters
			commandParameters.add(command.trim());
		}
		
		return commandParameters;
	}

	/**
	 * Descriptor to capture and validate fields from the interface.
	 */
	@Extension
	public static class PlayDescriptor extends
			BuildStepDescriptor<Builder> {

		/**
		 * Descriptor constructor.
		 */
		public PlayDescriptor() {
			load();
		}
		
		public PlayDescriptor(Class<? extends Builder> clazz) {
            super(clazz);
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
			return "Invoke Play Framework";
		}

		/**
		 * Get available Play installations.
		 * 
		 * @return Array of Play installations
		 */
		public PlayInstallation[] getInstallations() {
			return Jenkins.getInstance()
					.getDescriptorByType(PlayInstallation.PlayToolDescriptor.class)
					.getInstallations();
		}
		
        /**
         * Retrieve list of Play versions.
         * @return
         */
        public List<PlayVersionDescriptor> getPlayVersion() {
        	return Jenkins.getInstance().getDescriptorList(PlayVersion.class);
        }
        
		/**
		 * Check if the project path field is not empty and exists.
		 * 
		 * @param projectPath
		 *            Project path
		 * @return Form validation
		 */
		public FormValidation doCheckProjectPath(@QueryParameter String projectPath) {
			
			// If field is empty, notify
			return FormValidation.validateRequired(projectPath);
		}
	}

	@Override
	public void perform(Run<?, ?> run, FilePath filepath, Launcher launcher,
			TaskListener listener) throws InterruptedException, IOException {

		// Create file from play path String
		String fileExtension = launcher.isUnix() ? "" : ".bat";
		File playExecutable = PlayBuilder.getPlayExecutable(this.playToolHome, fileExtension);
		
		filepath = new FilePath(filepath, projectPath);
		
		// Check if play executable exists
		if (playExecutable == null) {
			listener.getLogger().println("ERROR! Play executable not found!");
			run.setResult(Result.FAILURE);
			return;
		}

		// Check if project folder exists
		if (!filepath.exists()) {
			listener.getLogger().println("ERROR! Project path not found!");
			run.setResult(Result.FAILURE);
			return;
		}

		// Creates the complete list of parameters including goals
		List<String> commandParameters = generatePlayParameters();

		// Validated that there are commands set up
		if (commandParameters == null) {
			listener.getLogger().println("ERROR! No commands were provided.");
			run.setResult(Result.FAILURE);
			return;
		}

		for (String comm : commandParameters) {
			listener.getLogger().println("Command detected: " + comm);
		}

		// Play 1.x is not able to execute several commands in sequence.
		// Instead, it should call the 'play' executable for every command
		// separately.
		if (this.getPlayVersion() instanceof Play1x) {

			// For each command...
			for (String comm : commandParameters) {
				// In Play1x, commands should not have quotes. Since the
				// launcher automatically adds quotes if the command contains
				// whitespace, it is necessary to split the command in the
				// whitespaces first and provide them to the launcher as an
				// array.

				// ... run it in a new process.
				Proc proc = launcher.launch()
						.cmds(playExecutable, comm.split(" ")).pwd(filepath)
						.writeStdin().stdout(listener.getLogger())
						.stderr(listener.getLogger()).start();

				int result = proc.join();
				// Immediately stop the build process when a command results in
				// error.
				if (result != 0) {
					listener.getLogger().println(
							"ERROR! Failed to execute the Play command.");
					run.setResult(Result.FAILURE);
					return;
				}
			}
			// Every command ran successfully
			run.setResult(Result.SUCCESS);
		}

		// Play 2.x (sbt) is able to execute all commands at once
		else {
			// Launch Play Framework
			Proc proc = launcher
					.launch()
					.cmds(playExecutable,
							commandParameters
									.toArray(new String[commandParameters
											.size()])).pwd(filepath)
					.writeStdin().stdout(listener.getLogger())
					.stderr(listener.getLogger()).start();

			if (proc.join() == 0)
				run.setResult(Result.SUCCESS);
			else run.setResult(Result.FAILURE);
		}
	}
}
