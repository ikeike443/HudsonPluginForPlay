package com.gmail.ikeike443;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractProject;
import hudson.model.Job;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class PlayAutoTestJobProperty extends JobProperty<AbstractProject<?,?>> {
	private PlayAutoTestJobPropertyHolder holder;

	/**
	 * Holder for job properties values.
	 */
	public static class PlayAutoTestJobPropertyHolder {
		private String applicationPath;

		@DataBoundConstructor
		public PlayAutoTestJobPropertyHolder(String applicationPath) {
			this.applicationPath = applicationPath;
		}
	}

	@DataBoundConstructor
	public PlayAutoTestJobProperty(PlayAutoTestJobPropertyHolder holder) {
		this.holder = holder;
	}

	public String getApplicationPath() {
		return holder == null ? null : holder.applicationPath;
	}

	public boolean isApplicationPathChecked() {
		return holder != null;
	}

	@Extension
	public static class DescriptorImpl extends JobPropertyDescriptor {
		public DescriptorImpl() {
			super(PlayAutoTestJobProperty.class);
			load();
		}

		@Override
		public String getDisplayName() {
			return "Play! properties";
		}

		@Override
		public boolean isApplicable(@SuppressWarnings("rawtypes") java.lang.Class<? extends Job> jobType)    {
			return AbstractProject.class.isAssignableFrom(jobType);
		}

		@Override
		public PlayAutoTestJobProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			return (PlayAutoTestJobProperty)req.bindJSON(clazz,formData);
		}
	}
}
