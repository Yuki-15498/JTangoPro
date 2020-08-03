package com.jtangopro;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 词条属性类
 * 使用字典保存各个属性
 */

public class Tango implements Serializable {
    private Map<String, String> m = new HashMap<>();

    public Tango(Map<String, String> m_){
        Iterator it = m_.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            m.put(key,m_.get(key));
        }
    }

    public void set(String key, String val){
        m.put(key,val);
    }

    public String get(String key){
        return m.get(key);
    }
}
