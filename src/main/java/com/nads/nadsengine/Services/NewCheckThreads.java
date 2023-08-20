package com.nads.nadsengine.Services;

import java.util.List;

import org.apache.tomcat.util.digester.Rules;

import com.nads.nadsengine.Models.NewRule;
import com.nads.nadsengine.Repositories.RulesRepository;

public class NewCheckThreads implements Runnable {

    private RulesRepository rulesRepository;
    private volatile boolean running = true;
    private String kode;

    public NewCheckThreads(RulesRepository rulesRepository, String kode) {
        this.rulesRepository = rulesRepository;
        this.kode = kode;
    }

    @Override
    public void run() {
        while (running) {
            List<NewRule> list_rule = this.rulesRepository.findByKode(kode);
        }
    }

    public void stop() {
        running = false;
    }

}
