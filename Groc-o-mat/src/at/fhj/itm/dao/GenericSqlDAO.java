package at.fhj.itm.dao;

import java.io.Serializable;
import java.sql.Connection;

import at.fhj.itm.utils.DBConnector;

public abstract class GenericSqlDAO <T, PK extends Serializable> {

	static Connection conn = DBConnector.getConnection();
    
	/**
	 * Inserts a new Item into the database
	 * @params newInstance - an instance to create
	 * @return -1 if fail
	 */
    public abstract PK create(T newInstance);

	/**
	 * Reads an existing item from the database
	 * @params ean - eancode as integer value
	 * @return itm - a grocery 'Item'
	 */
    public abstract T read(PK id);

    /**
     * Updating an existing database object
     * @param transientObject - object to update
     */
    public abstract void update(T transientObject);

    /**
     * Removes an object from the database
     * @param persistentObject - object to delete
     */
    public abstract void delete(T persistentObject);
}


