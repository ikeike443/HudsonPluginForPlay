/**
 * 
 */
package com.gmail.ikeike443;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import hudson.model.FreeStyleBuild;
import hudson.model.Result;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

import com.gmail.ikeike443.PlayAutoTestBuilder;

/**
 * @author ikeike443
 *
 */
public class PlayAutoTestBuilderTest extends HudsonTestCase {

	@Test
	public void testPlayPathIsNull() throws Exception {

		FreeStyleProject pj = createFreeStyleProject("playpathisnull");
		pj.getBuildersList().add(new PlayAutoTestBuilder("auto-test", "-Xmx1024m"));
		FreeStyleBuild build = pj.scheduleBuild2(0).get();
		System.out.println(build.getDisplayName()+" completed");

		String s = FileUtils.readFileToString(build.getLogFile());
		System.out.println(s);
		
		assertEquals(Result.FAILURE,build.getResult());
	}

	//TODO write test play path resolved

}
