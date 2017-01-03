package com.trinitcore.sql;

import com.trinitcore.sql.queryObjects.returnableQueries.Select;

/**
 * Created by cormacpjkinsella on 10/12/16.
 */
public class Association {
    public String parentColumn; public Select parentTable; public String childColumn; public Select childTable; public String name;

    public Association(String parentColumn, Select parentTable, String childColumn, Select childTable, String name) {
        this.parentColumn = parentColumn; this.parentTable = parentTable; this.childColumn = childColumn; this.childTable = childTable; this.name = name;

    }

    public void process() {
        System.out.println("Proccessing");
        for (Row parentRow : parentTable.getRows()) {
            int childRowsCount = 0;
            for (Row childRows : childTable.getRows()) {
                if (childRows.get(childColumn).equals(parentRow.get(parentColumn))) {
                    parentRow.put(name,childRows);
                    childRowsCount++;
                }
            }
            if (childRowsCount >= 2) {
                // childTable.resetWhere();
                // childTable.where(childColumn,parentRow.get(parentColumn));
                //childTable.reset(false);
                parentRow.put(name,childTable.getWhere(childColumn,parentRow.get(parentColumn)));
                // break;
                System.out.println("Dumped array");
            }
        }
    }

}
