package com.gmail.ikeike443;
import hudson.Extension;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.*;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author ikeike443
 */
public class PlayAutoTestBuilder extends Builder{

	private final String play_cmd;

	@DataBoundConstructor
	public PlayAutoTestBuilder(String play_cmd) {
		this.play_cmd = play_cmd;
	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getPlay_cmd() {
		return play_cmd;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
		String playpath = null;
		if(getDescriptor().path()!= null){
			playpath = getDescriptor().path();
		}else{
			listener.getLogger().println("play path is null");
			return false;
		}
/*		if(play_path_job != null){
			playpath = play_path_job;
		}*/
		listener.getLogger().println("playpath is "+playpath);
		try {
			String cmd = playpath + " " + play_cmd +" " /*"play auto-test "*/+build.getWorkspace().toString();
			listener.getLogger().println(cmd);
			Proc proc = launcher.launch(cmd, new String[0],listener.getLogger(),build.getWorkspace());
			int exitcode = proc.join();	

			if(exitcode == 0){
				//check test-result
				if(new File(build.getWorkspace().toString()+"/test-result/result.passed").exists()){
					return true;
				}else{
					File applicationLog = new File(build.getWorkspace().toString()+"/test-result/application.log");
					if(applicationLog.exists()){
						byte[] buffer = new byte[2000];
						FileInputStream inputStream = new FileInputStream(applicationLog);
						int bytesRead = 0;
						while((bytesRead = inputStream.read(buffer)) > 0) {
							listener.getLogger().write(buffer,0, bytesRead);
						}
						listener.getLogger().flush();
					}
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
			path = formData.getString("play_path");
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

