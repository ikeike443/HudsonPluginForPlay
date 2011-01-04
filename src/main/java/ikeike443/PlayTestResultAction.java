/**
 * 
 */
package ikeike443;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;

import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractBuild;

/**
 * @author ikeike443
 *
 */
public class PlayTestResultAction implements Action{
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
		return "test-result/application.log";
	}
	public List<TestResult> getTestResults(){
		List<TestResult> rt = new ArrayList<PlayTestResultAction.TestResult>();
		String[] files = new File(owner.getRootDir()+"/test-result").list(new FilenameFilter(){
            public boolean accept(File file, String name) {  
                boolean ret = name.endsWith(".html");   
                return ret;  
            }
		});
		for (final String file : files) {
			rt.add(new TestResult(){{
				Pattern ptn = Pattern.compile("(.*).(passed|failed).html");
				Matcher m = ptn.matcher(file);
				if(m.matches()){
					this.uri = file;
					this.name = m.group(0);
					this.status = m.group(1);
				}
			}});
		}
		return rt;
	}
	class TestResult{
		public String uri;
		public String name;
		public String status;
	}

	//TODO いちいちファイル読み書きしないで、Staplerのリダイレクトが使えないか検討する
	public void doDynamic(StaplerRequest req, StaplerResponse res) throws IOException{
		System.out.println(req.getRestOfPath());
		FileInputStream fileInputStream = new FileInputStream(owner.getRootDir()+req.getRestOfPath());
		ServletOutputStream out = res.getOutputStream();
		  int i;
		  while((i=fileInputStream.read())!=-1){out.write(i);}
		  out.close();
		
	}
	

}
