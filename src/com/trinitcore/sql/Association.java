package com.trinitcore.sql;

import com.trinitcore.sql.queryObjects.returnableQueries.Select;

import java.util.*;

/**
 * Created by cormacpjkinsella on 10/12/16.
 */
public class Association {

    public interface Listener {
        public void associatingTableDidChange();
    }

    public String parentColumn; public Select parentTable; public String childColumn; public Select childTable; public String name; public boolean forceArray = false; public boolean rearrangeAssociationsByChildTableCount = false; public boolean reverseRearrangement = false;
    public boolean counter; public boolean matchingDataBoolean;

    public Association(Association.Listener listener, String parentColumn, Select parentTable, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount, boolean reverseRearrangement, boolean useCounter, boolean useMatchingDataBoolean) {
        this.parentColumn = parentColumn; this.parentTable = parentTable; this.childColumn = childColumn; this.childTable = childTable; this.name = name; this.forceArray = forceArray; this.rearrangeAssociationsByChildTableCount = rearrangeAssociationsByChildTableCount; this.counter = useCounter;
        this.reverseRearrangement = reverseRearrangement; this.matchingDataBoolean = useMatchingDataBoolean;

        this.childTable.setMasterTableListener(listener);
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
        // System.out.println("Processing: "+parentTable.getRows().length);
        //childTable.resetWhere();
        childTable.reset(true);
        // childTable.resetLimit();
        for (Row parentRow : parentTable.getRows()) {
            Row[] relevantRows = childTable.getRowsWhere(childColumn,parentRow.get(parentColumn));
            // System.out.println(parentColumn + " --> " + childColumn + " :: " + parentRow.get(parentColumn) + " :: " + relevantRows.length);
            if (counter) {
                parentRow.put(name, relevantRows.length);
            } else if (matchingDataBoolean) {
                if (relevantRows.length != 0) parentRow.put(name, true);
                else parentRow.put(name,false);
            } else {
                if (relevantRows.length == 1 && !forceArray) {
                    // Single object row
                    parentRow.put(name, relevantRows[0]);
                } else if (relevantRows.length > 1 || forceArray) {
                    // Array objects row
                    parentRow.put(name, relevantRows);
                }
            }
            parentRow.associationCounter = counter;
            parentRow.associationMatchingDataBoolean = matchingDataBoolean;
            parentRow.associationColumn = name;
            parentRow.containsAssociation = true;
            parentRow.associationRowCount = relevantRows.length;
        }
        if (rearrangeAssociationsByChildTableCount) {
            rearrangeAssociationsByChildTableCount();
        }
    }

}
