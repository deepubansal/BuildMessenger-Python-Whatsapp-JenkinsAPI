package messenger.util;

import java.util.ArrayList;
import java.util.Collection;

public class LimitedArrayList<E> extends ArrayList<E> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    int limitSize;
    public LimitedArrayList(Collection<E> collection, int limitSize) {
        super(collection);
        this.limitSize = limitSize;
    }
    @Override
    public boolean add(E e) {
        while (this.size()+1 > limitSize)
          this.remove(0);
        return super.add(e);
        
    }
//    @Override
//    public void add<E>(int index, E element) {
//        super.add(index, element);
//        
//    }
}
