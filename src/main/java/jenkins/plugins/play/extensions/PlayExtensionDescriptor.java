/**
 * 
 */
package jenkins.plugins.play.extensions;

import jenkins.model.Jenkins;
import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;

/**
 * @author rafaelrezende
 *
 */
public abstract class PlayExtensionDescriptor extends Descriptor<PlayExtension>{
	
	public static DescriptorExtensionList<PlayExtension,PlayExtensionDescriptor> all() {
        return Jenkins.getInstance().getDescriptorList(PlayExtension.class);
    }

}
