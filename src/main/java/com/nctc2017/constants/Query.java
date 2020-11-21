package com.nctc2017.constants;

public class Query {
    /**
     * This query allows to get any Entity by OBJECT_TYPE_ID and OBJECT_ID.
     * Call execute() or query() must have 4 parameters for PreparedStatement: OBJECT_TYPE_ID, OBJECT_ID, OBJECT_TYPE_ID, OBJECT_ID 
     * */
    public static final String FIND_ANY_ENTITY = 
            "SELECT atr_temp.NAME, atr_val.VALUE"
                    + " FROM OBJECTS entity_obj, ATTRIBUTES atr_temp, OBJECTS entity_templ, ATTRIBUTES_VALUE atr_val"
                    + " WHERE"
                    + "     entity_obj.OBJECT_TYPE_ID = ?"// obj type id
                    + " AND entity_obj.OBJECT_ID = ?"// obj id
                    + " AND entity_templ.OBJECT_ID = entity_obj.SOURCE_ID"

                    + " AND atr_temp.OBJECT_TYPE_ID = entity_templ.OBJECT_TYPE_ID"
                    + " AND atr_val.ATTR_ID = atr_temp.ATTR_ID"
                    + " AND atr_val.OBJECT_ID = entity_templ.OBJECT_ID"

                    + " UNION"
                    + " SELECT atr_obj.NAME, atr_val.VALUE"
                    + " FROM OBJECTS entity_obj, ATTRIBUTES atr_obj, ATTRIBUTES_VALUE atr_val"
                    + " WHERE"
                    + "     entity_obj.OBJECT_TYPE_ID = ?"// obj type id
                    + " AND entity_obj.OBJECT_ID = ?"// obj id"
                    + " AND atr_obj.OBJECT_TYPE_ID = entity_obj.OBJECT_TYPE_ID"

                    + " AND atr_val.ATTR_ID = atr_obj.ATTR_ID"
                    + " AND atr_val.OBJECT_ID = entity_obj.OBJECT_ID";
    
    /**
     * You must have 3 parameters for PreparedStatement: <br>
     * OBJECT_TYPE_ID of container<br>
     * OBJECT_ID of owner<br>
     * OBJECT_TYPE_ID of container
     *
     * */

    public static  final String FIND_ALL_ENTITIES_BY_TYPE=
            "SELECT entity_obj.OBJECT_ID, atr_obj.NAME, atr_val.VALUE " +
                    "FROM OBJECTS entity_obj, ATTRIBUTES atr_obj, ATTRIBUTES_VALUE atr_val " +
                    "WHERE " +
                    "entity_obj.OBJECT_TYPE_ID =? " +
                    "AND atr_obj.OBJECT_TYPE_ID = entity_obj.OBJECT_TYPE_ID " +
                    "AND atr_val.ATTR_ID = atr_obj.ATTR_ID " +
                    "AND atr_val.OBJECT_ID = entity_obj.OBJECT_ID";

    public static final String FIND_CONTAINER_BY_OWNER_ID = 
            "SELECT child_obj.OBJECT_ID " +
                    "    FROM OBJECTS child_obj, OBJECTS parent_obj " +
                    "    WHERE child_obj.PARENT_ID = parent_obj.OBJECT_ID " +
                    "        and child_obj.OBJECT_TYPE_ID = ? " + // container object type
                    "        and child_obj.PARENT_ID = ? " + // owner object id
                    "        and  parent_obj.OBJECT_TYPE_ID = ? "; // owner object type

    
    /** See this {@link #FIND_CONTAINER_BY_OWNER_ID} */
    public static final String FIND_ALL_IN_CONTAINER_BY_OWNER_ID = 
            "SELECT entities_obj.OBJECT_ID"
                    + " FROM OBJECTS entities_obj" 
                    + " WHERE" 
                    + " entities_obj.PARENT_ID =" 
                    + "    (" + FIND_CONTAINER_BY_OWNER_ID + ")";
   
