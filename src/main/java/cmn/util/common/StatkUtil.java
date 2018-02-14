package cmn.util.common;

import java.util.Iterator;
import java.util.Stack;


public class StackUtil<E> {

	private Stack<E> stack = null;

	public StackUtil() {
		stack = new Stack<E>();

	}

	/**
	 *
	 * <pre>
	 *
	 * </pre>
	 * @param data
	 * @throws Exception
	 */
	public void pushData(E data) throws Exception {
		stack.push(data);
	}

	/**
	 *
	 * <pre>
	 * It is used to retrieves and removes the head of this stack.
	 * </pre>
	 * @return
	 * @throws Exception
	 */
	public E popData() throws Exception {

		return stack.pop();
	}


	/**
	 *
	 * <pre>
	 * Return size of stack
	 * </pre>
	 * @return int
	 * @throws Exception
	 */
	public int size() throws Exception {
		return stack.size();
	}

	/**
	 *
	 * <pre>
	 * Retrieve all data from Stack
	 * </pre>
	 * @return Iterator<E>
	 * @throws Exception
	 */
	public Iterator<E> iterator() throws Exception {
		return stack.iterator();
	}

	public static void main(String[] args) throws Exception {

		StackUtil<String> stackUtil = new StackUtil<String>();
		stackUtil.pushData("1");
		stackUtil.pushData("2");

		Iterator<String> itr = stackUtil.iterator();
		System.out.println("Iterator");
		while(itr.hasNext()) {
			System.out.println(itr.next());
		}
		System.out.println("Stack Size :: " + stackUtil.size());
		System.out.println(stackUtil.popData());
		System.out.println("Stack Size :: " + stackUtil.size());

		System.out.println(stackUtil.popData());
		System.out.println("Stack Size :: " + stackUtil.size());

	}
}

