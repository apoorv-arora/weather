package sanshinkan.org.warrior.data;

import java.io.Serializable;

/**
 * Created by apoorvarora on 11/02/19.
 */

public class NameValuePair implements Serializable {
    private String key;
    private Object value;

    public NameValuePair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public NameValuePair(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}