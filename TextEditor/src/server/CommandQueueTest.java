package server;

import static org.junit.Assert.*;
import org.junit.Test;

public class CommandQueueTest {
	
	@Test
	public void InsertCommandTest(){
		String[] command = {"INSERT", "12", "a"};
		Command insert = new Command(command);
		assertEquals(Command.CommandType.INSERT, insert.command);
	}
	
	@Test
	public void deleteCommandTest(){
		String[] command = {"DELETE", "10"};
		Command delete = new Command(command);
		assertEquals(Command.CommandType.DELETE, delete.command);
	}
	
	@Test
	public void queueTest(){
		CommandQueue queue = new CommandQueue();
		Command[] doublepop = new Command[2];
		doublepop = queue.doublePop();
		assertTrue(doublepop==null);
		String[] insertcommand = {"INSERT", "12", "a"};
		Command insert = new Command(insertcommand);
		String[] deletecommand = {"DELETE", "10"};
		Command delete = new Command(deletecommand);
		queue.add(insert);
		doublepop = queue.doublePop();
		assertTrue(doublepop[0].command.equals(Command.CommandType.INSERT) && doublepop[1]==null);
		queue.add(insert);
		queue.add(delete);
		doublepop = queue.doublePop();
		assertTrue(doublepop[0].command.equals(Command.CommandType.INSERT) && doublepop[1].command.equals(Command.CommandType.DELETE));
	}
}
