package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.repositories.DataRepository;

/**
 * Cette classe s'occupe de la logique qui se cache derrière les actions de la vue LoginView.
 *
 * @author  Cyril Auquier
 */
public class LoginPresenter extends Presenter {

    private final DataRepository repo;
    private ViewInterface view;
    private final Session session;

    /**
     * Crée un nouveau LoginPresenter à partir d'une Session courante donnée.
     *
     * @param session   Session courante.
     * @param repo
     */
    public LoginPresenter(Session session, final DataRepository repo) {
        this.repo = repo;
        this.session = session;
    }

    /**
     * Permet à l'utilisateur de se connecter et d'accéder à l'écran suivant.
     * Si le champ name ou firstname est vide, la connexion sera ignorée.
     *
     * @param name          Nom de l'utilisateur.
     * @param firstname     Prénom de l'utilisateur.
     */
    public void onConnexion(final String name, final String firstname) {
        if(inputsAreValid(name, firstname)) {
            connexion(name, firstname);
            repo.setCurrentAuthor(name + " " + firstname);
            view.goTo("HomeView");
        }
    }

    private boolean inputsAreValid(final String name, final String firstname) {
        if(name == null && firstname == null) {
            view.setMessage("Vous devez compléter le formulaire pour vous connecter.", TypeMessage.MESSAGE);
            return false;
        }
        return inputsContentAreValid(name, firstname);
    }

    private boolean inputsContentAreValid(final String name, final String firstname) {
        if(name == null || name.isBlank()) {
            view.setMessage("Le nom ne peut pas être vide.", TypeMessage.MESSAGE);
            return false;
        } else if (firstname == null || firstname.isBlank()) {
            view.setMessage("Le prénom ne peut pas être vide.", TypeMessage.MESSAGE);
            return false;
        }
        return true;
    }

    private void connexion(final String name, final String firstname) {
        session.setAuthor(name, firstname);
    }

    /**
     * Définit la vue avec laquelle le presenter va interagir.
     *
     * @param view  Vue avec laquelle interagir.
     */
    public void setView(ViewInterface view) {
        this.view = view;
    }
}
