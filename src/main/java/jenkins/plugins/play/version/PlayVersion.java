package jenkins.plugins.play.version;

import hudson.model.AbstractDescribableImpl;

/**
 * Abstract representation of a Play command. Every command implementing this
 * class must provide the respective command line (i.e.: 'clean', 'compile').
 */
public abstract class PlayVersion extends AbstractDescribableImpl<PlayVersion> {

}
