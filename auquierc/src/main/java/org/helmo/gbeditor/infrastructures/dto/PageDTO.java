package org.helmo.gbeditor.infrastructures.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.TreeMap;

public class PageDTO {
    @SerializedName("content")
    public final String content;
    @SerializedName("prevPageContent")
    public final String prevPageContent;
    @SerializedName("nextPageContent")
    public String nextPageContent = "";
    @SerializedName("choices")
    public final Map<String, String> choices = new TreeMap<>();
    @SerializedName("numPage")
    public int numPage;

    public PageDTO(final String content, final String prevPageContent, final Map<String, String> choices) {
        this.content = content;
        this.prevPageContent = prevPageContent;
        this.choices.putAll(choices);
        numPage = 0;
    }

    public PageDTO(final String content, final Map<String, String> choices, final int numPage) {
        this(content, "", choices);
        this.numPage = numPage;
    }

    public String getNextPageContent() {
        return nextPageContent;
    }

    public String getContent() {
        return content;
    }

    public String getPrevPageContent() {
        return prevPageContent;
    }

    public Map<String, String> getChoices() {
        return choices;
    }

    public int getNumPage() {
        return numPage;
    }
}