    /**
     * This query allows to get any Entity by OBJECT_TYPE_ID which locate in any container 
     * like hold, stock, ship by OBJECT_ID of this container.
     * Call execute() or query() must have 4 parameters for PreparedStatement: OBJECT_TYPE_ID, OBJECT_ID of Container, OBJECT_TYPE_ID, OBJECT_ID of Container 
     * */
    public static final String GET_ENTITIES_FROM_CONTAINER = 
            "SELECT entity_obj.OBJECT_ID, atr_temp.NAME, atr_val.VALUE "
                    + "FROM OBJECTS entity_obj, ATTRIBUTES atr_temp, OBJECTS entity_templ, ATTRIBUTES_VALUE atr_val"
                    + " WHERE"
                    + "    entity_obj.OBJECT_TYPE_ID = ?"//type id entity"
                    + " AND entity_obj.PARENT_ID = ?"// id obj container like hold"
                    + " AND entity_templ.OBJECT_ID = entity_obj.SOURCE_ID"

                    + " AND atr_temp.OBJECT_TYPE_ID = entity_templ.OBJECT_TYPE_ID"
                    + " AND atr_val.ATTR_ID = atr_temp.ATTR_ID"
                    + " AND atr_val.OBJECT_ID = entity_templ.OBJECT_ID"

                    + " UNION"
                    + " SELECT entity_obj.OBJECT_ID, atr_obj.NAME, atr_val.VALUE"
                    + " FROM OBJECTS entity_obj, ATTRIBUTES atr_obj, ATTRIBUTES_VALUE atr_val"
                    + " WHERE"
                    + "    entity_obj.OBJECT_TYPE_ID = ?"// obj type id
                    + " AND entity_obj.PARENT_ID = ?"// id obj container
                    + " AND atr_obj.OBJECT_TYPE_ID = entity_obj.OBJECT_TYPE_ID"
                    + " AND atr_val.ATTR_ID = atr_obj.ATTR_ID"
                    + " AND atr_val.OBJECT_ID = entity_obj.OBJECT_ID";

    public static final String GET_NEXTVAL = "SELECT NEXTVAL('obj_sq')";
    
    /**
     * To get value from field "OBJECT_ID" of "OBJECTS" table
     * This query allows to check is the correct parameter id of some object you have. 
     * You must have 2 parameters for PreparedStatement: OBJECT_ID and OBJECT_TYPE_ID. 
     * */
    public static final String CHECK_OBJECT =
            "SELECT OBJECT_ID"
                    + " FROM OBJECTS"
                    + " WHERE OBJECT_ID = ? AND OBJECT_TYPE_ID = ?";
    
    /**
     * INSERT data to the OBJECTS table.
     * You must have 5 parameters for PreparedStatement:<br> 
     * OBJECT_ID - new object id from seq.nextval call.
     * PARENT_ID(can be null),<br> 
     * OBJECT_TYPE_ID, <br>
     * SOURCE_ID(Template_id),<br>
     * Template_id,<br>
     * ATTR_ID of your template object curName (!NOT template OBJECT_TYPE curName!)
     * */
    public static final String CREATE_NEW_ENTITY =
            "INSERT INTO OBJECTS (OBJECT_ID, PARENT_ID, OBJECT_TYPE_ID, SOURCE_ID, NAME)"
                    + " VALUES (?, ?, ?, ?, "
                    + "(SELECT VALUE FROM ATTRIBUTES_VALUE"
                    + " WHERE OBJECT_ID = ? AND ATTR_ID = ?))";
    
    /**
     * INSERT data to the OBJECTS table.
     * You must have 3 parameters for PreparedStatement:<br> 
     * OBJECT_ID - new object id from seq.nextval call.
     * PARENT_ID(can be null),<br> 
     * OBJECT_TYPE_ID, <br>
     * OBJECT_TYPE_ID.
     * */
    public static final String CRATE_NEW_CONTAINER = 
            "INSERT INTO OBJECTS (OBJECT_ID, PARENT_ID, OBJECT_TYPE_ID, SOURCE_ID, NAME)"
                    + " VALUES (?, ?, ?, null,"
                    + " (SELECT NAME FROM OBJTYPE WHERE OBJECT_TYPE_ID = ?))";

