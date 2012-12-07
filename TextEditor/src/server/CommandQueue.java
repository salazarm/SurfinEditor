package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * A queue for our Command objects. 
 * @author Marco Salazar
 *
 */
public class CommandQueue implements Queue<Object>{
	private final ArrayList<Command> queue = new ArrayList<Command>();
	
	/**
	 * 
	 * @return A two-element Command list. The second item in the list will be null if only 1 command is in the queue. 
	 * @return null if the queue is empty.
	 */
	public Command[] doublePop(){
		Object first = this.poll();
		Object second = this.poll();
		if (first instanceof Command && second instanceof Command)
			return new Command[]{(Command)first,(Command) second};
		if(first instanceof Command)
			return new Command[]{(Command)first, null};
		else
			return null;
	}
	/**
	 * Not implemented because our server handles 1 command per client.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean addAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	/**
	 * Not implemented because there is no need.
	 */
	public void clear() {	
	}
	@Override
	public boolean contains(Object arg0) {
		return queue.contains(arg0);
	}
	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Unimplemented method
	 */
	public boolean containsAll(Collection arg0) {
		return false;
	}
	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Iterator iterator() {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean remove(Object o) {
		if(!(o instanceof Command))
			return false;
		queue.remove((Command)o);
		return true;
	}
	@SuppressWarnings("rawtypes")
	@Override
	public boolean removeAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public boolean retainAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public int size() {
		return queue.size();
	}
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray(Object[] arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean add(Object o) {
		if(!(o instanceof Command))
			return false;
		queue.add((Command) o);
		return true;
	}
	/**
	 * Returns null if the Queue is empty
	 */
	@Override
	public Object element() {
		if(!(queue.isEmpty()))
				return queue.get(0);
		return null;
	}
	
	@Override
	public boolean offer(Object o) {
		return add(o);
	}
	@Override
	public Command peek() {
		if (!queue.isEmpty())
			return queue.get(0);
		return null;
	}
	@Override
	public Command poll() {
		if (!queue.isEmpty()){
			Command head = queue.get(0);
			queue.remove(0);
			return head;
		}
		return null;
	}
	@Override
	public Object remove() {
		throw new UnsupportedOperationException();
	}
}
