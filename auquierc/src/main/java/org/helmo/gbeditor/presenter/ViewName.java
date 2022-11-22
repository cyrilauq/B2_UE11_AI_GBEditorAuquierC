package org.helmo.gbeditor.presenter;

/**
 * Définit les noms des vues utilisée dans le programme.
 */
public enum ViewName {
    CREATE_BOOK_VIEW("CreateBookView"),
    MODIFY_BOOK_VIEW("ModifyBookView"),
    CREATE_PAGE_VIEW("CreatePageView"),
    MODIFY_PAGE_VIEW("ModifyPageView"),
    MANAGA_PAGE_VIEW("ManagePageView"),
    LOGIN_VIEW("LoginView"),
    HOME_VIEW("HomeView");

    private final String viewName;

    /**
     * Crée un nouveau nom de vue.
     *
     * @param viewName  Nom de la vue.
     */
    ViewName(final String viewName) {
        this.viewName = viewName;
    }

    /**
     * Retourne le nom de la vue.
     *
     * @return  Le nom de la vue.
     */
    public String getName() {
        return viewName;
    }
}
