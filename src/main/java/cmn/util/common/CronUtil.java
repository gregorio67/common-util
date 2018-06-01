import java.util.Date;

import org.quartz.CronExpression;

public class CronUtil {

	public static Date previousScheduleDate = new Date();
	/**
	 * 
	 *<pre>
	 * 1.Description: Return next date 
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @param cronExpression Quartz cron expression
	 * @return Next date
	 * @throws Exception
	 */
	public static Date getNextDate(String cronExpression) throws Exception {
		return getNextDate(cronExpression, new Date());
	}
	public static Date getNextDate(String cronExpression, Date curDate) throws Exception {
		CronExpression cron = new CronExpression(cronExpression);
		Date nextExecutionDate = cron.getNextValidTimeAfter(curDate);
		return nextExecutionDate;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Return previous date
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @param cronExpression Quartz cron expression
	 * @return Previous date
	 * @throws Exception
	 */
	public static Date getPreviousDate(String cronExpression) throws Exception {
		return getPreviousDate(cronExpression, new Date());
	}
	public static Date getPreviousDate(String cronExpression, Date curDate) throws Exception {

		Date prevExecutionDate = null;
        try {
        	CronExpression cron = new CronExpression(cronExpression);
            
        	Date nextValidTime = cron.getNextValidTimeAfter(curDate);
            
            Date subsequentNextValidTime = cron.getNextValidTimeAfter(nextValidTime);
            
            long interval = subsequentNextValidTime.getTime() - nextValidTime.getTime();
            
            prevExecutionDate =  new Date(nextValidTime.getTime() - interval);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported cron or date", e);
        }
		return prevExecutionDate;
	}
	/**
	 * 
	 *<pre>
	 * 1.Description: Validate cron expression
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @param cronExpression Quartz cron expression
	 * @return result validate cron expression
	 * @throws Exception
	 */
	public static boolean isValidate(String cronExpression) throws Exception {
		return CronExpression.isValidExpression(cronExpression);
	}
	

	/**
	 * 
	 *<pre>
	 * 1.Description:
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @param cronExpression
	 * @return
	 * @throws Exception
	 */
	public static String getCronText(String cronExpression) throws Exception {
		CronExpression cron = new CronExpression(cronExpression);
		return cron.getExpressionSummary();
	}

	public static void main(String[] args) throws Exception {

		String cronExpression = "0 */10 * * * ?";
		String lastDayOfMonth = "0 10 10 L * ?";
//		boolean isOK = isValidate(cronExpression);
//		System.out.println("validate :: " + isOK);

		Date pdate = CronUtil.getPreviousDate(cronExpression);
		Date ndate = CronUtil.getNextDate(cronExpression);

		System.out.println(pdate + " :: " + ndate);
		
		System.out.println("exp ::" + getCronText(lastDayOfMonth));
	}
}
