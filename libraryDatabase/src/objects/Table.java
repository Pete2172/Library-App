package objects;

/**
 * The interface combines objects representing entities in library database,
 * it allows all the operations on those objects to be more generic
 */
public interface Table {
    /**
     * This static method creates a query, which will allow DatabaseConnector class
     * to add records,
     * @param table_name name of entity in library database
     * @param args arguments with every attribute, which is in the entity
     * @return return query, which helps DatabaseConnector class to add
     * a proper record
     */
    public static String buildInsertQuery(String table_name, String... args) {
        StringBuilder query = new StringBuilder();
        query.append("insert into " + table_name + " values(NULL, ");
        for(int i=0; i < args.length; i++){
            query.append("\"" + args[i] + "\"" + ((i + 1 == args.length) ? ")" : ","));
        }
        return query.toString();
    }

}
