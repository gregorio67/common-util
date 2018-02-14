

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;


public class SimpleGlobalJobListener implements JobListener {

	private static final Logger LOGGER = LogManager.getLogger(SimpleGlobalJobListener.class);

	/**
	 * Listener 이름을 정의해야 한다
	 */
	private static final String NAME = "simpleGlobalJobListener";

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
	 */
	@Override
	public void jobToBeExecuted( JobExecutionContext context ) {
		// Quartz Job execute Event
		LOGGER.info( "[Job Started] Job name: {}", context.getJobDetail().getKey().getName() );
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
	 */
	@Override
	public void jobExecutionVetoed( JobExecutionContext context ) {
		/** Quartz Job Cancel/deny event (Concurrent Constraints 또는 Trigger fail)  */
		LOGGER.info( "[Job Vetoed] Job name: {}", context.getJobDetail().getKey().getName() );
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
	 */
	@Override
	public void jobWasExecuted( JobExecutionContext context, JobExecutionException jobException ) {
		/** Quartz Job Stop Event (If Exception occurs, the parameter is error object  **/ 
		LOGGER.info( "[Job Executed End] Job name: {}", context.getJobDetail().getKey().getName() );
		if ( jobException != null ) {
			LOGGER.error( String.format( "[Job Executed End] An error occurred while '%s' job processing: ", context.getJobDetail().getKey().getName() ), jobException );
		}
	}
}
