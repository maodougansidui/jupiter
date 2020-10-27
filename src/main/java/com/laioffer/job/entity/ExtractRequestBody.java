package com.laioffer.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class ExtractRequestBody {
    public List<String> data;

    @JsonProperty("max_keyword")
    public int maxKeyWords;

    public ExtractRequestBody(List<String> data, int maxKeyWords){
        this.data=data;
        this.maxKeyWords=maxKeyWords;
    }
}
