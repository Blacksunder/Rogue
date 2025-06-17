package backend.datalayer;

import backend.domain.game_object.GameObject;
import backend.domain.game_object.creature.monsters.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class EnemyAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Enemy.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                TypeAdapter<T> delegate = (TypeAdapter<T>) gson.getDelegateAdapter(
                        EnemyAdapterFactory.this,
                        TypeToken.get((Class<? extends T>) value.getClass())
                );
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = JsonParser.parseReader(in);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String type = jsonObject.get("name").getAsString();

                Class<? extends GameObject> concreteClass = getConcreteClass(type);
                return (T) gson.getDelegateAdapter(EnemyAdapterFactory.this,
                        TypeToken.get(concreteClass)).fromJsonTree(jsonElement);
            }
        };
    }

    private static Class<? extends Enemy> getConcreteClass(String type) {
        Class<? extends Enemy> concreteClass;
        switch (type) {
            case "Ghost" -> concreteClass = Ghost.class;
            case "Snake" -> concreteClass = Snake.class;
            case "Ogre" -> concreteClass = Ogre.class;
            case "Vampire" -> concreteClass = Vampire.class;
            case "Zombie" -> concreteClass = Zombie.class;
            case "Weapon", "Food", "Elixir", "Scroll", "Mimic" -> concreteClass = Mimic.class;
            default -> throw new JsonParseException("Unknown game object: " + type);
        }
        return concreteClass;
    }
}
