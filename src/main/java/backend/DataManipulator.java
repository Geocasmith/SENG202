package backend;

import java.util.ArrayList;
import java.util.Objects;

/**
 * DataManipulator class owns Record, writer and reader classes
 * It includes methods for adding, editing and removing, records
 */

public class DataManipulator {
private ArrayList<Record> currentData;

    /**
     * Constructor for DataManipulator class
     * @param currentData
     */
    DataManipulator(ArrayList<Record> currentData) {
    this.currentData = currentData;
}

    /**
     * This method checks if a record has a unique entry
     * It may need to be replaced as it is an expensive call
     * @param line Integer
     * @param data Record
     * @return true if Record case number is unique or false otherwise
     */
    public boolean hasUniqueCaseNumber(int line, Record data)
{
    for (int i = 0; i < currentData.size(); i++) {
        if (Objects.equals(currentData.get(i).getCaseNumber(), data.getCaseNumber())) {
            if (i != line) {
                return false;
            }

        }
    }

    return true;
}

    /**
     *
     * @param line
     * @param data
     * @return true if data is edited or false otherwise
     */
    public boolean editLine(int line, Record data) {
    if (hasUniqueCaseNumber(line, data))
    {
        currentData.set(line, data);
        return true;
    }
    return false;
}

    /**
     *Adds a record into CurrentData
     * @param line
     * @param data
     * @return true if the record has been added or false otherwise
     */
    public boolean addLine(int line, Record data) {
    if (hasUniqueCaseNumber(line, data))
    {
        currentData.add(data);
        return true;
    }

    return false;
    }

    /**
     * Removes a record from list of records in currentData
     * @param line
     */
    public void deleteLine(int line) {
        currentData.remove(line);
    }

    /**
     *
     * @param col int
     * @return extractedCol
     */
    public ArrayList<Object> extractCol(int col)
    {
        int colSize = currentData.size();
        ArrayList<Object> extractedCol = new ArrayList<>();
        switch (col) {
            case 0:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getCaseNumber());
                }
                break;
            case 1:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getDate());
                }
                break;
            case 2:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getBlock());
                }
                break;
            case 3:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getIucr());
                }
                break;
            case 4:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getPrimaryDescription());
                }
                break;
            case 5:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getSecondaryDescription());
                }
                break;
            case 6:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getLocation());
                }
                break;
            case 7:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getArrest());
                }
                break;
            case 8:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getDomestic());
                }
                break;
            case 9:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getBeat());
                }
                break;
            case 10:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getWard());
                }
                break;
            case 11:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getFbicd());
                }
                break;
            case 12:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getXcoord());
                }
                break;
            case 13:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getYcoord());
                }
                break;
            case 14:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getLatitude());
                }
                break;
            case 15:
                for(int i = 0; i < colSize; i++) {
                    extractedCol.add(currentData.get(i).getLongitude());
                }
                break;
            default:
                break;
    }
    return extractedCol;
    }


    /**
     *
     * @param col1 int
     * @param col2 int
     * @return dataToGraph
     */
    public ArrayList<ArrayList<Object>> getDataToGraph(int col1, int col2)
    {
        ArrayList<ArrayList<Object> > dataToGraph = new ArrayList<ArrayList<Object>>();
        ArrayList<Object> dataCol1;
        ArrayList<Object> dataCol2;
        dataCol1 = extractCol(col1);
        dataCol2 = extractCol(col2);
        dataToGraph.add(dataCol1);
        dataToGraph.add(dataCol2);
        return dataToGraph;

    }
}
