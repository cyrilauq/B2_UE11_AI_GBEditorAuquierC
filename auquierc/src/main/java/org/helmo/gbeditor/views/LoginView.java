package org.helmo.gbeditor.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.helmo.gbeditor.presenter.TypeMessage;
import org.helmo.gbeditor.presenter.LoginPresenter;
import org.helmo.gbeditor.presenter.ViewInterface;
import org.helmo.gbeditor.presenter.ViewName;
import org.helmo.gbeditor.views.style.Theme;

public class LoginView extends View implements ViewInterface {
    private final LoginPresenter presenter;

    private final Label titleLbl = new Label("Connexion"); {
        titleLbl.getStyleClass().add("title");
        titleLbl.setPrefWidth(Theme.WINDOW_WIDTH);
    }

    private final Label nomLbl = new Label("Nom:");
    private final TextField nomFld = new TextField(); {
        nomFld.setOnKeyPressed(event -> onKeyPressed(event.getCode().getCode()));
    }

    private final Label prenomLbl = new Label("Prénom:");
    private final TextField prenomFld = new TextField(); {
        prenomFld.setOnKeyPressed(event -> onKeyPressed(event.getCode().getCode()));
    }

    private final Button connexionBtn = new Button("Connexion"); {
        connexionBtn.setOnAction(a -> notifyOnConnexionPressed());
    }

    private final Label messageLbl = new Label(); {
        messageLbl.getStyleClass().add("error");
    }

    private final GridPane form = new GridPane(); {
        form.getStyleClass().add("content-root");
        form.setPrefWidth(Theme.WINDOW_WIDTH * 0.2);
        form.setAlignment(Pos.CENTER);
        form.add(nomLbl, 0, 1);
        form.add(nomFld, 1, 1);
        form.add(prenomLbl, 0, 2);
        form.add(prenomFld, 1, 2);
        form.add(connexionBtn, 0, 3, 2, 1);
    }

    /**
     * Crée une nouelle LoginView
     *
     * @param viewName         Titre de la vue.
     * @param presenter     Presenter avec lequel la vue va interagir.
     */
    public LoginView(final ViewName viewName, final LoginPresenter presenter) {
        super(viewName);
        getChildren().addAll(titleLbl, form, messageLbl);

        this.presenter = presenter;
        presenter.setView(this);
    }

    private void notifyOnConnexionPressed() {
        presenter.onConnexion(nomFld.getText(), prenomFld.getText());
    }

    @Override
    public void setMessage(String txt, final TypeMessage message) {
        messageLbl.setText(txt);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        if(keyCode == KeyCode.ENTER.getCode()) {
            notifyOnConnexionPressed();
        }
    }
}
