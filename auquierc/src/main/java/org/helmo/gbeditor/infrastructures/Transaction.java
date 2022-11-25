package org.helmo.gbeditor.infrastructures;

import org.helmo.gbeditor.infrastructures.jdbc.UnableToRollbackException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Cette classe permet de créer une Transaction facilement.
 */
public class Transaction {

    private final Connection con;
    private ExceptionHandle rollbackAction;
    private ActionThrowingException commitAction;

    /**
     * Crée une nouvelle Transaction sur base d'une connection donnée.
     *
     * @param con   Connection avec laquelle la transaction va travailler.
     */
    public static Transaction from(Connection con) {
        try {
            con.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new TransactionNotSupportedException(ex);
        }
        return new Transaction(con);
    }

    /**
     * Crée une nouvelle Transaction sur base d'une connection donnée.
     *
     * @param con   Connection avec laquelle la transaction va travailler.
     */
    public Transaction(Connection con) {
        this.con = con;
    }

    /**
     * Définit l'action qui sera réalisée lors du commit de la transaction et retourne la transaction actuelle.
     *
     * @param sequence  Action à faire.
     *
     * @return          La transaction actuelle.
     */
    public Transaction commit(ActionThrowingException sequence) {
        this.commitAction = sequence;
        return this;
    }

    /**
     * Définit se qu'il faut faire lors du rollback et retourne la transaction actuelle.
     *
     * @param sequence  Exception à lancer en cas de rollback.
     *
     * @return          La transaction actuelle.
     */
    public Transaction onRollback(ExceptionHandle sequence) {
        this.rollbackAction = sequence;
        return this;
    }

    /**
     * Exécute la transaction.
     */
    public void execute() {
        try {
            commitAction.execute(con);
            con.commit();
        } catch (Exception ex) {
            try {
                con.rollback();
                rollbackAction.handle(ex);
            } catch (SQLException e) {
                throw new UnableToRollbackException(e);
            }
        } finally {
            try {
                con.setAutoCommit(true); //Active la gestion automatique des transactions
            } catch(SQLException ex) {
                throw new TransactionNotSupportedException(ex);
            }
        }
    }
}

class TransactionNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    /**
     * Crée une nouvelle TransactionNotSupportedException ayant une exception source donnée.
     *
     * @param ex    Exception source.
     */
	public TransactionNotSupportedException(SQLException ex) {
        super("Transaction are not supported by this DBMS or this driver", ex);
    }
}

@FunctionalInterface
interface ActionThrowingException {

    /**
     * Permet d'exécuter une action d'une transaction.
     *
     * @param con       Connection à la base de données sur laquelle on veut exécuter l'action.
     *
     * @throws Exception    Si une erreur s'est produite lors de l'exécution de l'action.
     */
    void execute(Connection con) throws Exception;
}

@FunctionalInterface
interface ExceptionHandle {
    void handle(Exception ex);
}
