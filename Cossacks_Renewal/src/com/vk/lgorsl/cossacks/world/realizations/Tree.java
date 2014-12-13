package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.interfaces.iTree;

/**
 * Simple realization of tree
 *
 * теортечески, можно будет в будущем сделать так, чтобы деревья со временем росли, раскидывали семена и в итоге умирали
 *
 * Created by lgor on 13.12.2014.
 */
public class Tree extends MapObject implements iTree {

    protected final int type;
    protected final int size;

    public Tree(int x, int y, int type, int size){
        this.x = x;
        this.y = y;
        this.id = -1;
        this.type = type;
        this.size = size;
    }

    @Override
    public int type() {
        return type;
    }

    @Override
    public int size() {
        return size;
    }

    public static class Factory implements iTree.Factory{

        final int type;

        public Factory(int type) {
            this.type = type;
        }

        @Override
        public iTree makeTree(int x, int y) {
            return new Tree(x, y, type, 100);
        }
    }
}
