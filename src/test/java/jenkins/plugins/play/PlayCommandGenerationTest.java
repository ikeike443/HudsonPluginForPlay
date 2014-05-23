/**
 * 
 */
package jenkins.plugins.play;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import jenkins.plugins.play.commands.PlayClean;
import jenkins.plugins.play.commands.PlayDist;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

/**
 * @author rafaelrezende
 * 
 */
public class PlayCommandGenerationTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	@Test
	public void first() throws Exception {
		FreeStyleProject project = j.createFreeStyleProject();
		project.getBuildersList().add(new PlayBuilder(".", ".", ".", Arrays.asList(new PlayClean(), new PlayDist())));
		project.getBuildersList().add(new Shell("echo hello"));
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		System.out.println(build.getDisplayName() + " completed");
		// TODO: change this to use HtmlUnit
		String s = FileUtils.readFileToString(build.getLogFile());
		System.out.println(s);
		assertTrue(s.contains("Play executable not found"));
	}

	@Test
	public void playRun() throws Exception {
		
		File mockFile = Mockito.mock(File.class);
		Mockito.when(mockFile.exists()).thenReturn(true);
		
		FreeStyleProject project = j.createFreeStyleProject();
		project.getBuildersList().add(new PlayBuilder(".", ".", ".", Arrays.asList(new PlayClean(), new PlayDist())));
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		System.out.println(build.getDisplayName() + " completed");
		// TODO: change this to use HtmlUnit
		String s = FileUtils.readFileToString(build.getLogFile());
		System.out.println(s);
		assertTrue(s.contains("clean"));

	}
}
