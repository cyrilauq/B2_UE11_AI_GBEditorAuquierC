package org.helmo.gbeditor.presenter;

/**
 * Définit les méthodes que les classes implémentant l'interface devront avoir.
 */
public interface ViewInterface {

    /**
     * Affiche un message ou une erreur, suivant le type donné, à l'écran.
     *
     * @param txt       Texte à afficher
     * @param type      Type de message
     */
    public void setMessage(String txt, final TypeMessage type);

    /**
     * Permet de changer de vue.
     *
     * @param view     Vue vers laquelle l'utilisateur veut se rendre.
     */
    public void goTo(String view);

    /**
     * Affiche le nom de l'auteur à l'écran.
     *
     * @param name  Nom de l'auteur.
     */
    public void setAuthorName(String name);

    /**
     * Définit ce que la vue fait lorsque le programme l'affiche.
     */
    public void onEnter(String fromView);

    /**
     * Définit ce que la vue fait lorsque le programme la quittera.
     */
    void onLeave(String fromView);

    /**
     * Affiche la vue voulue dans une deuxième fenêtre.
     *
     * @param viewName  Deuxième vue à afficher
     */
    void showPopUp(final String viewName);

    /**
     * Rafraichit la vue.
     * Cette méthode sera à redéfinir dans les classes filles.
     */
    void refreshAll(final String fromView);


    /**
     * Rafraichit toutes les vues du programme.
     */
    void refreshAll(ViewName viewName);

    /**
     * Affiche un message d'erreur à l'utilisateur.
     *
     * @param error Message d'erreur.
     */
    void showErrorMessage(final String error);
}
