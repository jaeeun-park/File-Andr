package com.example.filechooser;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void arrayListTest() {
        int size = 5;
        boolean[] test = new boolean[size];
        System.out.println(test[3]);
    }
}