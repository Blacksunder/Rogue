package backend.datalayer;

import backend.domain.game_object.GameObject;
import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.elixir.*;
import backend.domain.game_object.item.scroll.AgilityScroll;
import backend.domain.game_object.item.scroll.MaxHealthScroll;
import backend.domain.game_object.item.scroll.PowerScroll;
import backend.domain.game_object.item.food.Apple;
import backend.domain.game_object.item.food.Burger;
import backend.domain.game_object.item.food.Pizza;
import backend.domain.game_object.item.weapon.Axe;
import backend.domain.game_object.item.weapon.Katana;
import backend.domain.game_object.item.weapon.Sword;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ItemAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Item.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                TypeAdapter<T> delegate = (TypeAdapter<T>) gson.getDelegateAdapter(
                        ItemAdapterFactory.this,
                        TypeToken.get((Class<? extends T>) value.getClass())
                );
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = JsonParser.parseReader(in);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String type = jsonObject.get("name").getAsString();

                Class<? extends GameObject> concreteClass = getConcreteClass(type);
                return (T) gson.getDelegateAdapter(ItemAdapterFactory.this,
                        TypeToken.get(concreteClass)).fromJsonTree(jsonElement);
            }
        };
    }

    private static Class<? extends Item> getConcreteClass(String type) {
        Class<? extends Item> concreteClass;
        switch (type) {
            case "Elixir" -> concreteClass = Elixir.class;
            case "Axe" -> concreteClass = Axe.class;
            case "Katana" -> concreteClass = Katana.class;
            case "Sword" -> concreteClass = Sword.class;
            case "Apple" -> concreteClass = Apple.class;
            case "Burger" -> concreteClass = Burger.class;
            case "Pizza" -> concreteClass = Pizza.class;
            case "Agility Scroll" -> concreteClass = AgilityScroll.class;
            case "Max Health Scroll" -> concreteClass = MaxHealthScroll.class;
            case "Power Scroll" -> concreteClass = PowerScroll.class;
            case "Agility Elixir" -> concreteClass = AgilityElixir.class;
            case "Health Elixir" -> concreteClass = HealthElixir.class;
            case "Max Health Elixir" -> concreteClass = MaxHealthElixir.class;
            case "Power Elixir"-> concreteClass = PowerElixir.class;
            default -> throw new JsonParseException("Unknown game object: " + type);
        }
        return concreteClass;
    }
}
