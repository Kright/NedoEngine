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
    public int type();

    /**
     * @return size of tree. Necessary if we drawing it;
     */
    public int size();

    public static interface Factory{

        public iTree makeTree(int x, int y);
    }
}
