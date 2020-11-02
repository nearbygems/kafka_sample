package kz.kafka.sample.util;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Base64;

public class Ids {

  private final static ThreadLocal<SecureRandom> rnd = ThreadLocal.withInitial(SecureRandom::new);

  public static @NotNull ObjectId generate() {
    byte[] bytes = new byte[12];
    rnd.get().nextBytes(bytes);
    return new ObjectId(bytes);
  }

  public static @NotNull String generateStr() {
    return toStrId(generate());
  }

  private static final char C1 = '~';
  private static final char C2 = '@';

  private static @NotNull String base64_to_strId(String base64) {
    return base64.replace('/', C1).replace('+', C2);
  }

  private static @NotNull String strId_to_base64(@NotNull String strId) {
    return strId.replace(C1, '/').replace(C2, '+');
  }

  public static @NotNull String toStrId(ObjectId objectId) {
    return base64_to_strId(Base64.getEncoder().encodeToString(objectId.toByteArray()));
  }

  public static @NotNull ObjectId toObjectId(@NotNull String strId) {
    try {
      String base64str = strId_to_base64(strId);
      byte[] bytes = Base64.getDecoder().decode(base64str);
      return new ObjectId(bytes);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("ky9ace5ud7 :: strId = `" + strId + '`');
    }
  }

}
