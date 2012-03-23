package com.gmail.ikeike443;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ikeike443
 */
public class PlayAutoTestBuilder extends Builder {

    private final String play_cmd;
    private final String play_cmd2;
    private final String play_cmd3;
    private final String play_cmd4;
    private final String play_cmd5;
    private List<String> play_cmds;
    private final String play_path;
    private PrintStream logger;

    // This maps stored the executed commands and the results
    private Map<String, String> exitcodes;
    private AbstractBuild build;
    private Launcher launcher;
    private BuildListener listener;

    private String playpath;
    private FilePath workDir;


    @SuppressWarnings("serial")
    @DataBoundConstructor
    public PlayAutoTestBuilder(
            final String play_cmd,
            final String play_cmd2,
            final String play_cmd3,
            final String play_cmd4,
            final String play_cmd5,
            final String play_path) {
        System.out.println("Creating play auto test builder");
        this.play_cmd = ensureCommandString(play_cmd);
        this.play_cmd2 = ensureCommandString(play_cmd2);
        this.play_cmd3 = ensureCommandString(play_cmd3);
        this.play_cmd4 = ensureCommandString(play_cmd4);
        this.play_cmd5 = ensureCommandString(play_cmd5);
        this.play_path = play_path;
    }

    @SuppressWarnings({"deprecation"})
    @Override
    public boolean perform(@SuppressWarnings("rawtypes") AbstractBuild build, Launcher launcher, BuildListener listener) {
        this.build = build;
        this.launcher = launcher;
        this.listener = listener;
        logger = listener.getLogger();
        exitcodes = new HashMap<String, String>();
        this.play_cmds = nonEmptyCommands();

        try {
            cleanUpTestResult();
            setPlaypath(buildPlayPath());
            setWorkDir(buildWorkDir());

            for (String playCommand : this.play_cmds) {
                String command = substituteParameters(playCommand);
                execute(command);
                treatPlayAutoTestSpecially(command);
            }

            logResults();
            return !exitcodes.containsValue("Fail");
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace(logger);
            return false;
        }

    }

    private void cleanUpTestResult() {
        try {
            FilePath[] files = build.getProject().getSomeWorkspace().list("test-result/*");
            for (FilePath filePath : files) {
                filePath.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting files in dir 'test-result'", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while deleting files in dir 'test-result'", e);
        }
    }

    String buildPlayPath() {

        //build playpath
        String playpath = null;
        if (play_path != null && play_path.length() > 0) {
            playpath = play_path;
        } else if (getDescriptor().path() != null) {
            playpath = getDescriptor().path();
        }
        logger.println("play path is " + playpath);
        if (playpath == null) {
            throw new RuntimeException("Play path is null");
        }
        return playpath;
    }

    private FilePath buildWorkDir() {
        FilePath workDir = build.getWorkspace();
        @SuppressWarnings("unchecked")
        PlayAutoTestJobProperty playJobProperty = (PlayAutoTestJobProperty) build.getProject()
                .getProperty(PlayAutoTestJobProperty.class);
        String application_path = playJobProperty != null ? playJobProperty.getApplicationPath() : null;
        if (application_path != null && application_path.length() > 0) {
            workDir = build.getWorkspace().child(application_path);
        }
        return workDir;
    }

    private String substituteParameters(String playCommand) {
        // Substitute parameters
        ParametersAction param = build.getAction(ParametersAction.class);
        if (param != null) {
            logger.println("Substituting job parameters from " + playCommand);
            List<ParameterValue> values = param.getParameters();
            if (values != null) {
                for (ParameterValue value : values) {
                    String v = value.createVariableResolver(build).resolve(value.getName());
                    playCommand = playCommand.replace("${" + value.getName() + "}", v);
                }
            }
        }
        return playCommand;
    }

    private int execute(String playCommand) throws IOException, InterruptedException {
        String[] cmds = playCommand.split(" ", 2);
        Launcher.ProcStarter procStarter = setupProcStarter(cmds);
        logCommand(cmds);
        Proc proc = launcher.launch(procStarter);
        int exitcode = proc.join();
        exitcodes.put(playCommand, (exitcode == 0 ? "Done" : "Fail"));
        if (exitcode != 0) {
            logger.println("****************************************************");
            logger.println("* ERROR!!! while executing command: '" + playCommand + "', exitcode: " + exitcode);
            logger.println("****************************************************");
            throw new RuntimeException("Error while executing '" + playCommand + "'");
        }
        return exitcode;
    }

    private Launcher.ProcStarter setupProcStarter(String[] cmds) throws IOException, InterruptedException {
        Launcher.ProcStarter procStarter = launcher.new ProcStarter();
        procStarter.cmds(playpath, cmds[0], workDir.toString(), (cmds.length >= 2 ? cmds[1] : ""));
        procStarter.stdout(logger);
        procStarter.pwd(workDir);
        procStarter.envs(build.getEnvironment(listener));
        return procStarter;
    }

    private void logCommand(String[] cmds) {
        String cmd = playpath + " " + cmds[0] + " \"" + workDir.toString() + "\" "
                + (cmds.length >= 2 ? cmds[1] : "");
        logger.println("Executing " + cmd);
    }

    private void treatPlayAutoTestSpecially(String playCommand) throws IOException, InterruptedException {
        if (playCommand != null && playCommand.matches("(auto-?test.*)")) {
            //check test-result
            if (!new FilePath(workDir, "test-result/result.passed").exists()) {
                build.setResult(Result.UNSTABLE);
            }
        }
    }

    private void logResults() {
        logger.println("Each commands' results:");
        for (Map.Entry<String, String> rec : exitcodes.entrySet()) {
            logger.println("  " + rec.getKey() + ": " + rec.getValue());
        }
    }

    public void setPlaypath(String playpath) {
        this.playpath = playpath;
    }

    public void setWorkDir(FilePath workDir) {
        this.workDir = workDir;
    }

    String ensureCommandString(String command) {
        return isNullOrEmpty(command) ? "" : command.trim();
    }

    List<String> nonEmptyCommands() {
        List<String> commands = new ArrayList<String>(5);
        addIfNotEmpty(this.play_cmd, commands);
        addIfNotEmpty(this.play_cmd2, commands);
        addIfNotEmpty(this.play_cmd3, commands);
        addIfNotEmpty(this.play_cmd4, commands);
        addIfNotEmpty(this.play_cmd5, commands);
        return commands;
    }

    void addIfNotEmpty(String command, List<String> commands) {
        if (!isNullOrEmpty(command)) {
            commands.add(command.trim());
        }
    }

    boolean isNullOrEmpty(String command) {
        return command == null || command.trim().length() == 0;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getPlay_cmd() {
        return play_cmd;
    }

    public String getPlay_cmd2() {
        return play_cmd2;
    }

    public String getPlay_cmd3() {
        return play_cmd3;
    }

    public String getPlay_cmd4() {
        return play_cmd4;
    }

    public String getPlay_cmd5() {
        return play_cmd5;
    }

    public String getPlay_path() {
        return play_path;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link PlayAutoTestBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>views/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // this marker indicates Hudson that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public DescriptorImpl() {
            super();
            load();
        }

        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * <p/>
         * <p/>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String path;

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set path to play");

            return FormValidation.ok();
        }

        public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
            // indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Play!";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            path = formData.getString("play_path");
            save();
            return super.configure(req, formData);
        }


        /**
         * This method returns true if the global configuration says we should speak French.
         */
        public String path() {
            return path;
        }
    }
}

