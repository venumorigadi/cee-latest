package app2;

import org.apache.commons.fileupload.ProgressListener;

/**
 *
 * @author user
 */
public class UploadProgressListener implements ProgressListener {

    private long total = -1l;
    private long read = -1l;
    
    /**
     *
     * @param read
     * @param total
     * @param i
     */
    public void update(long read, long total, int i) {
        this.read = read;
        this.total = total;
    }

    /**
     *
     * @return
     */
    public int getPercent() {
        return (int) (100 * (read / total));
    }
    
}
