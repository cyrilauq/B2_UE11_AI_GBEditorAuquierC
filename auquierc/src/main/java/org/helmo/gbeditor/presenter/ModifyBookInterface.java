package org.helmo.gbeditor.presenter;

/**
 * Définit les méthodes qui seront implémentées par la vue et qui permettront au presenter d'interagir avec elle.
 * Ces méthodes seront utiles pour la modification de livres.
 */
public interface ModifyBookInterface extends CreateBookInterface {

    /**
     * Affiche le titre du livre à l'utilisateur.
     *
     * @param title     Titre du livre à afficher.
     */
    void setTitle(final String title);

    /**
     * Affiche le résumé du livre à l'utilisateur.
     *
     * @param resume     Résumé du livre à afficher.
     */
    void setResume(final String resume);

    /**
     * Affiche la couverture du livre à l'utilisateur.
     *
     * @param imgPath     Couverture du livre à afficher.
     */
    void setImg(final String imgPath);

}
