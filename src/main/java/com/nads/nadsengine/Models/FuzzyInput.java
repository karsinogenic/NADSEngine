package com.nads.nadsengine.Models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuzzyInput {

    private String value1;

    private List<String> value2;

}
