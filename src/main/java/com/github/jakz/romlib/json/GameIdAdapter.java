package com.github.jakz.romlib.json;

import java.lang.reflect.Type;

import com.github.jakz.romlib.data.game.GameID;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;;

public class GameIdAdapter implements JsonSerializer<GameID<?>>, JsonDeserializer<GameID<?>> {
  @Override
  public JsonElement serialize(GameID<?> src, Type type, JsonSerializationContext context)
  {
    if (src instanceof GameID.CRC)
      return context.serialize(((GameID.CRC)src).value);
    else if (src instanceof GameID.MultipleCRC)
      return context.serialize(((GameID.MultipleCRC)src).values);
    else
      throw new JsonParseException("No way to serialize GameID<?> " + src);
  }
  
  @Override
  public GameID<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    if (json.isJsonArray())
      return new GameID.MultipleCRC(context.deserialize(json, long[].class));
    else
      return new GameID.CRC(context.deserialize(json, Long.class));
  }
}