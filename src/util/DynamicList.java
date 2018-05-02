package util;

import java.util.ArrayList;

/**
 * Extended list can be extended if there is no element by several passing index
 * Use it when dont want to extend any other list by yourself
 * @see ArrayList
 * @param <E>
 */
public class DynamicList<E> extends ArrayList<E> {

    @Override
    public void add(int index, E element) {
        for (; super.size() < index; ) {
            super.add(null);
        }
        super.add(index, element);
    }

    @Override
    public E set(int index, E element) {
        for (; super.size() <= index; ) {
            super.add(null);
        }
        return super.set(index, element);
    }
}
