package com.vk.lgorsl.cossacks.world.realizations;

import com.vk.lgorsl.cossacks.world.interfaces.iTree;

import java.util.Random;

/**
 * Simple realization of tree
 *
 * теортечески, можно будет в будущем сделать так, чтобы деревья со временем росли, раскидывали семена и в итоге умирали
 *
 * Created by lgor on 13.12.2014.
 */
public class Tree implements iTree {

    private static final int amountOfWood = 100;

    protected final int type;
    protected final int size;
    private int x, y;
    private boolean alive = true;

    public Tree(int x, int y, int type, int size){
        this.x = x;
        this.y = y;
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

    @Override
    public int cutDown() {
        alive = false;
        return amountOfWood;
    }

    @Override
    public int id() {
        return -1;
    }

    @Override
    public int getCountryId() {
        return 0;
    }

    @Override
    public boolean alive() {
        return alive;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    public static class Factory implements iTree.Factory{

        private final int typesCount;
        private final Random rnd = new Random();

        public Factory(int typesCount) {
            this.typesCount = typesCount;
        }

        @Override
        public iTree makeTree(int x, int y) {
            return new Tree(x, y, rnd.nextInt(typesCount), 100);
        }
    }
}
