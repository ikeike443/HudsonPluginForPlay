/**
 * 
 */
package com.gmail.ikeike443;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author ikeda
 *
 */
public class PlayTestResultActionTest extends HudsonTestCase {
	@Test
	public void testTestResultIsOK() throws Exception {
		FreeStyleProject pj = createFreeStyleProject("testsuccess");
		pj.getBuildersList().add(new PlayAutoTestBuilder("auto-test", "-Xmx1024"));
		FreeStyleBuild build = pj.scheduleBuild2(0).get();//must be failure but don't care
		System.out.println(build.getDisplayName()+" completed");
		//setup
		FilePath root = new FilePath(build.getRootDir());
		rsrccopy("src/test/resources/successresult/", "test-result/", root );
		rsrccopy("src/test/resources/successresult/", "conf/", root );
		
		Properties conf = new Properties();
		InputStream inputStream = new FileInputStream(new File(
				build.getRootDir()+"/conf/application.conf"));
		conf.load(inputStream);
		inputStream.close();
		
		PlayTestResultAction act = new PlayTestResultAction(build);
		act.setPassed(new FilePath(root, "test-result/result.passed").exists());
		act.setAppName(conf.getProperty("application.name"));//TODO set default name
		build.addAction(act);
		
		PlayTestResultAction action = build.getAction(act.getClass());
		
		assertTrue("application.log don't exists!",action.getApplicationlogExists());
		assertFalse("application.log is empty!",action.getApplicationlog().isEmpty());
		assertFalse("test result is empty!",action.getTestResults().isEmpty());
		assertEquals("status is not passed!","passed",action.getStatusatall());
		assertEquals("app name is not playscala!","playscala",action.getAppName());
		
	}
	
	@Test
	public void testTestResultIsNG() throws Exception {
		FreeStyleProject pj = createFreeStyleProject("testfailure");
		pj.getBuildersList().add(new PlayAutoTestBuilder("auto-test", "-Xmx1024"));
		FreeStyleBuild build = pj.scheduleBuild2(0).get();//must be failure but don't care
		System.out.println(build.getDisplayName()+" completed");
		//setup
		FilePath root = new FilePath(build.getRootDir());
		rsrccopy("src/test/resources/failedresult/", "test-result/", root );
		rsrccopy("src/test/resources/failedresult/", "conf/", root );
		
		Properties conf = new Properties();
		InputStream inputStream = new FileInputStream(new File(
				build.getRootDir()+"/conf/application.conf"));
		conf.load(inputStream);
		inputStream.close();
		
		PlayTestResultAction act = new PlayTestResultAction(build);
		act.setPassed(new FilePath(root, "test-result/result.passed").exists());
		act.setAppName(conf.getProperty("application.name"));
		build.addAction(act);
		
		PlayTestResultAction action = build.getAction(act.getClass());
		
		assertTrue("application.log is not exists!",action.getApplicationlogExists());
		assertFalse("application.log is empty!",action.getApplicationlog().isEmpty());
		assertFalse("test result is empty!",action.getTestResults().isEmpty());
		assertEquals("status is not failed!","failed",action.getStatusatall());
		assertEquals("app name is not playscala!","playscala",action.getAppName());
		
	}
	
	private void rsrccopy(String rscrpath, String filename, FilePath copyto) throws IOException, InterruptedException{
		File file = new File(rscrpath);
		FilePath[] sucresult = new FilePath(file).list(filename+"**");
		for (FilePath filePath : sucresult) {
			filePath.copyTo(new FilePath(copyto, filename+filePath.getName()));
		}
	}
	
}
