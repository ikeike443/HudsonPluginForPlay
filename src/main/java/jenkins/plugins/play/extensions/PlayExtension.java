/**
 * 
 */
package jenkins.plugins.play.extensions;

import hudson.model.AbstractDescribableImpl;

/**
 * @author rafaelrezende
 *
 */
public abstract class PlayExtension extends AbstractDescribableImpl<PlayExtension>{
	
	public String getCommand() {
		return null;
	}
	
	public String getParameter() {
		return "";
	}
	
}
