package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.cossacks.world.interfaces.Iter;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iMap;
import com.vk.lgorsl.cossacks.world.interfaces.iMapObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * map which divides space to quads and creates own little list of objects for every part of map
 *
 * Created by lgor on 07.01.2015.
 */
public class QuadsMap<T extends iMapObject> implements iMap<T> {

    private final ArrayList<ArrayList<T>> quads;
    private final int width, heigth, quadSize;
    private final int dx, dy;
    private final iRectangle2i mapSize;

    public QuadsMap(WorldMetrics metrics, int quadSize) {
        this.quadSize = quadSize;
        this.mapSize = metrics.mapSize();
        width = mapSize.width() / quadSize + 1;
        heigth = mapSize.height() / quadSize + 1;
        dx = -mapSize.xMin();
        dy = -mapSize.yMin();

        quads = new ArrayList<>();
        for(int i=0; i<heigth*width; i++){
            quads.add(new ArrayList<>());
        }
    }

    @Override
    public void add(T obj) {
        if (mapSize.contains(obj)) {
            getQuad(obj).add(obj);
        }
    }

    private ArrayList<T> getQuad(T obj) {
        int x = (obj.x() + dx) / quadSize;
        int y = (obj.y() + dy) / quadSize;
        return quads.get(x + y * width);
    }

    @Override
    public boolean remove(T obj) {
        return getQuad(obj).remove(obj);
    }

    @Override
    public iRectangle2i bounds() {
        return mapSize;
    }

    @Override
    public void update(int ticks) {
        for (ArrayList<T> lst : quads) {
            Iterator<T> iter = lst.iterator();
            while (iter.hasNext()) {
                T obj = iter.next();
                if (!obj.alive()) {
                    iter.remove();
                    continue;
                }
                ArrayList<T> lst2 = getQuad(obj);
                if (lst != lst2) {
                    iter.remove();
                    lst2.add(obj);
                }
            }
        }
    }

    @Override
    public Iterable<T> objects(final iRectangle2i area) {
        final int left = Math.max((area.xMin() + dx) / quadSize, 0);
        final int right = Math.min((area.xMax() + dx) / quadSize, width-1);
        final int yMin = Math.max((area.yMin() + dy) / quadSize, 0);
        final int yMax = Math.min((area.yMax() + dy) / quadSize, heigth -1);

        return new Iter<T>() {

            int posX = left;
            int posY = yMin;
            private Iterator<T> iterator = quads.get(posX + posY * width).iterator();

            T next;
            boolean old = true;

            T findNext() {
                for(;;) {
                    while (iterator.hasNext()) {
                        T obj = iterator.next();
                        if (area.contains(obj)) {
                            return obj;
                        }
                    }
                    iterator = nextIterator();
                    if (iterator == null){
                        return null;
                    }
                }
            }

            private Iterator<T> nextIterator(){
                if (posX < right) {
                    posX++;
                    return quads.get(posX + posY * width).iterator();
                } else {
                    if (posY < yMax) {
                        posY++;
                        posX = left;
                        return quads.get(posX + posY * width).iterator();
                    } else {     //x==right && y==yMax
                        return null;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                if (old) {
                    next = findNext();
                    old = false;
                }
                return next != null;
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
        final int left = Math.max((area.xMin() + dx) / quadSize, 0);
        final int right = Math.min((area.xMax() + dx) / quadSize, width-1);
        final int yMin = Math.max((area.yMin() + dy) / quadSize, 0);
        final int yMax = Math.min((area.yMax() + dy) / quadSize, heigth -1);

        for (int i = left; i <= right; i++) {
            for (int j = yMin; j <= yMax; j++) {
                for(T obj : quads.get(i + j*width)){
                    if (area.contains(obj)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
