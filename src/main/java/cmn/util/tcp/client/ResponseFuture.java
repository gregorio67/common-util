import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResponseFuture {

	private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private final int timeout = 3;
    private TcpMessage response;

    /**
     * 
     *<pre>
     * 1.Description: Return message to calling method
     * 2.Biz Logic:
     * 3.Author : LGCNS
     *</pre>
     * @return
     */
    public TcpMessage get() {
    	TcpMessage result = null;
        try {
            lock.lock();

            if (response != null) {
                result = response;
            } else {
                done.await(timeout, TimeUnit.SECONDS);
                result = response;
            }

        } 
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        } 
        finally {
            lock.unlock();
        }

        return result;
    }

    /**
     * 
     *<pre>
     * 1.Description: The message is set from FutureHoder when Future Holder received response from server
     * 2.Biz Logic:
     * 3.Author : LGCNS
     *</pre>
     * @param data
     */
    public void receive(TcpMessage data) {
        try {
            lock.lock();
            response = data;
            done.signal();
        } 
        finally {
            lock.unlock();
        }
    }
}
