package taxris.framework.schedule;

import java.lang.reflect.InvocationTargetException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;

import taxris.framework.util.ApplicationContextProvider;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution

public class MethodInvokingJobBean extends QuartzJobBean {


	@Override
	protected void executeInternal( JobExecutionContext context ) {
		MethodInvoker methodInvoker = new MethodInvoker();

		try {
			String beanName = (String)context.getJobDetail().getJobDataMap().get( "beanName" );
			String methodName = (String)context.getJobDetail().getJobDataMap().get( "methodName" );
			Assert.notNull( beanName );
			Assert.notNull( methodName );

			Object serviceBean = ApplicationContextProvider.getApplicationContext().getBean( beanName );

			methodInvoker.setTargetObject( serviceBean );
			methodInvoker.setTargetMethod( methodName );
			if ( context.getJobDetail().getJobDataMap().getBoolean( "passJobParameter" ) ) {
				/** Set method parameter from input parameter **/ 
				methodInvoker.setArguments( new Object[] { context.getJobDetail().getJobDataMap() } );
			}
			methodInvoker.prepare();

			context.setResult( methodInvoker.invoke() );
		} catch ( InvocationTargetException ex ) {
			if ( ex.getTargetException() instanceof JobExecutionException ) {
				try {
					throw ((JobExecutionException)ex.getTargetException());
				} catch (JobExecutionException e) {
				} // NOPMD - Spring 'MethodInvokingJob' reference
			}
			throw new JobMethodInvocationFailedException( methodInvoker, ex.getTargetException() ); // NOPMD - Spring 'MethodInvokingJob' Reference
		} catch ( Exception ex ) {
			throw new JobMethodInvocationFailedException( methodInvoker, ex );
		}
	}
}
