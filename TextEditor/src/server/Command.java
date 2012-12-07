package server;

/**
 * A class for <b>Commands</b>:
 * Has the following <b>public</b> fields:
 * <ul>
 * <li>	<b>command</b> - Either <b> Command.CommandType.INSERT </b> or <b> Command.CommandType.DELETE </b></li>
 * <li> <b>index</b> - The index of where the user intended to remove an element in his client document model.</li>
 * <li> <b>characterToInsert</b> <i>(Only for INSERT commands)</i>  - The character to be inserted at <b> index></b>
 * </ul>
 * @author Marco Salazar
 *
 */
public class Command {
	public final CommandType command;
	public final int index;
	public char characterToInsert;
	private boolean isInsert = false; //for debugging
	
	/**
	 * INSERT for insert
	 * DELTE for delete
	 * RETAIN for retain (currently not being used).
	 * @author Marco Salazar
	 *
	 */
	public static enum CommandType {
		INSERT, DELETE, RETAIN;
	}

	Command(String[] command) {
		if (command[0].equals("INSERT"))
			this.command = CommandType.INSERT;
		else if (command[0].equals("DELETE"))
			this.command = CommandType.DELETE;
		else
			/* should not reach this next line of code */
			throw new IllegalStateException();
		this.index = Integer.parseInt(command[1]);
		if (this.command == CommandType.INSERT) {
			if (command[2].length() != 1)
				/* Should never reach this code */
				throw new IllegalStateException();
			this.characterToInsert = command[2].charAt(0);
			this.isInsert = true;
		}
	}
	@Override
	public String toString(){
		return ""+this.command+" "+this.index+" "+((isInsert)? this.characterToInsert : "");
	}
}