package com.example.zh2t.BasicResponce;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor @NoArgsConstructor
@Setter @Getter
public class BasicMessageResponse {
    private Map<String, Object> data = new HashMap<>();
    int status;

    public void addField(String key, Object value) {
        data.put(key, value);
    }

    public BasicMessageResponse(String key, Object value, int status){
        data.put(key, value);
        this.status = status;
    }
}

