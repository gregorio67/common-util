package cmn.util.common;


public class TimeChecker {

    private long curTime;
    private long durationTime;
    private boolean stopFlag = false;

    /**
     *
     */
    public TimeChecker() {
        start();
    }

    /**
     * Timer는 계속되고, 현재까지의 시간을 리턴한다.
     *
     * @return   현재까지의 시간
     */
    public long period(){
        if (stopFlag == true) {
            start();
        }
        return System.currentTimeMillis() - curTime;
    }

    /**
     * Timer를 시작한다.
     *
     */
    public void start(){
        curTime = System.currentTimeMillis();
        stopFlag = false;
    }

    /**
     * Timer를 중지하고, 현재까지의 시간을 리턴한다.
     *
     * @return   현재까지의 시간
     */
    public long stop() {
        if (stopFlag == false) {
            durationTime = System.currentTimeMillis() - curTime;
            stopFlag = true;
        }
        return durationTime;
    }
}
