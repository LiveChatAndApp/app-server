package cn.wildfirechat.app.tools;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {
    public static String shortUUID(int size) {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, size);
    }
}
