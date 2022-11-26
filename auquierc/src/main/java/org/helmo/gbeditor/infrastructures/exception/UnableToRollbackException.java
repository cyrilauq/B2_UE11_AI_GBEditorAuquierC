package org.helmo.gbeditor.infrastructures.exception;

import java.sql.SQLException;

/**
 * Définit une exception qui sera lancée quand l'application ne sera pas capable de faire un rollback lors d'une transaction.
 */
public class UnableToRollbackException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée une nouvelle UnableToRollbackException avec une exception SQL de départ.
	 *
	 * @param ex	Exception SQL de départ.
	 */
	public UnableToRollbackException(SQLException ex) {
        super(ex);
    }
}
