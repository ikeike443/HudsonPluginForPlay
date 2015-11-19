/**
 * 
 */
package jenkins.plugins.play;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jenkins.plugins.play.commands.PlayAutoTest;
import jenkins.plugins.play.commands.PlayClean;
import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCustom;
import jenkins.plugins.play.commands.PlayDist;
import jenkins.plugins.play.commands.PlayInstall;
import jenkins.plugins.play.commands.PlayJavadoc;
import jenkins.plugins.play.commands.PlayPackage;
import jenkins.plugins.play.commands.PlayPrecompile;
import jenkins.plugins.play.commands.PlayPublish;
import jenkins.plugins.play.commands.PlayTest;
import jenkins.plugins.play.commands.PlayTestOnly;
import jenkins.plugins.play.commands.PlayWar;
import jenkins.plugins.play.version.Play1x;
import jenkins.plugins.play.version.Play2x;
import jenkins.plugins.play.version.PlayVersion;
import hudson.Proc;
import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.FreeStyleProject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * These tests evaluate the execution of Play1x and Play2x (equivalent to Activator).
 * 
 * @author rafaelrezende
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PlayBuilder.class, Proc.class})
public class PlayCommandExecutionTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	/**
	 * Checks if the play command is properly executed in Play2x mode. Commands
	 * are concatenated in an expected order. Special commands should exhibits
	 * parameters (testOnly, custom) between quotes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void Play2x() throws Exception {
		
		List<PlayCommand> commands = new ArrayList<PlayCommand>();
		commands.add(new PlayClean());
		commands.add(new PlayDist());
		commands.add(new PlayPackage());
		commands.add(new PlayPublish());
		commands.add(new PlayTest());
		commands.add(new PlayTestOnly("com.package.Test"));
		commands.add(new PlayInstall());
		commands.add(new PlayTestOnly("com.package.Test2"));
		commands.add(new PlayCustom("custom"));
		commands.add(new PlayCustom("custom double"));
		PlayVersion play2x = new Play2x(commands);
		
		String playFolder = "/play-folder";
		String projectFolder = ".";
		
		// Mock the function to retrieve the play executable, should return a file even if the play executable does not exist
		PowerMockito.mockStatic(PlayBuilder.class);
		// Play command isn't expected to exist in the test environment. Therefore call the echo command instead.
		Mockito.when(PlayBuilder.getPlayExecutable(Mockito.anyString())).thenReturn(new File("echo"));
		
		FreeStyleProject project = j.createFreeStyleProject();
		project.getBuildersList().add(new PlayBuilder(playFolder, projectFolder, play2x));
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		
		// Build fails because play tools and projects are not expected to really exist.
		assertEquals(Result.FAILURE, build.getResult());

		// Still, checks that the output contains the completed and ordered command
		String s = FileUtils.readFileToString(build.getLogFile());
		System.out.println(s);
		assertTrue(s.contains("echo -Dsbt.log.noformat=true clean dist package publish test \"testOnly com.package.Test\" install \"testOnly com.package.Test2\" custom \"custom double\""));
	}
	
	/**
	 * Checks if the command execution in Play1x mode is using a series of
	 * independent commands for each chosen parameter (test, precompile etc).
	 * Play1x does not support concatenated commands.
	 * 
	 * @throws Exception
	 */
	@Test
	public void Play1x() throws Exception {
		
		List<PlayCommand> commands = new ArrayList<PlayCommand>();
		commands.add(new PlayClean());
		commands.add(new PlayAutoTest());
		commands.add(new PlayJavadoc());
		commands.add(new PlayPrecompile());
		commands.add(new PlayWar("./output.war"));
		commands.add(new PlayCustom("custom"));
		commands.add(new PlayCustom("custom double"));
		commands.add(new PlayClean());
		PlayVersion play1x = new Play1x(commands);
		
		String playFolder = "/play-folder";
		String projectFolder = ".";
		
		// Mock the function to retrieve the play executable, should return a file even if the play executable does not exist
		PowerMockito.mockStatic(PlayBuilder.class);
		// Play command isn't expected to exist in the test environment. Therefore call the echo command instead.
		Mockito.when(PlayBuilder.getPlayExecutable(Mockito.anyString())).thenReturn(new File("echo"));
		
		FreeStyleProject project = j.createFreeStyleProject();
		project.getBuildersList().add(new PlayBuilder(playFolder, projectFolder, play1x));
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		
		// Build fails because play tools and projects are not expected to really exist.
		assertEquals(Result.FAILURE, build.getResult());

		// Still, checks that the output contains the commands printed out individually
		String s = FileUtils.readFileToString(build.getLogFile());
		System.out.println(s);
		assertTrue(s.contains("echo clean"));
		assertTrue(s.contains("echo auto-test"));
		assertTrue(s.contains("echo javadoc"));
		assertTrue(s.contains("echo precompile"));
		assertTrue(s.contains("echo war -o ./output.war"));
		assertTrue(s.contains("echo custom"));
		assertTrue(s.contains("echo custom double"));
		
		// Clean should appear twice
		assertEquals(2, StringUtils.countMatches(s, "echo clean"));
	}

}
