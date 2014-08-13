/**
 * 
 */
package jenkins.plugins.play;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jenkins.plugins.play.commands.PlayAutoTest;
import jenkins.plugins.play.commands.PlayBuild;
import jenkins.plugins.play.commands.PlayClean;
import jenkins.plugins.play.commands.PlayCommand;
import jenkins.plugins.play.commands.PlayCompile;
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
import jenkins.plugins.play.version.Play2x;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author rafaelrezende
 *
 */
public class TestGenerateCommands {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void generateCommands() {
		
		// Check the command list generated from the PlayBuild command generator.
		// The command generator retrieves a list in the same order of the parameters added, plus additional flags.
		// The Play version (v1, v2) is not important in here.
		List<PlayCommand> commands = new ArrayList<PlayCommand>();
		commands.add(new PlayClean());
		commands.add(new PlayDist());
		commands.add(new PlayCompile());
		commands.add(new PlayAutoTest());
		commands.add(new PlayBuild());
		commands.add(new PlayCustom("custom comm"));
		commands.add(new PlayInstall());
		commands.add(new PlayJavadoc());
		commands.add(new PlayPackage());
		commands.add(new PlayPrecompile());
		commands.add(new PlayPublish());
		commands.add(new PlayTest());
		commands.add(new PlayTestOnly("com.test.Test"));
		commands.add(new PlayWar("path"));
		
		Play2x play2xBuild = new Play2x(commands);
		
		PlayBuilder playBuilder = new PlayBuilder(".", ".", play2xBuild);
		
		List<String> generatedCommands = playBuilder.generatePlayParameters();

		int i = 0;
		assertEquals("-Dsbt.log.noformat=true", generatedCommands.get(i++));
		assertEquals("clean", generatedCommands.get(i++));
		assertEquals("dist", generatedCommands.get(i++));
		assertEquals("compile", generatedCommands.get(i++));
		assertEquals("auto-test", generatedCommands.get(i++));
		assertEquals("build-module", generatedCommands.get(i++));
		assertEquals("custom comm", generatedCommands.get(i++));
		assertEquals("install", generatedCommands.get(i++));
		assertEquals("javadoc", generatedCommands.get(i++));
		assertEquals("package", generatedCommands.get(i++));
		assertEquals("precompile", generatedCommands.get(i++));
		assertEquals("publish", generatedCommands.get(i++));
		assertEquals("test", generatedCommands.get(i++));
		assertEquals("testOnly com.test.Test", generatedCommands.get(i++));
		assertEquals("war -o path", generatedCommands.get(i++));
	}
	
	@Test
	public void generateCustomCommand() {
		
		// Check the output with a single command
		List<PlayCommand> commands = new ArrayList<PlayCommand>();
		PlayCustom command = new PlayCustom("testing custom");
		commands.add(command);
		
		Play2x play2xBuild = new Play2x(commands);
		
		PlayBuilder playBuilder = new PlayBuilder(".", ".", play2xBuild);
		String playBuilderCommand = StringUtils.join(playBuilder.generatePlayParameters(), " ");

		assertEquals("-Dsbt.log.noformat=true testing custom", playBuilderCommand);
	}
	
	@Test
	public void generateTestOnlyCommand() {
		
		// Check the output with a single command
		List<PlayCommand> commands = new ArrayList<PlayCommand>();
		PlayTestOnly command = new PlayTestOnly("com.test.Test");
		commands.add(command);
		
		Play2x play2xBuild = new Play2x(commands);
		
		PlayBuilder playBuilder = new PlayBuilder(".", ".", play2xBuild);
		String playBuilderCommand = StringUtils.join(playBuilder.generatePlayParameters(), " ");

		assertEquals("-Dsbt.log.noformat=true testOnly com.test.Test", playBuilderCommand);
	}

}