    /**
     * This query allows to update any Attributes_Value by OBJECT_ID
     * Call update() must have 5 parameters for PreparedStatement:
     * Your new value, ATTRIBUTE_ID, OBJECT_ID, OBJECT_TYPE_ID,OBJECT_ID
     * */
    public static final String UPDATE_ONE_ATTRIBUTE_VALUE =
            "UPDATE  attributes_value" +
            "SET VALUE = ?" +
            "WHERE attributes_value.attr_id = ?" +
            "AND attributes_value.object_id = ?" +
            "AND EXISTS (" +
            "SELECT *" +
            "FROM objects o" +
            "WHERE o.object_type_id = ?" +
            "AND o.object_id = ?)";
    public static final String DELETE_OBJECT = "DELETE FROM objects WHERE object_id = ? AND object_type_id = ?";
    
    /** Put entity to container with checking if the correct parameter id of container you have.<br>
     * PreparedStatement args order:<br>
     * OBJECT_ID of container<br>
     * OBJECT_ID of entity<br>
     * OBJECT_ID of container<br>
     * OBJECT_ID of container<br>
     * OBJECT_TYPE_ID of container<br>
     * */
    public static final String PUT_ENTITY_TO_CONTAINER = 
            "UPDATE OBJECTS SET PARENT_ID = ?"
                    + " WHERE OBJECT_ID = ? AND ? ="
                    + "     (" + CHECK_OBJECT + ")";
    
    /**
     * This query allows to get value of specified attribute of given entity.
     * PreparedStatement args:
     * OBJECT_ID of entity
     * ATTR_ID of attribute
     */
    public static final String GET_ATTR_VALUE =
            "SELECT value FROM attributes_value WHERE object_id = ? and attr_id = ? ";

    /**
     * This query allows to get Attributes_Value by Reference table
     * You must have 4 parameters for PreparedStatement:
     * OBJECT_TYPE_ID of entity
     * OBJECT_ID of entity
     * OBJECT_TYPE_ID of reference
     * ATTR_ID of attributes in etity
     * */
    public static final String FIND_ATTR_BY_REF =
            "SELECT obj_ref.object_id " +
                    "FROM objects obj, objtype obj_type, " +
                    "attributes attr, " +
                    "objects obj_ref, objtype obj_ref_type, " +
                    "objreference table_ref " +
                    "WHERE " +
                    "obj_type.object_type_id = ? " +
                    "AND obj.object_id = ? " +
                    "AND obj.object_type_id  = obj_type.object_type_id " +
                    "AND obj_ref_type.object_type_id = ? " +
                    "AND obj_ref.object_type_id = obj_ref_type.object_type_id " +
                    "AND attr.attr_id = ? " +
                    "AND table_ref.object_id = obj.object_id " +
                    "AND table_ref.reference = obj_ref.object_id ";


    public static final String FIND_ATTRS_BY_ATTR_ID =
            "SELECT obj.object_id shipObject_id,attr.name, obj_ref.name  " +
                    "FROM objects obj, objtype obj_type, " +
                    "attributes attr, " +
                    "objects obj_ref, objtype obj_ref_type, " +
                    "objreference table_ref " +
                    "WHERE " +
                    "obj_type.object_type_id = ? " +
                    "AND obj.object_type_id  = obj_type.object_type_id " +
                    "AND obj_ref_type.object_type_id = ? " +
                    "AND obj_ref.object_type_id = obj_ref_type.object_type_id " +
                    "AND attr.attr_id = ? " +
                    "AND table_ref.object_id = obj.object_id " +
                    "AND table_ref.reference = obj_ref.object_id";

