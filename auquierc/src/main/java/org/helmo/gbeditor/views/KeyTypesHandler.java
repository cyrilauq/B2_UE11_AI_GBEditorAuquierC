package org.helmo.gbeditor.views;


import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

public class KeyTypesHandler implements EventHandler<KeyEvent> {
    private final TextArea handler;
    private final Label nbCharHandler;

    public final int maxChar;

    /**
     * Crée un nouvel KeyTypesHandler
     *
     * @param handler       TextArea sur lequel on veut agir.
     * @param nbCharHandler Label dans lequel on veut écrire le nombre de caractères contenu dans le TextArea
     * @param maxChar       Nombre de caractères maximum autorisé dans le TextArea.
     */
    public KeyTypesHandler(TextArea handler, Label nbCharHandler, int maxChar) {
        this.handler = handler;
        this.nbCharHandler = nbCharHandler;
        this.maxChar = Math.abs(maxChar);
    }

    @Override
    public void handle(KeyEvent event) {
        if(handler.getText().length() > maxChar) {
            handler.setText(handler.getText(0, maxChar));
            handler.positionCaret(maxChar);
        }
        if(nbCharHandler != null) {
            nbCharHandler.setText(handler.getText().length() + "/" + maxChar);
        }
    }
}