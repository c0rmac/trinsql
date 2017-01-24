package com.trinitcore.sql;

import com.trinitcore.sql.queryObjects.returnableQueries.Select;

import java.util.*;

/**
 * Created by cormacpjkinsella on 10/12/16.
 */
public class Association {
    public String parentColumn; public Select parentTable; public String childColumn; public Select childTable; public String name; public boolean forceArray; boolean rearrangeAssociationsByChildTableCount; boolean reverseRearrangement;

    public Association(String parentColumn, Select parentTable, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount, boolean reverseRearrangement) {
        this.parentColumn = parentColumn; this.parentTable = parentTable; this.childColumn = childColumn; this.childTable = childTable; this.name = name; this.forceArray = forceArray; this.rearrangeAssociationsByChildTableCount = rearrangeAssociationsByChildTableCount;
        this.reverseRearrangement = reverseRearrangement;

    }

    public void rearrangeAssociationsByChildTableCount() {
        if (reverseRearrangement) Arrays.sort(parentTable.getRows(),Collections.reverseOrder());
        else Arrays.sort(parentTable.getRows());
    }

    public List<Row> rowsWithAssociations() {
        List<Row> rows = new ArrayList<>();
        for (Row parentRow: parentTable.getRows()) {
            if (parentRow.containsAssociation) {
                rows.add(parentRow);
            }
        }
        return rows;
    }

    public void process() {
        System.out.println("Processing: "+parentTable.getRows().length);
        for (Row parentRow : parentTable.getRows()) {
            childTable.reset(false);
            Row[] relevantRows = childTable.getRowsWhere(childColumn,parentRow.get(parentColumn));
            if (relevantRows.length == 1 && !forceArray) {
                // Single object row
                parentRow.put(name, relevantRows[0]);
                parentRow.associationColumn = name;
                parentRow.containsAssociation = true;
            } else if (relevantRows.length > 1 || forceArray) {
                // Array objects row
                parentRow.put(name, relevantRows);
                parentRow.associationColumn = name;
                parentRow.containsAssociation = true;
            }

            /*
            int childRowsCount = 0;
            for (Row childRows : childTable.getRows()) {
                if (childRows.get(childColumn).equals(parentRow.get(parentColumn))) {
                    parentRow.put(name,childRows);
                    childRowsCount++;
                    // break;
                }
            }
            // Use an array
            if (childRowsCount >= 2) {
                // childTable.resetWhere();
                // childTable.where(childColumn,parentRow.get(parentColumn));
                // parentRow.put(name, childTable.getRows());
                // childTable.reset(false);
                parentRow.put(name,childTable.getRowsWhere(childColumn,parentRow.get(parentColumn)));
                // break;
                System.out.println("Dumped array");
            }
            */
        }
        if (rearrangeAssociationsByChildTableCount) {
            rearrangeAssociationsByChildTableCount();
        }
    }

}
