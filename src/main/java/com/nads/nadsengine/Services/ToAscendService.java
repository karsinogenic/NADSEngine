package com.nads.nadsengine.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.nads.nadsengine.Models.SetupRule;
import com.nads.nadsengine.Repositories.RulesRepository;
import com.nads.nadsengine.Repositories.SetupRuleRepository;

public class ToAscendService {

    public void saveToAscendMongo(Map to_db_data, List<String> triggered_code,
            SetupRuleRepository setupRuleRepository, MongoTemplate mongoTemplate) {
        boolean hasil = false;
        for (String string : triggered_code) {
            Optional<SetupRule> isi = setupRuleRepository.findByKode(string);
            if (isi.get().getDatabaseName().toUpperCase().equals("ASCEND")) {
                hasil = true;
                break;
            }
        }
        if (hasil) {
            mongoTemplate.insert(to_db_data, "nadsAscend");
        }
        // return hasil;
    }

}
