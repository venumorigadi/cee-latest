package app2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author user
 */
public class MessageRecievingThread implements Iterable {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("A", "Apple");
        map.put("B", "Ball");
        map.put("C", "Cat");
        for(Map.Entry<String, String> e : map.entrySet()) {
            System.out.println(e.getKey()+" for "+e.getValue());
        }
        Set<String> keys = map.keySet();
        for(String key : keys) {
            System.out.println(key+" for "+map.get(key));
        }
        
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
