package com.github.jakz.romlib.data.game;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import com.pixbits.lib.lang.StringUtils;

public interface GameID<T>
{
  public static class Numeric implements GameID<Integer>
  {
    private final int value;
    public Numeric(int value) { this.value = value; }
  }
  
  public static class Textual implements GameID<String>
  {
    private final String value;
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
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        
        for (Rom.Hash hash : hashes)
        {
          buffer.putLong(hash.size());
          buffer.putInt((int)hash.crc());
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
}
