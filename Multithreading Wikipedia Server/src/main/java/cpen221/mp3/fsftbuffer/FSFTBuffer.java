package cpen221.mp3.fsftbuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class FSFTBuffer<T extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    public static final int SECONDS_TO_MILLISECONDS = 1000;

    private int capacity;
    private int timeout;
    private List<T> items = Collections.synchronizedList(new ArrayList<>());
    //startTimes represents start times of when each object has been most recently used
    private List<Long> startTimes = Collections.synchronizedList(new ArrayList<>());
    //lastUsed represents last time an object was used
    private List<Long> lastUsed = Collections.synchronizedList(new ArrayList<>());
//    private final ArrayList<Long> lastUsed = new ArrayList<Long>();

    /*
    Representation Invariant:
    capacity > 0
    timeout > 0
     */

    /*
    Abstraction Function:
    Represents a buffer with capacity = capacity and removes elements inside it after timeout seconds.
     */

    /*
    Thread Safety Argument:
        - all parts of code that add and remove elements of the buffer are synchronized
        - buffer implements Thread-safe list data types
     */

    /**
     * Create a buffer with a fixed capacity and a timeout value.
     * Objects in the buffer that have not been refreshed within the
     * timeout period are removed from the cache.
     *
     * @param capacity the number of objects the buffer can hold
     * @param timeout  the duration, in seconds, an object should
     *                 be in the buffer before it times out
     */
    public FSFTBuffer(int capacity, int timeout) {
        this.capacity = capacity;
        this.timeout = timeout;
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DSIZE, DTIMEOUT);
    }

    private void removeTimeouts() {
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
        //Check if any values have timed out
        synchronized(this) {
            for(int i = 0; i < items.size(); i++) {
                long timeSinceUsed = curTime - lastUsed.get(i);
                if(timeSinceUsed >= timeout) {
                    //Remove all timed-out Objects
                    items.remove(i);
                    startTimes.remove(i);
                    lastUsed.remove(i);
                    //Decrement i, since an element was removed
                    i--;
                }
            }
        }
//        for(int i = 0; i < items.size(); i++) {
//            long timeSinceUsed = curTime - lastUsed.get(i);
//            if(timeSinceUsed >= timeout) {
//                //Remove all timed-out Objects
//                items.remove(i);
//                startTimes.remove(i);
//                lastUsed.remove(i);
//                //Decrement i, since an element was removed
//                i--;
//            }
//        }
    }

    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     * @param t Object to add to the buffer. t must implement Bufferable
     */
    public boolean put(T t) {
        removeTimeouts();
        boolean status = false;
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;

        synchronized(this) {
            //If buffer not full, add value
            if(items.size() < capacity) {
                items.add(t);
                startTimes.add(curTime);
                lastUsed.add(curTime);
                status = true;
            }
            else {
                long minTime = lastUsed.get(0);
                int index = 0;
                for(int i = 0; i < items.size(); i++) {
                    if(lastUsed.get(i) < minTime) {
                        index = i;
                        minTime = lastUsed.get(i);
                    }
                }
                items.remove(index);
                lastUsed.remove(index);
                startTimes.remove(index);

                items.add(t);
                lastUsed.add(curTime);
                startTimes.add(curTime);

                status = true;
            }
        }
//        //If buffer not full, add value
//        if(items.size() < capacity) {
//            items.add(t);
//            startTimes.add(curTime);
//            lastUsed.add(curTime);
//            status = true;
//        }
//        else {
//            long minTime = lastUsed.get(0);
//            int index = 0;
//            for(int i = 0; i < items.size(); i++) {
//                if(lastUsed.get(i) < minTime) {
//                    index = i;
//                    minTime = lastUsed.get(i);
//                }
//            }
//            items.remove(index);
//            lastUsed.remove(index);
//            startTimes.remove(index);
//
//            items.add(t);
//            lastUsed.add(curTime);
//            startTimes.add(curTime);
//
//            status = true;
//        }
        return status;
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public T get(String id) throws NoSuchElementException {
        removeTimeouts();

        synchronized(this) {
            for(T item: items) {
                if(item.id().equals(id)) {
                    return item;
                }
            }
        }
//        for(T item: items) {
//            if(item.id().equals(id)) {
//                return item;
//            }
//        }
        throw new NoSuchElementException();
    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its
     * timeout is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    public boolean touch(String id) {
        removeTimeouts();
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
        int index = -1;

        synchronized(this) {
            for(int i = 0; i < items.size(); i++) {
                if(id.equals(items.get(i).id())) {
                    index = i;
                    lastUsed.set(index, curTime);
                }
            }
        }
//        for(int i = 0; i < items.size(); i++) {
//            if(id.equals(items.get(i).id())) {
//                index = i;
//                lastUsed.set(index, curTime);
//            }
//        }

        return index != -1;
    }

    /**
     * Update an object in the buffer.
     * This method updates an object and acts like a "touch" to
     * renew the object in the cache.
     *
     * @param t the object to update
     * @return true if successful and false otherwise
     */
    public boolean update(T t) {
        removeTimeouts();
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
        int index = -1;

        synchronized(this) {
            for(int i = 0; i < items.size(); i++) {
                if(t.equals(items.get(i))) {
                    index = i;
                    lastUsed.set(index, curTime);
                }
            }
        }
//        for(int i = 0; i < items.size(); i++) {
//            if(t.equals(items.get(i))) {
//                index = i;
//                lastUsed.set(index, curTime);
//            }
//        }

        return index != -1;
    }
}
