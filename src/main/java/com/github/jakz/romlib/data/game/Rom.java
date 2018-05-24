package com.github.jakz.romlib.data.game;

import java.util.Arrays;

import com.github.jakz.romlib.data.game.attributes.RomAttribute;
import com.pixbits.lib.io.archive.Verifiable;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.digest.DigestInfo;
import com.pixbits.lib.lang.StringUtils;

public class Rom implements Verifiable
{
  public class Hash
  {
    byte[] md5;
    byte[] sha1;
    long crc32;
    RomSize size;
    
    public Hash(RomSize size, long crc32, byte[] md5, byte[] sha1)
    {
      this.size = size;
      this.crc32 = crc32;
      this.md5 = md5;
      this.sha1 = sha1;
    }
    
    public boolean isEquivalent(Hash o)
    {
      return 
          o.size.equals(o.size) && 
          crc32 == o.crc32 && 
          (md5 == null || o.md5 == null || Arrays.equals(md5,o.md5)) && 
          (sha1 == null || o.sha1 == null || Arrays.equals(sha1,o.sha1));
    }
    
    public boolean equals(Object o) { return (o instanceof Hash) && ((Hash)o).isEquivalent(this); }
    public int hashCode() { return (int)crc32; }
    
    public long crc() { return crc32; }
    public long size() { return size.bytes(); }
  }
  
  
  public final String name;
  public final Hash hash;
  
  private Game game;
  private Handle handle;
  
  public Rom(RomSize size, long crc)
  {
    this(null, size, crc, null, null);
  }
  
  public Rom(long crc)
  {
    this(null, null, crc, null, null);
  }
  
  public Rom(String name, RomSize size, long crc32)
  {
    this(name, size, crc32, null, null);
  }
  
  public Rom(String name, RomSize size, long crc32, byte[] md5, byte[] sha1)
  {
    this.name = name;
    this.hash = new Hash(size, crc32, md5, sha1);
    this.handle = null;
  }
  
  public Rom(String name, RomSize size, DigestInfo info)
  {
    this(name, size, info.crc, info.md5, info.sha1);
  }
  
  public void setHandle(Handle handle) { this.handle = handle; }
  public Handle handle() { return handle; }
  
  void setGame(Game game) { this.game = game; }
  public Game game() { return game; }
    
  @Override public String toString()
  { 
    StringBuilder builder = new StringBuilder();

    builder.delete(0, builder.length());
    builder.append("[").append(name).append(", size: ").append(hash.size).append(", crc: ").append(Long.toHexString(hash.crc32));
    if (hash.md5 != null)
      builder.append(", md5: ").append(StringUtils.toHexString(hash.md5));
    if (hash.sha1 != null)
      builder.append(", sha1: ").append(StringUtils.toHexString(hash.sha1));
    builder.append("]");
    
    return builder.toString();
  }
  
  public boolean isPresent() { return handle != null; }
  public boolean isMissing() { return handle == null; }
 
  public boolean isEquivalent(Rom rom)
  {
    return rom.hash.isEquivalent(this.hash);
  }
  
  //@Override public boolean equals(Object o) { return o instanceof Rom && ((Rom)o).isEquivalent(this); }
  //@Override public int hashCode() { return (int)crc32; }
  
  @SuppressWarnings("unchecked")
  public <T> T getAttribute(RomAttribute attribute)
  {
    switch (attribute)
    {
      case ROM_NAME: return (T)name;
      case SIZE: return (T)hash.size;
      case SHA1: return (T)hash.sha1;
      case MD5: return (T)hash.md5;
      case CRC: return (T)(Long)hash.crc32;
      default: return null;
    }
  }
    
  @Override public long size() { return hash.size.bytes(); }
  @Override public long crc() { return hash.crc32; }
  @Override public byte[] sha1() { return hash.sha1; }
  @Override public byte[] md5() { return hash.md5; }  
  
  public Hash hash() { return hash; }
}
