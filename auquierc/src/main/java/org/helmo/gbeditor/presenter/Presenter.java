package org.helmo.gbeditor.presenter;

/**
 * Cette classe définit les méthodes que chacune de ses classes filles pourront implémenter.
 *
 * @author  Cyril Auquier
 */
public class Presenter {

    /**
     * Définit les actions réalisées par le presenter lorsque la vue fera appel à lui.
     * Cette méthode sera rédéfinie par les classes filles implémentant Presenter.
     *
     * @param fromView  Nom de la vue précédente.
     */
    public void onEnter(final String fromView) { }

    /**
     * Définit les actions réalisées par le presenter lorsque la vue fera appel à lui.
     * Cette méthode sera rédéfinie par les classes filles implémentant Presenter.
     *
     * @param toView  Nom de la vue précédente.
     */
    public void onLeave(final String toView) { }


    /**
     * Retourne à la vue HomeView.
     * Cette méthode sera rédéfinie par les classes filles implémentant Presenter.
     */
    public void onHomePressed() { }

    /**
     * Définit les actions qui seront faites par le presenter lors du rafraichissement de la vue.
     * Cette méthode sera redéfinir par les classes filles.
     */
    public void onRefresh() { }
}
