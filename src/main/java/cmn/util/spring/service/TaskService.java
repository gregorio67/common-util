public interface TaskService {

	public boolean checkTaskStatus(String mapperName, String jobId) throws Exception;
	
	public int updateTaskStatus(String mapperName, String jobId, boolean isStarted) throws Exception;
}
