/**
 * 
 */
package jenkins.plugins.play;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author rafaelrezende
 *
 */
public class TestValidatePlayTarget extends TestCase{
	
	@Test
	public void testMinVersionConversion() {
		
		assertEquals(ValidatePlayTarget.versionToLong("0.0.1", false), 1000000L);
		assertEquals(ValidatePlayTarget.versionToLong("1", false), 10000000000L);
		assertEquals(ValidatePlayTarget.versionToLong("1.2.2", false), 10202000000L);
		assertEquals(ValidatePlayTarget.versionToLong("1.2.3.4.5.6", false), 10203040506L);
		assertEquals(ValidatePlayTarget.versionToLong("1.2.3.4.5.6.7", false), 10203040506L);
		assertEquals(ValidatePlayTarget.versionToLong("99.99.99.99.99.99.99", false), 999999999999L);
	}
	
	@Test
	public void testMaxVersionConversion() {
		
		assertEquals(ValidatePlayTarget.versionToLong("0.0.1", true), 1999999L);
		assertEquals(ValidatePlayTarget.versionToLong("1", true), 19999999999L);
		assertEquals(ValidatePlayTarget.versionToLong("1.2.2", true), 10202999999L);
		assertEquals(ValidatePlayTarget.versionToLong("1.2.3.4.5.6", true), 10203040506L);
		assertEquals(ValidatePlayTarget.versionToLong("1.2.3.4.5.6.7", true), 10203040506L);
		assertEquals(ValidatePlayTarget.versionToLong("99.99.99.99.99.99.99", true), 999999999999L);
	}
	
	@Test
	public void testRangeMatching() {
		
		assertTrue(ValidatePlayTarget.compareVersions("0", "0", "0.5"));
		assertTrue(ValidatePlayTarget.compareVersions("0.2", "0", "0.5"));
		assertTrue(ValidatePlayTarget.compareVersions("1.2", "0", "1.5"));
		assertTrue(ValidatePlayTarget.compareVersions("1.2", "1", "1.5"));
		assertTrue(ValidatePlayTarget.compareVersions("1.2", "1.0", "1.5"));
		assertTrue(ValidatePlayTarget.compareVersions("1.2", "1.2", "1.5"));
		assertTrue(ValidatePlayTarget.compareVersions("2", "1.1.1.1.1.1", "2.2.2.2.2.2"));
		assertTrue(ValidatePlayTarget.compareVersions("1.1.1.1.1.1", "1", "2"));
		assertTrue(ValidatePlayTarget.compareVersions("1.1.1.1.1.1.1", "1", "2"));
		assertTrue(ValidatePlayTarget.compareVersions("99.99.99.99.99.99", "99.99.99.99.99.99", "99.99.99.99.99.99"));
		assertTrue(ValidatePlayTarget.compareVersions("99.99.99.99.99.99.99", "99.99.99.99.99.99.99", "99.99.99.99.99.99.99"));
		
		assertFalse(ValidatePlayTarget.compareVersions("", "1.2", "2.5"));
		assertFalse(ValidatePlayTarget.compareVersions("0.1", "0.2", "0.5"));
		assertFalse(ValidatePlayTarget.compareVersions("0", "0.2", "0.5"));
		assertFalse(ValidatePlayTarget.compareVersions("1.2", "1.2.1", "1.5"));
		assertFalse(ValidatePlayTarget.compareVersions("1.2.3.4.5.8", "1.2.3.4.5.6", "1.2.3.4.5.7"));
	}
	
	@Test
	public void testVersionToLong() {
		
		assertEquals(10200330000L, ValidatePlayTarget.versionToLong("1.2.0.33", false));
		assertEquals(10200339999L, ValidatePlayTarget.versionToLong("1.2.0.33", true));
		assertEquals(0L, ValidatePlayTarget.versionToLong("0", false));
		assertEquals(12099999999L, ValidatePlayTarget.versionToLong("1.20", true));
		assertEquals(99999999999L, ValidatePlayTarget.versionToLong("9", true));
		assertEquals(999999999999L, ValidatePlayTarget.versionToLong("99", true));
	}
}
