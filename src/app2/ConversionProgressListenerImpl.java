package app2;

/**
 *
 * @author user
 */
public class ConversionProgressListenerImpl implements ConversionProgressListener {

    private String log = "";
    
    /**
     *
     * @param message
     */
    public void update(String message) {
        log += message;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return log;
    }

    /**
     *
     */
    public void clearMessage() {
        log = "";
    }
}
