package server;

import static org.junit.Assert.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Test;

public class DocTest {
	
	/**
	 * The solo purpose of this testing is to the ensure the basic functions (insert and remove) of a document 
	 * function properly.
	 */
	
	@Test
	public void BasicFuntionTest() throws InterruptedException{
		Document doc = new Document("samplefiles", new CopyOnWriteArrayList<Character>(), "2000", 0);
		assertEquals("samplefiles", doc.getName());
		
		// Check insert function.
		doc.insertChar(0, "a");
		Thread.sleep(10);
		assertEquals("a", doc.toString());
		
		doc.insertChar(0, "b");
		Thread.sleep(10);
		assertEquals("ba", doc.toString());
		
		// Check remove function.
		doc.removeChar(0);
		Thread.sleep(10);
		assertEquals("a", doc.toString());
		
		// Check series of commands.
		doc.insertChar(1, "c");
		doc.insertChar(2, "d");
		doc.insertChar(3, "e");
		doc.insertChar(4, "f");
		doc.removeChar(3);
		doc.insertChar(4, "g");
		Thread.sleep(10);
		assertEquals("acdfg", doc.toString());
	}
}
