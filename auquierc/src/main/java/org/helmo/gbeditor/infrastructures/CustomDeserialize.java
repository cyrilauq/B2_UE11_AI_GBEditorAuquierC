package org.helmo.gbeditor.infrastructures;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.helmo.gbeditor.infrastructures.dto.BookDTO;
import org.helmo.gbeditor.infrastructures.dto.PageDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Cette classe permet de définir un deserializer personnalisé pour la désérialisation Json.</p>
 * <p>Il empêche l'apparition de valeur nulle dans les BookDTO qui sont initialisé à partir du fichier Json.</p>
 * <p>J'ai utilisé cette solution, car lors de la désérialisation, je fais un switch sur un attribut version de livre pour savoir comme je dois convertir ce dernier.
 * Or, lorsque cette attribut n'existe pas la valeur donnée est null et un switch n'accepte pas de valeur null</p>
 *
 * <p>Source: <a href="https://stackoverflow.com/questions/6096940/how-do-i-write-a-custom-json-deserializer-for-gson">StackOverflow</a></p>
 */
public class CustomDeserialize implements JsonDeserializer<BookDTO> {
    @Override
    public BookDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var jo = json.getAsJsonObject();
        return new BookDTO(get(jo, "title"),
                get(jo, "isbn"),
                get(jo, "author"),
                get(jo, "resume"),
                get(jo, "imgPath"),
                get(jo, "version"),
                getPages(jo));
    }

    private List<PageDTO> getPages(JsonObject jo) {
        return jo.get("pages") == null ? new ArrayList<>() :
                new Gson().fromJson(jo.get("pages"), new TypeToken<List<PageDTO>>() {}.getType());
    }

    /**
     * Récupère la valeur correspondant à l'attribut rechercher dans un objet Json donné.
     *
     * @param jo        Objet Json dans lequel on souhaite récupérer un attribut donné.
     * @param attribute Attribut recherché
     *
     * @return          Si l'attribut a été trouvé dans l'objet Json, la valeur lui correspondant sous format de chaine de caractères.
     *                  Si l'attribut n'a pas été trouvé, retourne une chaine de caractères vide.
     */
    private String get(JsonObject jo, String attribute) {
        var attr = jo.get(attribute);
        return attr == null ? "" : attr.getAsString();
    }
}
