import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MonitorInfo;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JvmUtil {

	
	/**
	 * 
	 *<pre>
	 * 1.Description: Get JVM Process ID
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	public static long getProcessId() throws Exception {
		 RuntimeMXBean runtimeBean = java.lang.management.ManagementFactory.getRuntimeMXBean();
		 String jvmName = runtimeBean.getName();
	     long pid = Long.valueOf( jvmName.split( "@" )[0] );
	     return pid;
	}
	

	/**
	 * 
	 *<pre>
	 * 1.Description: Return JVM Name
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	public static String getJvmName() throws Exception {
		 RuntimeMXBean runtimeBean = java.lang.management.ManagementFactory.getRuntimeMXBean();
		 String jvmName = runtimeBean.getName();
	     return jvmName.split( "@" )[1];
	}
	/**
	 * 
	 *<pre>
	 * 1.Description: Get Memory Infomation
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getJvmMemInfo() throws Exception {
		Map<String, Object> jvmMap = new HashMap<String, Object>();
		
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		
		long initMem = mbean.getHeapMemoryUsage().getInit();
		long usedMem = mbean.getHeapMemoryUsage().getUsed();
		long maxMem = mbean.getHeapMemoryUsage().getMax();
		long freeMem = maxMem - usedMem;
		
		jvmMap.put("heapInit", initMem);		
		jvmMap.put("heapUsage", usedMem);
		jvmMap.put("heapMax", maxMem);
		jvmMap.put("heapFree", freeMem);
		
		return jvmMap;
		
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Get thread information
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getThreadInfo() throws Exception {
		Map<String, Object> threadMap = new HashMap<String, Object>();
		
		ThreadMXBean bean = java.lang.management.ManagementFactory.getThreadMXBean();
		
		long totalThread = bean.getTotalStartedThreadCount();
		long curThread = bean.getThreadCount();
		long peakThread = bean.getPeakThreadCount();
		
		threadMap.put("totalThread", totalThread);
		threadMap.put("currentThread", curThread);
		threadMap.put("peakThread", peakThread);
		
		
		return threadMap;
	}
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Return free memory size
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public static double getMenInfo() throws Exception {
		com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		long totalMem = osBean.getTotalPhysicalMemorySize();
		long freeMem = osBean.getFreePhysicalMemorySize();
		double usage = ((double)freeMem / (double)totalMem);
		return usage;
	}
	
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Return CPU average usage
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public static double getCpuInfo() throws Exception {
		com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
//		long totalMem = osBean.getTotalPhysicalMemorySize();
//		long freeMem = osBean.getFreePhysicalMemorySize();		
		double avgCpuUsage = osBean.getSystemCpuLoad();
		
		return avgCpuUsage;
		
	}	
	/**
	 * 
	 *<pre>
	 * 1.Description: Thread Dump
	 * 2.Biz Logic:
	 * 3.Author : 
	 *</pre>
	 * @return
	 * @throws Exception
	 */
	public static List<String> getThredDump() throws Exception {
		
		List<String> threadInfo = new ArrayList<String>();
		ThreadMXBean bean = java.lang.management.ManagementFactory.getThreadMXBean();

		ThreadInfo[] infos = bean.dumpAllThreads(true,true );
		
        for(ThreadInfo info : infos) {
            StringBuilder sb = new StringBuilder();
            sb.append("<H3>");
            sb.append("\"" + info.getThreadName() + "\""
                    + " Id=" + info.getThreadId() + " " + info.getThreadState());
            if (info.getLockName() != null) {
                sb.append(" on " + info.getLockName());
            }
            if (info.getLockOwnerName() != null) {
                sb.append(" owned by \"" + info.getLockOwnerName() + "\" Id="
                        + info.getLockOwnerId());
            }
            if (info.isSuspended()) {
                sb.append(" (suspended)");
            }
            if (info.isInNative()) {
                sb.append(" (in native)");
            }
            sb.append("</H3>");
       
            int i = 0;
            for (; i < info.getStackTrace().length; i++) {
                StackTraceElement ste = info.getStackTrace()[i];
                sb.append("&nbsp&nbsp&nbsp&nbsp&nbsp at " + ste.toString());
                sb.append("<br>");                            
                if (i == 0 && info.getLockInfo() != null) {
                    Thread.State ts = info.getThreadState();
                    switch (ts) {
                    case BLOCKED:
                        sb.append("\t-  blocked on " + info.getLockInfo());
                        sb.append("<br>");                            
                        break;
                    case WAITING:
                        sb.append("\t-  waiting on " + info.getLockInfo());
                        sb.append("<br>");                            
                        break;
                    case TIMED_WAITING:
                        sb.append("\t-  timed waiting on " + info.getLockInfo());
                        sb.append("<br>");                            
                        break;
                    default:
                    }
                }

                for (MonitorInfo mi : info.getLockedMonitors()) {
                    if (mi.getLockedStackDepth() == i) {
                        sb.append("\t-  locked " + mi);
                        sb.append("<br>");                            
                    }
                }
            }
            if (i < info.getStackTrace().length) {
                sb.append("\t...");
                sb.append("<br>");                            
            }

            LockInfo[] locks = info.getLockedSynchronizers();
            if (locks.length > 0) {
                sb.append("\n\tNumber of locked synchronizers = " + locks.length);
                sb.append("<br>");                            
                for (LockInfo li : locks) {
                    sb.append("\t- " + li);
                    sb.append("<br>");                            
                }
            }
            sb.append("occured blocked " + info.getBlockedCount() + " times");
            sb.append("and blocked time(nanosecond) is " + info.getBlockedTime());
            threadInfo.add(sb.toString());
        }
        return threadInfo;
	}
	
	/**
	 * 
	 *<pre>
	 * 1.Description: Convert byte to megabyte
	 * 2.Biz Logic:
	 * 3.Author : LGCNS
	 *</pre>
	 * @param bytes
	 * @return
	 */
    public static String formatBytes(long bytes) {
        long mb = bytes;
        if (bytes > 0) {
            mb = bytes / 1024 / 1024;
        }
        return mb + "M";
    }
