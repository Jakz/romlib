package com.github.jakz.romlib.support.cso;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.digest.DigestInfo;
import com.pixbits.lib.io.digest.DigestOptions;
import com.pixbits.lib.io.digest.Digester;
import com.pixbits.lib.lang.StringUtils;

public class CSOBinaryHandle extends Handle
{
  private Path path;
  
  private long size;
  private long compressedSize;
  private long crc;
  
  private CSOInfo info;
  
  public CSOBinaryHandle(Path path)
  {
    this.path = path;
    size = -1L;
    compressedSize = -1L;
    crc = -1L;
  }
  
  public CSOBinaryHandle(Path path, CSOInfo info)
  {
    this(path);
    this.info = info;
  }
  
  public CSOBinaryHandle(Path path, byte[] header, long crc, long size, long csize)
  {
    this(path);
    this.info = new CSOInfo(header);
    this.crc = crc;
    this.size = size;
    this.compressedSize = csize;
  }
  
  private void cacheInfoIfNeeded() throws IOException
  {
    if (info == null)
      info = new CSOInfo(path);
  }
  
  @Override public Handle getVerifierHandle() { return this; }

  @Override public String toString() { return path.getFileName().toString(); }

  @Override public Path path() { return path; }
  @Override public String relativePath() { return path.getFileName().toString(); } 
  @Override public String fileName() { return path().toString(); }

  @Override public String plainName() { return FileUtils.trimExtension(path.getFileName().toString()); }
  
  @Override public String internalName() { return path.getFileName().toString(); }
  @Override public String plainInternalName() { return plainName(); }

  @Override public void relocate(Path file) { this.path = file; }

  @Override
  public Handle relocateInternal(String internalName)
  {
    throw new UnsupportedOperationException("a binary handle doesn't have an internal filename");
  }

  @Override
  public boolean isArchive() { return false; }

  @Override
  public String getInternalExtension() { return getExtension(); }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return null;
  }

  @Override
  public long crc()
  {
    try
    {
      if (crc == -1)
        computeCRC();
    } 
    catch (IOException|NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }

    return crc;
  }

  @Override
  public long size()
  {
    try
    {
      if (size == -1)
      {
        cacheInfoIfNeeded();
        size = info.uncompressedSize();
      }
      return size;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return 0;
    } 
  }

  @Override
  public long compressedSize()
  {
    try
    {
      if (compressedSize == -1)
        compressedSize = Files.size(path);
      return compressedSize;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return 0;
    }  
  }
  
  public void computeCRC() throws NoSuchAlgorithmException, IOException
  {
    if (crc == -1)
    {
      cacheInfoIfNeeded();
      CSOInputStream stream = new CSOInputStream(path, info);
      Digester digester = new Digester(new DigestOptions(true, false, false, false));
      DigestInfo d = digester.digest(null, stream);
      
      crc = d.crc;
    }
  }
  
  private JsonElement serializeHeader() { return info.serialize(); }

  public void serializeToJson(JsonObject j, JsonSerializationContext context)
  {
    j.add("header", serializeHeader());
    j.addProperty("size", size());
    j.addProperty("csize", compressedSize());
  }
  
  public CSOBinaryHandle(JsonObject o, JsonDeserializationContext context)
  {
    this(
      context.deserialize(o.get("path"), Path.class), 
      StringUtils.fromHexString(o.get("header").getAsString()), 
      Long.parseUnsignedLong(o.get("crc").getAsString(), 16), 
      o.get("size").getAsLong(), 
      o.get("csize").getAsLong()
    );
  }
}
