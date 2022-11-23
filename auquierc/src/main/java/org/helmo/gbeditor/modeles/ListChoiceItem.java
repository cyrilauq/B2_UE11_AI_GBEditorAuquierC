package org.helmo.gbeditor.modeles;

public class ListChoiceItem {
    private final String content;
    private final int numPage;
    private String label;

    public ListChoiceItem(final int numPage, final String content) {
        this.numPage = numPage;
        this.content = content;
    }

    public ListChoiceItem(final String label, final int numPage, final String content) {
        this(numPage, content);
        this.label = label;
    }

    public int getNumPage() {
        return numPage;
    }

    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }
}
