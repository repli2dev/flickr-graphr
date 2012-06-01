/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.flickrgraphr.flickr.api;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * Test class user
 *
 * @author Jan Drabek
 */
public class UserTest {

	public UserTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of isValid method, of class User.
	 */
	@Test
	public void testIsValid() {
		assertFalse(new User(null, null).isValid());
		assertFalse(new User(null, "").isValid());
		assertFalse(new User("", null).isValid());

		assertTrue(new User("", "").isValid());
		assertTrue(new User("10", "Name").isValid());
		assertTrue(new User("30", "Name2").isValid());
	}

	/**
	 * Test of getId method, of class User.
	 */
	@Test
	public void testGetId() {
		assertEquals("30", new User("30", "Name2").getId());
		assertEquals(null, new User(null, "Name2").getId());
	}

	/**
	 * Test of getDisplayName method, of class User.
	 */
	@Test
	public void testGetDisplayName() {
		assertEquals("Name2", new User("30", "Name2").getDisplayName());
		assertEquals(null, new User(null, null).getDisplayName());
	}
}
