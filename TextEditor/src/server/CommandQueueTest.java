package server;

import static org.junit.Assert.*;
import org.junit.Test;

public class CommandQueueTest {
	
	@Test
	public void InsertCommandTest(){
		String[] command = {"insert", "12", "a"};
		Command insert = new Command(command);
		assertEquals(Command.CommandType.INSERT, insert.command);
	}
}
