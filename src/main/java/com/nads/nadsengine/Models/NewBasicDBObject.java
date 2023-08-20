package com.nads.nadsengine.Models;

import com.mongodb.BasicDBObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBasicDBObject {

    private int id;
    private BasicDBObject basicDBObject;

}
