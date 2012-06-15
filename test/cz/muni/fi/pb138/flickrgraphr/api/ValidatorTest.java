package cz.muni.fi.pb138.flickrgraphr.api;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test of Validator class
 *
 * @author Jan Drabek
 */
public class ValidatorTest {

	public ValidatorTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of getIdType method, of class Validator.
	 */
	@Test
	public void testGetIdType() {
		assertEquals(IdType.invalidId, Validator.getIdType(null));
		assertEquals(IdType.invalidId, Validator.getIdType(""));
		assertEquals(IdType.invalidId, Validator.getIdType("asd"));
		
		assertEquals(IdType.email, Validator.getIdType("josef.novak@atlas.cz"));
		assertEquals(IdType.email, Validator.getIdType("grep@example.com"));
		
		assertEquals(IdType.flickrId, Validator.getIdType("11111111@A22"));
		assertEquals(IdType.flickrId, Validator.getIdType("12345678@A22"));
		assertEquals(IdType.flickrId, Validator.getIdType("2345678@A22"));
		
		assertEquals(IdType.name, Validator.getIdType("Testovací jméno"));
		assertEquals(IdType.name, Validator.getIdType("Test"));
	}

	/**
	 * Test of isEmail method, of class Validator.
	 */
	@Test
	public void testIsEmail() {
		// Valid cases
		assertTrue(Validator.isEmail("novak@atlas.cz"));
		assertTrue(Validator.isEmail("josef.novak@atlas.cz"));
		// Invalid cases
		assertFalse(Validator.isEmail(""));
		assertFalse(Validator.isEmail("@"));
		assertFalse(Validator.isEmail("@atlas.cz"));
		assertFalse(Validator.isEmail("josef.novak@atlas"));
		assertFalse(Validator.isEmail("josef.novak@"));
		assertFalse(Validator.isEmail("josef.novak"));
		assertFalse(Validator.isEmail("josef..novak@novak.cz"));
		assertFalse(Validator.isEmail("josef.novak@novak..cz"));
		assertFalse(Validator.isEmail("jos\\ef.novak@novak.cz"));
	}

	/**
	 * Test of isUserId method, of class Validator.
	 */
	@Test
	public void testIsUserId() {
		assertFalse(Validator.isUserId(""));
		assertFalse(Validator.isUserId("12345678"));
		assertFalse(Validator.isUserId("@"));
		assertFalse(Validator.isUserId("N2"));
		assertFalse(Validator.isUserId("@22"));
		assertFalse(Validator.isUserId("@A22"));
		assertFalse(Validator.isUserId("@N1"));
		assertFalse(Validator.isUserId("123454@"));
		assertFalse(Validator.isUserId("123454N2"));
		assertFalse(Validator.isUserId("123454@22"));
		assertFalse(Validator.isUserId("123454@N1"));
		assertFalse(Validator.isUserId("123454@A22"));
		assertTrue(Validator.isUserId("11111111@A22"));
	}

	/**
	 * Test of isDate method, of class Validator.
	 */
	@Test
	public void testIsDate() {
		// Some valid dates, check for one digit dates
		assertTrue(Validator.isDate("2012-01-01"));
		assertTrue(Validator.isDate("2012-05-10"));
		assertTrue(Validator.isDate("2012-2-28"));
		assertTrue(Validator.isDate("2012-2-2"));
		assertTrue(Validator.isDate("2012-12-2"));
		assertTrue(Validator.isDate("2012-2-12"));
		assertTrue(Validator.isDate("2012-12-31"));

		// Test some invalid dates 
		assertFalse(Validator.isDate(""));
		assertFalse(Validator.isDate("2012"));
		assertFalse(Validator.isDate("2012-1"));
		assertFalse(Validator.isDate("2012-01"));
		assertFalse(Validator.isDate("-1000"));
		assertFalse(Validator.isDate("-1000-1"));
		assertFalse(Validator.isDate("-1000-01"));
		assertFalse(Validator.isDate("-1000-1-1"));
		assertFalse(Validator.isDate("-1000-01-01"));
		assertFalse(Validator.isDate("-1000-1-01"));
		assertFalse(Validator.isDate("-1000-01-1"));

		// Some bonuses if it really tests date which really existed
		assertFalse(Validator.isDate("2011-02-29"));
		assertFalse(Validator.isDate("2011-13-10"));
	}

	/**
	 * Test of getDate method, of class Validator.
	 */
	@Test
	public void testGetDate() {
		// Not tested...
		// What was first, the egg or the chicken?
	}
}
