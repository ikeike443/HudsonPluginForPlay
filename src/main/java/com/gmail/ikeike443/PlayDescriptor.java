/**
 * 
 */
package com.gmail.ikeike443;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

/**
 * @author rafaelrezende
 *
 */
public class PlayDescriptor extends BuildStepDescriptor<Builder>{
	
	private String playId;
	private String playHome;
	
	public PlayDescriptor() {
		load();
	}

	@Override
	public boolean isApplicable(Class<? extends AbstractProject> arg0) {
		return true;
	}

	@Override
	public String getDisplayName() {
		return "Play!Framework";
	}
	
	@Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        json = json.getJSONObject("play");
        playId = json.getString("playId");
        playHome = json.getString("playHome");
        save();
        return true;
    }
	
	

}
