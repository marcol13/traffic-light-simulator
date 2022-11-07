package com.put.urbantraffic.util;

import java.io.*;

public class ObjectSerializationHelper {
    public static void saveObject(Serializable object, String filename) {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename))) {
            stream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object readObject(String filename) {
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filename))) {
            return stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
