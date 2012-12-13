package server;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Test;

public class DocTest {
	
	/**
	 * The solo purpose of this testing is to the ensure the basic functions (insert and remove) of a document 
	 * function properly.
	 */
	
	@Test
	public void BasicFuntionTest() throws InterruptedException{
		Document doc = new Document("samplefiles", new CopyOnWriteArrayList<String>(), "1000samplefiles1000", 0);
		assertEquals("samplefiles", doc.getName());
		
		// Check insert function.
		doc.insertChar(0, "60");
		Thread.sleep(25);
		assertEquals("60a", doc.toString());
		
		doc.insertChar(0, "100");
		Thread.sleep(25);
		assertEquals("100a60a", doc.toString());
		
		// Check remove function.
		doc.removeChar(1);
		Thread.sleep(25);
		assertEquals("60a", doc.toString());
		
		// Check series of commands.
		doc.insertChar(1, "10");
		doc.insertChar(2, "20");
		doc.insertChar(3, "30");
		doc.insertChar(4, "40");
		doc.removeChar(3); // when we remove on the server we automatically remove 1 less 
		doc.insertChar(4, "50");
		Thread.sleep(50);
		assertEquals("60a10a30a40a50a", doc.toString());
	}
}
