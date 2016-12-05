package paging;

import java.util.HashMap;

/**
 * Created by sarahaly on 11/29/16.
 */
public class Process {
    protected int numFaults;
    protected int numEvictions;
    protected int waitTime;
    protected int num;
    protected int loadTime;
    protected int residencyTime;
    protected double A, B, C;
    protected int w;
    protected int refsLeft;

    public Process(int num, int refs){
        this.num = num;
        numFaults = 0;
        numEvictions = 0;
        loadTime = 0;
        waitTime = 0;
        residencyTime = 0;
        w = 0;
        refsLeft = refs;
    }

    public Process(int num, double A, double B, double C, int refs){
        this.num = num;
        numFaults = 0;
        numEvictions = 0;
        loadTime = 0;
        waitTime = 0;
        residencyTime = 0;
        w = 0;
        this.A = A;
        this.B = B;
        this.C = C;
        this.refsLeft = refs;
    }
}
