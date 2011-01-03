/**
 * 
 */
package ikeike443;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractBuild;

/**
 * @author ikeike443
 *
 */
public class PlayTestResultAction implements Action {
    private AbstractBuild<?, ?> owner;
    public AbstractBuild<?, ?> getOwner() {
        return this.owner;
    }

    public PlayTestResultAction(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

	/* (non-Javadoc)
	 * @see hudson.model.Action#getIconFileName()
	 */
	public String getIconFileName() {
		// TODO Auto-generated method stub
		return "document.gif";
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "playauto-test";
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName() {
		// TODO Auto-generated method stub
		return "playTestResult";
	}
	
	public String getApplicationlog(){
		return owner.getRootDir()+"/test-result/application.log";
	}
	public String[] getTestResults(){
		return new File(owner.getRootDir()+"/test-result").list(new FilenameFilter(){
            public boolean accept(File file, String name) {  
                boolean ret = name.endsWith(".html");   
                return ret;  
            }
		});
	}

}
