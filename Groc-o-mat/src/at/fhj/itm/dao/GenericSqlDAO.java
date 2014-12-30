package at.fhj.itm.dao;

import java.io.Serializable;
import java.sql.Connection;

import at.fhj.itm.utils.DBConnector;

public abstract class GenericSqlDAO <T, PK extends Serializable> {

	Connection conn = DBConnector.getConnection();
    /** Persist the newInstance object into database */
    public abstract PK create(T newInstance);

    /** Retrieve an object that was previously persisted to the database using
     *   the indicated id as primary key
     */
    public abstract T read(PK id);

    /** Save changes made to a persistent object.  */
    public abstract void update(T transientObject);

    /** Remove an object from persistent storage in the database */
    public abstract void delete(T persistentObject);
}


