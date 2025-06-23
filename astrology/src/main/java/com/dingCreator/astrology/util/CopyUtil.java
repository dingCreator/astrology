package com.dingCreator.astrology.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Slf4j
public class CopyUtil {

    @SuppressWarnings("unchecked")
    public static <T> T copyNewInstance(T t) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();//字节数组容器
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            //反序列化
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (T) ois.readObject();
        } catch (Exception e) {
            log.error("deep copy throws exception", e);
            throw new RuntimeException(e);
        }
    }
}
