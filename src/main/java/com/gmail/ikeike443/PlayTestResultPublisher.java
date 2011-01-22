package com.gmail.ikeike443;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link PlayTestResultPublisher} is created. The created
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
public class PlayTestResultPublisher extends Publisher {


	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
	@DataBoundConstructor
	public PlayTestResultPublisher() {
	}


	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
		// this is where you 'build' the project
		// since this is a dummy, we just say 'hello world' and call that a build


		try {

			FilePath[] files = build.getProject().getWorkspace().list("test-result/*");
			FilePath root = new FilePath(build.getRootDir());
			for (FilePath filePath : files) {
				filePath.copyTo(new FilePath(root, "test-result/"+filePath.getName()));
			}
			//TODO 例外。リファクタリング。
			Properties conf = new Properties();
			InputStream inputStream = new FileInputStream(new File(
					build.getWorkspace()+"/conf/application.conf"));
			conf.load(inputStream);

			PlayTestResultAction act = new PlayTestResultAction(build);
			act.setPassed(new FilePath(root, "test-result/result.passed").exists());
			act.setAppName(conf.getProperty("application.name"));
			build.addAction(act);
			inputStream.close();
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public BuildStepMonitor getRequiredMonitorService() {
		// TODO Auto-generated method stub
		return BuildStepMonitor.NONE;
	}

	// overrided for better type safety.
	// if your plugin doesn't really define any property on Descriptor,
	// you don't have to do this.
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl)super.getDescriptor();
	}

	/**
	 * Descriptor for {@link PlayTestResultPublisher}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views.
	 *
	 * <p>
	 * See <tt>views/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Extension // this marker indicates Hudson that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}



		@Override
		public String getDisplayName() {
			return "Play! auto-test reports";
		}

	}

}

