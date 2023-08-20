package com.nads.nadsengine.Services;

import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Repositories.SetupRuleRepository;

public class TestThread implements Runnable {

    private int num;
    // private String chars;
    private SetupRuleRepository srs;

    public TestThread(int num, SetupRuleRepository srs) {
        this.num = num;
        this.srs = srs;
        // this.chars = chars;
    }

    public void run() {
        System.out.println("Thread " + num + "is running");
        try {
            String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o" };
            String[] chars2 = { "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
            Thread.sleep(10000);
            SetupRule sr = new SetupRule(null, chars[num], chars2[num], num, null, "as", true);
            srs.save(sr);
            System.out.println("Thread " + num + "is finish");

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
