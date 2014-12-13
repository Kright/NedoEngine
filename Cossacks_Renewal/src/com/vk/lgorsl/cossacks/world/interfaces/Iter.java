package com.vk.lgorsl.cossacks.world.interfaces;

import java.util.Iterator;

/**
 * abstract class, who realize iterator and iterable simultaneously.
 * method iterator() returns itself;
 *
 * Created by lgor on 13.12.2014.
 */
public abstract class Iter<T> implements Iterator<T>, Iterable<T> {
    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public abstract boolean hasNext();

    @Override
    public abstract T next();

    @Override
    public abstract void remove();
}
