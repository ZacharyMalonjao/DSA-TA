import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private List<String[]> InventoryData; // Store data from CSV

    // Constructor to initialize and load data
    public CSVReader(String csvFile) {
        InventoryData = new ArrayList<>();
        loadDataFromCSV(csvFile);
    }

    // Method to load data from the CSV file
    private void loadDataFromCSV(String csvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                InventoryData.add(columns);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get employee data
    public List<String[]> getInventoryData() {
        return InventoryData;
    }
}
