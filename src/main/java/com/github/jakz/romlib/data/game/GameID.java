package com.github.jakz.romlib.data.game;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.pixbits.lib.lang.StringUtils;

public interface GameID<T>
{
  public static class Numeric implements GameID<Integer>
  {
    public final int value;
    public Numeric(int value) { this.value = value; }
    public boolean equals(Object o) { return o instanceof Numeric && ((Numeric)o).value == value; }
    public int hashCode() { return Integer.hashCode(value); }
  }
  
  public static class Textual implements GameID<String>
  {
    public final String value;
    public Textual(String value) { this.value = value; }
  }
  
  public static class SizeAndCRC implements GameID<Long>
  {
    public final long value;
    
    public SizeAndCRC(long value) { this.value = value; }
    public SizeAndCRC(long size, long crc) { this.value = crc ^ size; }
    
    @Override public String toString() { return "GameID.SizeAndCRC("+Long.toHexString(value)+")"; }
    @Override public boolean equals(Object o) { return o instanceof SizeAndCRC && ((SizeAndCRC)o).value == value; }
    @Override public int hashCode() { return Long.hashCode(value); }
  }
  
  public static class CRC implements GameID<Long>
  {
    public final long value;
    
    public CRC(long crc) { this.value = crc; }
    
    @Override public String toString() { return "GameID.CRC("+Long.toHexString(value)+")"; }
    @Override public boolean equals(Object o) { return o instanceof CRC && ((CRC)o).value == value; }
    @Override public int hashCode() { return Long.hashCode(value); }
  }
  
  public static class RomHash implements GameID<byte[]>
  {
    public final byte[] value;
    
    public RomHash(Rom.Hash hash)
    {
      value = Arrays.copyOf(hash.md5, hash.md5.length);
    }
    
    @Override public boolean equals(Object o) { return o instanceof RomHash && ((RomHash)o).value == value; }
    @Override public int hashCode() { return Arrays.hashCode(value); }
  }
  
  public static class MultipleCRC implements GameID<long[]>
  {
    public final long[] values;
    public MultipleCRC(long... values) { this.values = values; }
    
    @Override public boolean equals(Object o) { return o instanceof MultipleCRC && Arrays.equals(((MultipleCRC)o).values, values); }
    @Override public int hashCode() { return Arrays.hashCode(values); }
  }
  
  public static class MagicHash implements GameID<byte[]>
  {
    private static MessageDigest digest;
    
    public final byte[] value;
    public MagicHash(Rom.Hash... hashes)
    {
        try
        {
          if (digest == null)
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e)
        {
          
          e.printStackTrace();
        }
          
        int bufferSize = hashes.length * (Long.BYTES + Integer.BYTES);
        DynamicByteBuffer buffer = new DynamicByteBuffer(bufferSize);
        
        for (Rom.Hash hash : hashes)
        {
          if (hash.size() != -1)
            buffer.putLong(hash.size());
          
          if (hash.crc() != -1)
            buffer.putInt((int)hash.crc());
          else if (hash.md5 != null)
            buffer.put(hash.md5);
          else if (hash.sha1 != null)
            buffer.put(hash.sha1);
        }
        
        value = digest.digest(buffer.array());
    }
    
    public MagicHash(String bytes)
    {
      value = StringUtils.fromHexString(bytes);
    }
    
    @Override public boolean equals(Object o) { return o instanceof MagicHash && Arrays.equals(((MagicHash)o).value, value); }
    @Override public int hashCode() { return Arrays.hashCode(value); }
  }
  
  @FunctionalInterface
  public static interface Generator
  {
    public GameID<?> compute(Game game);
    
    public static Generator DEFAULT = game -> {
      Rom.Hash[] hashes = game.stream().map(Rom::hash).toArray(i -> new Rom.Hash[i]);
      return new GameID.MagicHash(hashes);
    };
    
    public static Generator BY_RELEASE_NUMBER = game -> {
      return new GameID.Numeric(game.getAttribute(GameAttribute.NUMBER));
    };
    
    public static Generator BY_NAME = game -> {
      return new GameID.Textual(game.getAttribute(GameAttribute.TITLE));
    };
  }
}
