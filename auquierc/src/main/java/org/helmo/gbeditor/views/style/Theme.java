package org.helmo.gbeditor.views.style;

/**
 * Cette classe définit les éléments de style, images qui seront utilisés dans l'application.
 *
 * @author  Cyril Auquier
 */
public class Theme {
    public final static int WINDOW_WIDTH = 750;
    public final static int WINDOW_HEIGHT = 550;

    public final static double SND_WINDOW_WIDTH = Theme.WINDOW_WIDTH * .7;

    public final static String DEFAULT_BOOK_COVER = Theme.class.getResource("/img/placeholder.jpg").toString();
}
