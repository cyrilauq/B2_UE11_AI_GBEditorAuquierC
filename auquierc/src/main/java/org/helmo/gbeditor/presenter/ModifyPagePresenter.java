package org.helmo.gbeditor.presenter;

import org.helmo.gbeditor.domains.Book;
import org.helmo.gbeditor.domains.Page;
import org.helmo.gbeditor.domains.Session;
import org.helmo.gbeditor.infrastructures.exception.UnableToSavePageException;
import org.helmo.gbeditor.modeles.ListChoiceItem;
import org.helmo.gbeditor.repositories.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gérer ce qui va être affiché à l'écran utilisateur et comment le programme va réagir au évènement lancés par sa vue.
 */
public class ModifyPagePresenter extends Presenter implements PageViewHandler, ChoiceViewHandler {

    private ModifyPageInterface view;
    private final Session session;
    private final DataRepository repo;
    private Book currentBook;
    private Page currentPage;

    /**
     * Crée un nouveau ModifyPagePresenter avec une session et un DataRepository donné.
     *
     * @param session   Session courante sur laquelle on travaille actuellement.
     * @param repo      Repository qui permet d'interagir avec la ressource choisie.
     */
    public ModifyPagePresenter(final Session session, final DataRepository repo) {
        this.session = session;
        this.repo = repo;
    }

    /**
     * Définit la vue avec laquelle le presenter va interagir.
     *
     * @param view  Vue avec laquelle le presenter interagit.
     */
    public void setView(final ModifyPageInterface view) {
        this.view = view;
    }

    /**
     * Crée un nouveau choix avec comme libellé le label donnée et comme cible la page donnée.
     * Si aucune pas n'est trouvée pour le contenu donné, on ne fait rien.
     *
     * @param label         Libellé du choix à créer.
     * @param targetContent Contenu de la page cible.
     */
    public void onChoiceCreated(final String label, final String targetContent) {
        var target = currentBook.getPageFor(targetContent);
        if(target != null) {
            try {
                currentPage.addChoice(label, target);
                repo.save(currentBook);
                view.setMessage("Le choix a bien été ajouté.", TypeMessage.MESSAGE);
                refresh();
            } catch (Page.TheTargetPageCannotBeTheSourcePage e) {
                view.setMessage("La page cible du choix ne peut pas être la même que la page de destination.", TypeMessage.MESSAGE);
            }
        } else {
            view.setMessage("Vous ne pouvez pas ajouter de choix sans spécifier de page de destination.", TypeMessage.MESSAGE);
        }
        // TODO : Avertir utilisateur si page est la cible de choix.
    }

    @Override
    public void onEnter(String fromView) {
        refresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        // TODO : Faut-il
        setCurrentInfos();
        view.setPageContent(currentPage.getContent());
        setChoices();
        setTargets();
    }

    private void setCurrentInfos() {
        currentBook = session.getCurrentBook();
        currentPage = currentBook.getPageFor(session.getCurrentPageContent());
    }

    private void setTargets() {
        List<ListChoiceItem> target = new ArrayList<>();
        AtomicInteger i2 = new AtomicInteger(1);
        currentBook.forEach(p -> target.add(new ListChoiceItem(i2.getAndIncrement(), p.getContent())));
        view.setTarget(target);
    }

    private void setChoices() {
        List<ListChoiceItem> choices = new ArrayList<>();
        currentPage.forEach(p -> choices.add(new ListChoiceItem(p, currentBook.getNForPage(currentPage.getPageForChoice(p)), p)));
        view.setChoices(choices);
    }

    @Override
    public void onHomePressed() {
        view.goTo(ViewName.MANAGE_PAGE_VIEW.getName());
    }

    @Override
    public void onEdit(String content) {

    }

    @Override
    public void onConfirmedDelete(String content) {
        try {
            currentPage.removeChoice(content);
            repo.save(currentBook);
            refresh();
        } catch (UnableToSavePageException e) {
            view.setMessage("Une erreur est survenue lors de la suppression du choix", TypeMessage.MESSAGE);
        }
    }
}