    /**
     * This query allows to get fleet speed of player by his id.
     * PreparedStatement args:<br>
     * OBJECT_ID id player <br>
     * OBJECT_TYPE_ID id of type mast<br>
     * ATTR_ID mast current speed attr id<br>
     */
    public static final String GET_FLEET_SPEED =
            "SELECT min(fleet_speed) FROM (SELECT sum(CAST (speed_val.VALUE AS Integer)) as fleet_speed"
                    + " FROM ATTRIBUTES_VALUE speed_val, OBJECTS mast,"
                    + " OBJECTS ship, OBJECTS player" 
                    + " WHERE"
                    + " player.OBJECT_ID = ?" // id player
                    + " AND ship.PARENT_ID = player.OBJECT_ID"
                    + " AND mast.PARENT_ID = ship.OBJECT_ID" 
                    + " AND mast.OBJECT_TYPE_ID = ?"//mast obj type
                    + " AND speed_val.ATTR_ID = ?" // mast current speed
                    + " AND speed_val.OBJECT_ID = mast.OBJECT_ID"
                    + " GROUP BY ship.OBJECT_ID) AS all_speeds"; //as speeds";
    
    /**
     * This query allows to get faster ship speed of player by his id.
     * PreparedStatement args:<br>
     * OBJECT_ID id player <br>
     * OBJECT_TYPE_ID id of type mast<br>
     * ATTR_ID mast current speed attr id<br>
     */
    public static final String GET_HIGHEST_SPEED =
            "SELECT max(fleet_speed) FROM (SELECT sum(CAST (speed_val.VALUE AS Integer)) as fleet_speed"
                    + " FROM ATTRIBUTES_VALUE speed_val, OBJECTS mast,"
                    + " OBJECTS ship, OBJECTS player" 
                    + " WHERE"
                    + " player.OBJECT_ID = ?" // id player
                    + " AND ship.PARENT_ID = player.OBJECT_ID"
                    + " AND mast.PARENT_ID = ship.OBJECT_ID" 
                    + " AND mast.OBJECT_TYPE_ID = ?"//mast obj type
                    + " AND speed_val.ATTR_ID = ?" // mast current speed
                    + " AND speed_val.OBJECT_ID = mast.OBJECT_ID"
                    + " GROUP BY ship.OBJECT_ID) AS all_speeds";// as speeds";
    
    /**
     * This query allows to get max shot dist of ship by his id.
     * PreparedStatement args:<br>
     * OBJECT_ID id ship <br>
     * ATTR_ID cannon distance attr id<br>
     */
    public static final String GET_MAX_SHOT_DISTANCE = 
            " SELECT max(CAST (dist_val.VALUE AS Integer)) shot_dist"
                    + " FROM ATTRIBUTES_VALUE dist_val, OBJECTS cannon,"
                    + " OBJECTS ship, OBJECTS cannon_tem"
                    + " WHERE"
                    + " ship.OBJECT_ID = ?" // ship id
                    + " AND cannon.PARENT_ID = ship.OBJECT_ID"
                    + " AND cannon.PARENT_ID = ship.OBJECT_ID"
                    + " AND cannon_tem.OBJECT_ID = cannon.SOURCE_ID"
                    + " AND dist_val.ATTR_ID = ?"//distance
                    + " AND dist_val.OBJECT_ID = cannon_tem.OBJECT_ID";
    
    /**
     * This query allows to get speed of ship by his id.
     * PreparedStatement args:<br>
     * OBJECT_ID id ship <br>
     * OBJECT_TYPE_ID id of type ship<br>
     * OBJECT_TYPE_ID id of type mast<br>
     * ATTR_ID mast current speed attr id<br>
     */
    public static final String GET_CURRENT_SPEED =
            "SELECT sum(speed_val.VALUE::NUMERIC::INTEGER) as speed"
            + " FROM ATTRIBUTES_VALUE speed_val, OBJECTS mast,"
                    + " OBJECTS ship"
                    + " WHERE"
                    + " ship.OBJECT_ID = ?"
                    + " AND ship.OBJECT_TYPE_ID = ?"
                    + " AND mast.PARENT_ID = ship.OBJECT_ID"
                    + " AND mast.OBJECT_TYPE_ID = ?"
                    + " AND speed_val.ATTR_ID = ?"
                    + " AND speed_val.OBJECT_ID = mast.OBJECT_ID";

