package com.gmail.ikeike443;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class PlayAutoTestJobProperty extends JobProperty<AbstractProject<?,?>> {
    public final String application_path;

    @DataBoundConstructor
    public PlayAutoTestJobProperty(String application_path) {
        this.application_path = application_path;
    }

    public String getApplication_path() {
        return application_path;
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "Play! properties";
        }

        @Override
        public boolean isApplicable(java.lang.Class<? extends Job> jobType)    {
            return AbstractProject.class.isAssignableFrom(jobType);
        }
    }
}
