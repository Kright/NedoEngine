package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.cossacks.world.interfaces.Iter;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iMap;
import com.vk.lgorsl.cossacks.world.interfaces.iMapObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Naive map realization
 * эталон того, как должно работать
 * В перспективе, сделаю разделение карты на квадраты.
 *
 * Created by lgor on 13.12.2014.
 */
public class NaiveMap<T extends iMapObject> implements iMap<T> {

    private final iRectangle2i bounds;
    private List<T> lst;

    public NaiveMap(WorldMetrics metrics){
        this.bounds = metrics.mapSize();
        lst = new ArrayList<>();
    }

    @Override
    public void add(T obj) {
        lst.add(obj);
    }

    @Override
    public boolean remove(T obj) {
        return lst.remove(obj);
    }

    @Override
    public iRectangle2i bounds() {
        return bounds;
    }

    @Override
    public void update(int ticks) {
        Iterator<T> iterator = lst.iterator();
        while (iterator.hasNext()){
            if (!iterator.next().alive()){
                iterator.remove();
            }
        }
    }

    @Override
    public Iterable<T> objects(final iRectangle2i area) {
        final Iterator<T> iterator = lst.iterator();
        return new Iter<T>() {
            T next;
            boolean old = true;

            T findNext(){
                while (iterator.hasNext()){
                    T obj = iterator.next();
                    if (area.contains(obj)){
                        return obj;
                    }
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                if (old){
                    next = findNext();
                    old = false;
                }
                return next!=null;
            }

            @Override
            public T next() {
                old = true;
                return next;
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    @Override
    public boolean containsObjects(iRectangle2i area) {
        for(T obj: lst){
            if (area.contains(obj) && obj.alive()){
                return true;
            }
        }
        return false;
    }
}
