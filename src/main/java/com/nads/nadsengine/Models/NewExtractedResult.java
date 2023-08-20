package com.nads.nadsengine.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewExtractedResult {

    private String string;
    private int token_score;
    private int score;
    private int index;
    // private String and_or;

}
