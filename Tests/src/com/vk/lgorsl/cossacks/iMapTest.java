package com.vk.lgorsl.cossacks;

import com.vk.lgorsl.NedoEngine.math.Rectangle2i;
import com.vk.lgorsl.NedoEngine.math.iRectangle2i;
import com.vk.lgorsl.cossacks.world.interfaces.WorldMetrics;
import com.vk.lgorsl.cossacks.world.interfaces.iMap;
import com.vk.lgorsl.cossacks.world.interfaces.iMapObject;
import com.vk.lgorsl.cossacks.world.realizations.NaiveMap;
import com.vk.lgorsl.cossacks.world.realizations.QuadsMap;
import junit.framework.TestCase;

import java.util.*;

/**
 * tests for iMap realizations;
 *
 * Created by lgor on 12.01.2015.
 */
public class iMapTest extends TestCase{

    public List<iMapObject> fill(iRectangle2i bounds, int count){
        Random rnd = new Random();
        List<iMapObject> result = new ArrayList<>();
        for(int i =0; i< count ; i++){
            iMapObject t = new iMapObject() {
                int id = count;
                int x = rnd.nextInt(bounds.width()+1) + bounds.xMin();
                int y = rnd.nextInt(bounds.height()+1) + bounds.yMin();

                @Override
                public int id() {
                    return id;
                }

                @Override
                public int getCountryId() {
                    return 0;
                }

                @Override
                public boolean alive() {
                    return true;
                }

                @Override
                public int x() {
                    return x;
                }

                @Override
                public int y() {
                    return y;
                }
            };
            result.add(t);
        }
        return result;
    }

    public void equals(iRectangle2i area, iMap<iMapObject> map1, iMap<iMapObject> map2){
        HashSet<iMapObject> set = new HashSet<>(4096);
        int count = 0;
        for(iMapObject obj: map1.objects(area)){
            set.add(obj);
            count++;
        }
        int count2 = 0;
        for(iMapObject obj: map2.objects(area)){
            assertTrue(set.contains(obj));
            count2++;
        }
        assertEquals(count, count2);
        assertEquals(map1.containsObjects(area), map2.containsObjects(area));
    }

    public void testNaiveMapvsQuadsMap() {
        final int objectsCount = 1024;
        final int timesTested = 10;

        WorldMetrics metrics = new WorldMetrics(2048 << 5, 2048 << 5, 30<<5, 5);
        iMap<iMapObject> map1 = new NaiveMap<>(metrics);
        iMap<iMapObject> map2 = new QuadsMap<>(metrics, 64<<5);
        List<iMapObject> lst = fill(map1.bounds(), objectsCount);

        for(iMapObject obj: lst){
            map1.add(obj);
            map2.add(obj);
        }

        equals(map1.bounds(), map1, map2);

        Random rnd =new Random(12345);
        for(int i=0; i<timesTested; i++){
            int x1 = rnd.nextInt(map1.bounds().width())+map1.bounds().xMin();
            int x2 = rnd.nextInt(map1.bounds().width())+map1.bounds().xMin();
            int y1 = rnd.nextInt(map1.bounds().height()) + map1.bounds().yMin();
            int y2 = rnd.nextInt(map1.bounds().height()) + map1.bounds().yMin();
            equals(new Rectangle2i(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)), map1, map2);
        }
    }
}
