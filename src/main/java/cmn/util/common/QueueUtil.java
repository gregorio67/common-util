package cmn.util.common;

import java.util.Iterator;
import java.util.PriorityQueue;


public class QueueUtil<E> {

	private PriorityQueue<E> queue = null;

	public QueueUtil() {
		queue = new PriorityQueue<E>();

	}
	/**
	 *
	 * <pre>
	 * It is used to insert the specified element into this queue and return true upon success.
	 * </pre>
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public boolean addData(E data) throws Exception {
		return queue.add(data);
	}

	/**
	 *
	 * <pre>
	 * It is used to retrieves and removes the head of this queue, or returns null if this queue is empty.
	 * </pre>
	 * @return
	 * @throws Exception
	 */
	public E pollData() throws Exception {

		return queue.poll();
	}

	/**
	 *
	 * <pre>
	 * It is used to retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
	 * </pre>
	 * @return
	 * @throws Exception
	 */
	public E queryData() throws Exception {
		return queue.peek();
	}

	public int size() throws Exception {
		return queue.size();
	}


	public Iterator<E> iterator() throws Exception {
		return queue.iterator();
	}


	public static void main(String[] args) throws Exception {

		QueueUtil<String> queueUtil = new QueueUtil<String>();
		queueUtil.addData("1");
		queueUtil.addData("2");

		Iterator<String> itr = queueUtil.iterator();
		System.out.println("Iterator");
		while(itr.hasNext()) {
			System.out.println(itr.next());
		}
		System.out.println("Queue Size :: " + queueUtil.size());
		System.out.println(queueUtil.queryData());
		System.out.println("Queue Size :: " + queueUtil.size());
		System.out.println(queueUtil.pollData());
		System.out.println("Queue Size :: " + queueUtil.size());
		System.out.println(queueUtil.pollData());
		System.out.println("Queue Size :: " + queueUtil.size());


	}
}

