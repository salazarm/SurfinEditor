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
		Document doc = new Document("samplefiles", new CopyOnWriteArrayList<String>(), "2000", 0);
		assertEquals("samplefiles", doc.getName());
		
		// Check insert function.
		doc.insertChar(0, "60");
		Thread.sleep(10);
		assertEquals("60a", doc.toString());
		
		doc.insertChar(0, "100");
		Thread.sleep(10);
		assertEquals("100a60a", doc.toString());
		
		// Check remove function.
		doc.removeChar(0);
		Thread.sleep(10);
		assertEquals("60a", doc.toString());
		
		// Check series of commands.
		doc.insertChar(1, "0");
		doc.insertChar(2, "1");
		doc.insertChar(3, "2");
		doc.insertChar(4, "3");
		doc.removeChar(3);
		doc.insertChar(4, "4");
		Thread.sleep(10);
		assertEquals("60a0a1a3a4a", doc.toString());
	}
}
