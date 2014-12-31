package com.vk.lgorsl.cossacks.world.interfaces;

/**
 * дерево.
 *
 * Created by lgor on 13.12.2014.
 */
public interface iTree extends iMapObject{

    /**
     * @return type of tree. Necessary if we drawing it;
     */
    int type();

    /**
     * @return size of tree. Necessary if we drawing it;
     */
    int size();

    /**
     * @return amount of wood
     */
    int cutDown();

    public static interface Factory{

        public iTree makeTree(int x, int y);
    }
}
