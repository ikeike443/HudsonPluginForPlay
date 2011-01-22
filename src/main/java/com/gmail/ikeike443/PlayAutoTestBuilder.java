package com.gmail.ikeike443;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link PlayAutoTestBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)} method
 * will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class PlayAutoTestBuilder extends Builder{

    private final String play_path_job;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public PlayAutoTestBuilder(String play_path_job) {
        this.play_path_job = play_path_job;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getPlay_path_job() {
        return play_path_job;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
    	// this is where you 'build' the project
		// since this is a dummy, we just say 'hello world' and call that a build
		String playpath = null;
		// this also shows how you can consult the global configuration of the builder
		if(getDescriptor().path()!= null){
			playpath = getDescriptor().path();
		}else{
			listener.getLogger().println("play path is null");
			return false;
		}
		if(play_path_job != null){
			playpath = play_path_job;
		}
		listener.getLogger().println("playpath is "+playpath);
        try {
        	String cmd = playpath + "play auto-test "+build.getWorkspace().toString();
        	listener.getLogger().println(cmd);
        	ProcessBuilder pb = new ProcessBuilder(playpath+"play","auto-test",build.getWorkspace().toString());
        	Process ps = pb.start();
			printInputStream(ps.getInputStream(), listener.getLogger());	
			int waitFor = ps.waitFor();
					
			if(waitFor==0){
				//check test-result
				if(new File(build.getWorkspace().toString()+"/test-result/result.passed").exists()){
					
					return true;
				}else{
					return false;
				}
			}else{
				listener.getLogger().println("play test failed");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
    }
	static void printInputStream(InputStream is,PrintStream logger) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			for (;;) {
				String line = br.readLine();
				if (line == null) break;
				logger.println(line);
			}
		} finally {
			br.close();
		}
	}

    // overrided for better type safety.
    // if your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link PlayAutoTestBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>views/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // this marker indicates Hudson that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
		/**
		 * To persist global configuration information,
		 * simply store it in a field and call save().
		 *
		 * <p>
		 * If you don't want fields to be persisted, use <tt>transient</tt>.
		 */
		private String path;

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 *
		 * @param value
		 *      This parameter receives the value that the user has typed.
		 * @return
		 *      Indicates the outcome of the validation. This is sent to the browser.
		 */
		public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
			if(value.length()==0)
				return FormValidation.error("Please set path to play");
			return FormValidation.ok();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
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
			// To persist global configuration information,
			// set that to properties and call save().
			path = formData.getString("play_path");
			// ^Can also use req.bindJSON(this, formData);
			//  (easier when there are many fields; need set* methods for this, like setUseFrench)

			save();
			return super.configure(req,formData);
		}

		/**
		 * This method returns true if the global configuration says we should speak French.
		 */
		public String path() {
			return path;
		}
	}
}

