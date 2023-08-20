package com.nads.nadsengine.Services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.language.Soundex;

import com.mongodb.BasicDBObject;
import com.nads.nadsengine.Models.NewExtractedResult;

public class SoundexService {

    public List<NewExtractedResult> list_soundex(List<BasicDBObject> list_isi, String ref, String paramDB) {
        List<NewExtractedResult> list_hasil = new ArrayList<>();
        Soundex sdx = new Soundex();
        String sdx_input = (sdx.soundex(ref));
        // System.out.println(sdx_input);
        // System.out.println("size: " + list_isi.size());
        for (int i = 0; i < list_isi.size(); i++) {
            BasicDBObject basicDBObject = list_isi.get(i);
            String sdx_db = sdx.soundex(basicDBObject.getString(paramDB));

            // System.out.println(paramDB + "= " + basicDBObject.getString(paramDB) + " : "
            // + ref);
            // System.out.println("soundex =" + sdx_input + " : " + sdx_db);
            if (sdx_db != null) {
                if (sdx_input.toString().equals(sdx_db.toString())) {
                    NewExtractedResult newExtractedResult = new NewExtractedResult(
                            basicDBObject.get(paramDB).toString(), 0,
                            0, i);
                    list_hasil.add(newExtractedResult);
                }
            }
            // System.out.println("same ? " + basicDBObject.getString(paramDB).equals(ref));
        }
        return list_hasil;
    }

    public List<NewExtractedResult> list_notsoundex(List<BasicDBObject> list_isi, String ref, String paramDB) {
        List<NewExtractedResult> list_hasil = new ArrayList<>();
        Soundex sdx = new Soundex();
        String sdx_input = (sdx.soundex(ref));
        // System.out.println(sdx_input);
        // System.out.println("size: " + list_isi.size());
        for (int i = 0; i < list_isi.size(); i++) {
            BasicDBObject basicDBObject = list_isi.get(i);
            String sdx_db = sdx.soundex(basicDBObject.getString(paramDB));

            // System.out.println(paramDB + "= " + basicDBObject.getString(paramDB) + " : "
            // + ref);
            // System.out.println("soundex =" + sdx_input + " : " + sdx_db);

            // System.out.println("same ? " + basicDBObject.getString(paramDB).equals(ref));
            if (!sdx_input.toString().equals(sdx_db.toString())) {
                NewExtractedResult newExtractedResult = new NewExtractedResult(basicDBObject.get(paramDB).toString(), 0,
                        0, i);
                list_hasil.add(newExtractedResult);
            }
        }
        return list_hasil;
    }

}
