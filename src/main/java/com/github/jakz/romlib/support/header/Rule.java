package com.github.jakz.romlib.support.header;

public class Rule
{
  public final int signatureOffset;
  public final byte[] signature;
  public final int bytesToSkip;
  
  private Rule(int bytesToSkip, byte[] signature, int signatureOffset)
  {
    this.bytesToSkip = bytesToSkip;
    this.signature = signature;
    this.signatureOffset = signatureOffset;
  }
  
  public static Rule of(int bytesToSkip)
  {
    return new Rule(bytesToSkip, null, 0);
  }
  
  public static Rule of(String signature, int bytesToSkip)
  {
    return of(signature.getBytes(), bytesToSkip);
  }
  
  public static Rule of(String signature, int offset, int bytesToSkip)
  {
    return new Rule(bytesToSkip, signature.getBytes(), offset);
  }
  
  public static Rule of(byte[] signature, int bytesToSkip)
  {
    return new Rule(bytesToSkip, signature, 0);
  }
  
  
}
