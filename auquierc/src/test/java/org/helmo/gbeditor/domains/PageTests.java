package org.helmo.gbeditor.domains;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PageTests {
    private Page p1;
    private Page p2;
    private Page p3;

    @Test
    void whenAPageIsInitializedThenThePageKnowsItsContent() {
        p1 = new Page("Page 1");
        p2 = new Page("Page 2");
        p3 = new Page("Page 3");
        assertEquals("Page 1", p1.getContent());
        assertEquals("Page 2", p2.getContent());
        assertEquals("Page 3", p3.getContent());
    }

    @Test
    void whenAddChoiseThenTheChoiceIsSavedInThePage() {
        p1 = new Page("Page 1");
        p2 = new Page("Page 2");
        p3 = new Page("Page 3");
        p1.addChoice("Choice 1", p2);
        p1.addChoice("Choice 2", p3);
        assertEquals(2, p1.getChoices().size());
        assertEquals(p2, p1.getChoices().get("Choice 1"));
        assertEquals(p3, p1.getChoices().get("Choice 2"));
    }

    @Test
    void whenAddChoiceWithNullPageThenDoesNothing() {
        p1 = new Page("Page 1");
        p1.addChoice("Choice 1", null);
        assertEquals(0, p1.getChoices().size());
    }

    @Test
    void whenAddChoiceWithNullLabelThenDoesNothing() {
        p1 = new Page("Page 1");
        p2 = new Page("Page 2");
        p1.addChoice(null, p2);
        assertEquals(0, p1.getChoices().size());
    }

    @Test
    void whenDeletePageThenAllChoiceAssociatedAreDeleted() {
        p1 = new Page("Page 1");
        p2 = new Page("Page 2");
        p3 = new Page("Page 3");
        p1.addChoice("Choice 1", p2);
        p1.addChoice("Choice 2", p3);
        p1.remove(p2);
        assertEquals(1, p1.getChoices().size());
        assertEquals(p3, p1.getChoices().get("Choice 2"));
    }

    @Test
    void whenAddChoiceAndTheTargetIsTheCurrentPageThenThrowException() {
        p1 = new Page("Page 1");
        assertThrows(IllegalArgumentException.class, () -> p1.addChoice("Choice 1", p1));
    }

    @Test
    void whenDeleteChoiceThenPageContainsNoMoreThisChoice() {
        p1 = new Page("Page 1");
        p2 = new Page("Page 2");
        p3 = new Page("Page 3");
        p1.addChoice("Choice 1", p2);
        p1.addChoice("Choice 2", p3);
        p1.removeChoice("Choice 1");
        assertEquals(1, p1.getChoices().size());
        assertEquals(p3, p1.getChoices().get("Choice 2"));
    }

}
