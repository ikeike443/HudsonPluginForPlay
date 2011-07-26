/**
 * 
 */
package com.gmail.ikeike443;

import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.FreeStyleProject;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * @author ikeike443
 *
 */
public class PlayAutoTestBuilderTest extends HudsonTestCase {

	@Test
	public void testPlayPathIsNull() throws Exception {

		FreeStyleProject pj = createFreeStyleProject("playpathisnull");
		pj.getBuildersList().add(new PlayAutoTestBuilder("auto-test",null,null,null,null, null));
		FreeStyleBuild build = pj.scheduleBuild2(0).get();
		System.out.println(build.getDisplayName()+" completed");

		String s = FileUtils.readFileToString(build.getLogFile());
		System.out.println(s);
		
		assertEquals(Result.FAILURE,build.getResult());
	}
}
