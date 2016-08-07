package com.credr.inspsheettesting.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vijayagnihotri on 24/06/16.
 */
public class PartsObject implements Serializable{
    public PartsObject() {

    }
    public String version;
    public ArrayList<PartStats> parts;

}
