package org.helmo.gbeditor.infrastructures.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.TreeMap;

/**
 * Cette classe s'occupe de gérer le manière dont une page sera stockée.
 */
public class PageDTO {
    @SerializedName("content")
    public final String content;
    @SerializedName("choices")
    public final Map<String, String> choices = new TreeMap<>();
    @SerializedName("numPage")
    public int numPage;

    /**
     * Crée une nouvelle PageDTO avec un contenu, des choix et un numéro de page donnés.
     *
     * @param content   Contenu de la page
     * @param choices   Choix de la page
     * @param numPage   Numéro de la page
     */
    public PageDTO(final String content, final Map<String, String> choices, final int numPage) {
        this.content = content;
        this.choices.putAll(choices);
        this.numPage = numPage;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getChoices() {
        return choices;
    }

    public int getNumPage() {
        return numPage;
    }
}
