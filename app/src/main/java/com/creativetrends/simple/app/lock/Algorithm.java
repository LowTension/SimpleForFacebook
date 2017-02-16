package com.creativetrends.simple.app.lock;

/**Created by Creative Trends Apps on 4/15/16.*/
public enum Algorithm {

    SHA1("1"), SHA256("2");

    private String mValue;

    Algorithm(String value) {
        this.mValue = value;
    }

    public static Algorithm getFromText(String text) {
        for (Algorithm algorithm : Algorithm.values()) {
            if (algorithm.mValue.equals(text)) {
                return algorithm;
            }
        }
        return SHA1;
    }

    public String getValue() {
        return mValue;
    }
}
