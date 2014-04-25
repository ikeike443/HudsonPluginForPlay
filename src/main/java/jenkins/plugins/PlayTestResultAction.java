/**
 * 
 */
package jenkins.plugins;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author ikeike443
 *
 */
public class PlayTestResultAction implements Action{
	private AbstractBuild<?, ?> owner;
	private String statusatall;
	private String appName;
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
		return "/plugin/play-autotest-plugin/favicon.png";
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		return "Play! Test Result";
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName() {
		return "playTestResult";
	}
	public boolean getApplicationlogExists(){
		return new File(owner.getRootDir()+"/test-result/application.log").exists();
	}

	public String getApplicationlog(){
		if(new File(owner.getRootDir()+"/test-result/application.log").exists()){
			return "test-result/application.log";
		}else{
			return "";
		}
	}
	public List<PlayTestResult> getTestResults(){
		List<PlayTestResult> rt = new ArrayList<PlayTestResult>();
		String[] files = new File(owner.getRootDir()+"/test-result").list(new FilenameFilter(){
			public boolean accept(File file, String name) {  
				boolean ret = name.endsWith(".html");   
				return ret;  
			}
		});
		for (final String file : files) {
			rt.add(new PlayTestResult(){{
				Pattern ptn = Pattern.compile("(.*).(passed|failed).html");
				Matcher m = ptn.matcher(file);
				if(m.matches()){
					this.uri = file;
					this.name = m.group(1);
					this.status = m.group(2);
				}
			}});
		}
		return rt;
	}


	public void doDynamic(StaplerRequest req, StaplerResponse res) throws IOException{
		System.out.println(req.getRestOfPath());
		FileInputStream fileInputStream = new FileInputStream(owner.getRootDir()+req.getRestOfPath());
		ServletOutputStream out = res.getOutputStream();
		int i;
		while((i=fileInputStream.read())!=-1){out.write(i);}
		out.close();

	}

	public void setPassed(boolean b) {
		this.statusatall = b? "passed" : "failed"; 

	}
	public String getStatusatall(){
		return this.statusatall;
	}

	public void setAppName(String appName) {
		this.appName = appName;

	}
	public String getAppName(){
		return this.appName;
	}



}
