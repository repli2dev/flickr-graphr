package cz.muni.fi.pb138.flickrgraphr.backend.cron;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test of TaskName class
 * @author Jan Drábek
 */
public class TaskNameTest {
	
	public TaskNameTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of equals method, of class TaskName.
	 */
	@Test
	public void testEquals() {
		assertTrue(new TaskName(10,"Nazev").equals(new TaskName(10,"Nazev")));
		assertTrue(new TaskName(10,null).equals(new TaskName(10,null)));
		
		assertFalse(new TaskName(10,"Ahoj").equals(new TaskName(10,null)));
		assertFalse(new TaskName(10,"Ahoj").equals(new TaskName(10,"Ahoj2")));
		assertFalse(new TaskName(10,null).equals(new TaskName(10,"Ahoj2")));
		assertFalse(new TaskName(20,"Ahoj2").equals(new TaskName(10,"Ahoj2")));
	}

	/**
	 * Test of hashCode method, of class TaskName.
	 */
	@Test
	public void testHashCode() {
		assertEquals(new TaskName(10,"Nazev").hashCode(), new TaskName(10,"Nazev").hashCode());
		assertEquals(new TaskName(10,"Nazev").hashCode(), new TaskName(10,"Nazev").hashCode());
		assertEquals(new TaskName(10,null).hashCode(), new TaskName(10,null).hashCode());
		
		assertNotSame(new TaskName(20,"Nazev").hashCode(), new TaskName(10,"Nazev").hashCode());
		assertNotSame(new TaskName(10,"Nazev2").hashCode(), new TaskName(10,"Nazev").hashCode());
		assertNotSame(new TaskName(10,null).hashCode(), new TaskName(20,null).hashCode());
	}

	/**
	 * Test of getName method, of class TaskName.
	 */
	@Test
	public void testGetName() {
		assertEquals("Nazev", new TaskName(10,"Nazev").getName());
		assertEquals(null, new TaskName(10,null).getName());
		assertNotSame("Nazev2", new TaskName(10,"Nazev").getName());
		assertNotSame(null, new TaskName(10,"Nazev").getName());
	}
}
