package com.github.jakz.romlib.json;

import java.lang.reflect.Type;

import com.github.jakz.romlib.data.game.GameID;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pixbits.lib.lang.StringUtils;;

public class GameIdAdapter implements JsonSerializer<GameID<?>>, JsonDeserializer<GameID<?>> {
  @Override
  public JsonElement serialize(GameID<?> src, Type type, JsonSerializationContext context)
  { 
    if (src instanceof GameID.CRC)
      return context.serialize(((GameID.CRC)src).value);
    else if (src instanceof GameID.MagicHash)
      return context.serialize(StringUtils.toHexString(((GameID.MagicHash)src).value));
    else
    {
      JsonArray a = new JsonArray();

      if (src instanceof GameID.SizeAndCRC)
      {
        a.add(new JsonPrimitive("size-crc"));
        a.add(((GameID.SizeAndCRC)src).value);
      }
      else if (src instanceof GameID.MultipleCRC)
      {
        a.add(new JsonPrimitive("multiple-crc"));
        a.add(context.serialize(((GameID.MultipleCRC)src).values));
      }
      else if (src instanceof GameID.Numeric)
      {
        a.add(new JsonPrimitive("number"));
        a.add(context.serialize(((GameID.Numeric)src).value));
      }
      else if (src instanceof GameID.Textual)
      {
        a.add(new JsonPrimitive("textual"));
        a.add(context.serialize(((GameID.Textual)src).value));
      }
      else
        throw new JsonParseException("No way to serialize GameID<?> " + src);
    
      return a;
    }
  }
  
  @Override
  public GameID<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    if (json.isJsonPrimitive())
    {
      JsonPrimitive pj = json.getAsJsonPrimitive();
      
      if (pj.isNumber())
        return new GameID.CRC(context.deserialize(pj, Long.class));
      else if (pj.isString())
        return new GameID.MagicHash((String)context.deserialize(pj, String.class));
    }
    else
    {
      JsonArray j = json.getAsJsonArray();
      String jtype = j.get(0).getAsString();
      
      if (jtype.equals("size-crc"))
        return new GameID.SizeAndCRC(j.get(1).getAsLong());
      else if (jtype.equals("multiple-crc"))
        return new GameID.MultipleCRC(context.deserialize(j.get(1), long[].class));
      else if (jtype.equals("number"))
        return new GameID.Numeric(context.deserialize(j.get(1), Integer.class));
      else if (jtype.equals("textual"))
        return new GameID.Textual(context.deserialize(j.get(1), String.class));
    }
    
    throw new JsonParseException("No way to unserialize GameID<?>");
  }
}