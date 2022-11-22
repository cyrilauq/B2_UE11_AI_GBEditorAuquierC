package org.helmo.gbeditor.modeles;

public class ListChoiceItem {
    private final String content;
    private final int numPage;

    public ListChoiceItem(final int numPage, final String content) {
        this.numPage = numPage;
        this.content = content;
    }

    public int getNumPage() {
        return numPage;
    }

    public String getContent() {
        return content;
    }
}