    public static final String GET_SHIP_DAMAGE =
            "SELECT SUM(CAST (damage_value.VALUE AS Integer)) as damage"
                +" FROM ATTRIBUTES_VALUE damage_value, OBJECTS cannon,"
                +" OBJECTS ship"
                +" WHERE"
                +" damage_value.ATTR_ID = ?"
                +" AND damage_value.OBJECT_ID = cannon.SOURCE_ID"
                +" AND cannon.PARENT_ID = ship.OBJECT_ID"
                +" AND ship.OBJECT_TYPE_ID = ?"
                +" AND ship.OBJECT_ID = ?";

    /**
     * This query allows to get source of object by object_id.
     * PreparedStatement args:<br>
     * OBJECT_ID id ship <br>
     */

    public static final String GET_SOURCE_ID =
            "SELECT entity_obj.SOURCE_ID "
            + "FROM OBJECTS entity_obj "
            + "WHERE entity_obj.OBJECT_ID = ? "
            + "AND entity_obj.OBJECT_TYPE_ID = ?";
    
    /**
     * This query returns hold's !free! space on ship.<br>
     * You need 2 parametrs for prepared statements.<br>
     * 1. Ship object id.<br>
     * 2. Hold object type id.<br> 
     */
    public static final String GET_OCUPATED_VOLUME_GOODS_AMMO = 
            "SELECT COALESCE(sum(CAST (atr_v.VALUE AS Integer)) - count(atr_v.OBJECT_ID), 0) volume "
                    + " FROM attributes_value atr_v"
                    + " WHERE atr_v.OBJECT_ID in "
                    + " (SELECT cargo.OBJECT_ID FROM objects cargo WHERE PARENT_ID = "
                    + "     (SELECT hold.OBJECT_ID "
                    + "      FROM objects hold "
                    + "      WHERE hold.PARENT_ID = ? AND hold.OBJECT_TYPE_ID = ?))"
                    + " and (atr_v.ATTR_ID = " + DatabaseAttribute.GOODS_QUANTITY 
                    + " OR atr_v.ATTR_ID = " + DatabaseAttribute.AMMO_NUM + ")";
    
    public static final String GET_COUNT_OF_ENTITY_FROM_CONTAINER = 
            "SELECT entity_obj.NAME, count(entity_obj.OBJECT_ID)"
                    + " FROM OBJECTS entity_obj"
                    + " WHERE"
                    + " entity_obj.OBJECT_TYPE_ID = ?"
                    + " AND entity_obj.PARENT_ID = ?"
                    + " GROUP BY entity_obj.NAME";

    public static final String GET_COUNT_OF_ALL_ENTITY_FROM_CONTAINER =
            "SELECT count(entity_obj.OBJECT_ID)"
                    + " FROM OBJECTS entity_obj"
                    + " WHERE"
                    + " entity_obj.OBJECT_TYPE_ID = ?"
                    + " AND entity_obj.PARENT_ID = ?";
    
    public static final String GET_TEMPLATE_ID = "SELECT source_id FROM objects WHERE OBJECT_ID = ?";

    public static final String getCallFunctionQuery(String functionName, int paramNumber){
        String query = "SELECT * FROM \"%s\" (%s)";
        return getCallQuery(query, functionName, paramNumber);
    }
    
    private static final String getCallQuery(String query, String functionName, int paramNumber){
        String questionMark = "?";
        String comma = ",";
        String params = "";
        for (int i = 0; i < paramNumber; i++) {
            if (i > 0) {
                params += comma;
            }
            params += questionMark;
        }
        return String.format(query, functionName.toLowerCase(), params);
    }

}

