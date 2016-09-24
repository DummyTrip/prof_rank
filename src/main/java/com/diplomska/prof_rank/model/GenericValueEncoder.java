package com.diplomska.prof_rank.model;

import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class GenericValueEncoder<T> implements ValueEncoder<T> {

    private List<T> list;
    private final PropertyAccess access;
    private final String fieldName;

    public GenericValueEncoder(List<T> list, String fieldName, PropertyAccess propertyAccess) {
        this.list = list;
        this.fieldName = fieldName;
        this.access = propertyAccess;
    }

    public String toClient(T obj) {
        if (fieldName == null) {
            return obj + "";
        } else {
            return access.get(obj,fieldName)+"";
        }
    }

    public T toValue(String string) {
        for (T obj : list) {
            if (toClient(obj).equals(string)) return obj;
        }
        return null;
    }
}