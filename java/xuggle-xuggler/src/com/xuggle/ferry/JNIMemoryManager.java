package com.xuggle.ferry;

import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal Only.
 * <p>
 * This class is used by the JNI Memory system as the root for our
 * own garbage collector.
 * </p>
 * @author aclarke
 *
 */
public class JNIMemoryManager
{
  private static final Logger log = LoggerFactory.getLogger(JNIMemoryManager.class);

  private ReferenceQueue<Object> mRefQueue;
  private Set<JNIWeakReference> mRefList;

  /**
   * The constructor is package level so others can't create it.
   */
  JNIMemoryManager()
  {
    mRefQueue = new ReferenceQueue<Object>();
    mRefList = new HashSet<JNIWeakReference>();
  }
  
  /**
   * Get the underlying queue we track references with.
   * @return The queue.
   */
  ReferenceQueue<Object> getQueue()
  {
    return mRefQueue;
  }
  
  /**
   * Get the number of Ferry's objects we believe are still pinned
   * (i.e. in use).
   * @return number of ferry objects in use.
   */
  public long getNumPinnedObjects()
  {
    long result;
    synchronized(mRefList)
    {
      result = mRefList.size();
    }
    return result;
  }
  
  /**
   * A finalizer for the memory manager itself.  It just calls
   * internal garbage collections and then exists.
   * 
   * This may end up "leaking" some memory if all Ferry objects
   * have not otherwise beened collected, but this is not
   * a huge problem for most application.
   */
  public void finalize()
  {
    log.debug("destroying: {}", this);
    gc();
    mRefList = null;
    gc();
  }
  
  /**
   * Add a reference to the set of references we'll collect.
   * @param ref The reference to collect.
   * @return true if already in list; false otherwise.
   */
  public boolean addReference(JNIWeakReference ref)
  {
    boolean result = false;
    synchronized(mRefList)
    {
      result = mRefList.add(ref);
    }
    return result;
  }
  
  /**
   * Remove this reference from the set of references we're tracking,
   * and collect it.
   * @param ref The reference to remove
   * @return true if the reference was in the queue; false if not.
   */
  public boolean removeReference(JNIWeakReference ref)
  {
    boolean result = false;
    synchronized(mRefList)
    {
      result = mRefList.remove(ref);
    }
    return result;    
  }
  
  /**
   * Do a Xuggle Ferry Garbage Collection.
   * 
   * This takes all Ferry objects that are no longer reachable and
   * deletes the underlying native memory.  It is called every
   * time you allocate a new Ferry object to ensure we're freeing
   * up native objects as soon as possible (rather than waiting for
   * the potentially slow finalizer).  It is also called via a finalizer
   * on an object that is referenced by the Ferry'ed object (that way,
   * the earlier of the next Ferry allocation, or the finalizer thread,
   * frees up unused native memory).
   */
  public void gc()
  {
    JNIWeakReference ref = null;
    while((ref = (JNIWeakReference)mRefQueue.poll())!=null)
    {
      ref.delete();
    }
  }

}