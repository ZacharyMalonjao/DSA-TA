import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DsaMs1 {
    public static void main(String[] args) {
        // Inventory list
        List<String[]> inventory = new ArrayList<>();
        String file = "inventory.csv";
        
        // Load inventory from CSV
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                inventory.add(columns);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        printData(inventory);  

        Scanner sc = new Scanner(System.in);
        boolean isDone = false;

        while (!isDone) {
            System.out.println("----------------------------");
            System.out.println("Please input which number corresponds to the action you want to do:");
            System.out.println("1: Add");
            System.out.println("2: Search");
            System.out.println("3: Sort");
            System.out.println("4: Delete");
            System.out.println("5: Edit");
            System.out.println("6: Exit");
            System.out.print("Insert your number: ");
            
            String command = sc.nextLine(); // Read user input as a string

            switch (command) {
                case "1": // add
                    LocalDate date = LocalDate.now();
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    String[] newRow = new String[5];
                    
                    // initialize new stock with default values
                    // INSERT DATE
                    newRow[0] = date.format(f);
                    // INSERT STOCK LABEL
                    newRow[1] = "New";
                    // INSERT PURCHASE STATUS
                    newRow[4] = "On-hand";
                  
                    while (true) {    
                        System.out.print("Insert Engine Number: ");
                        String engineNumber = sc.nextLine();
                        if (engineNumber.trim().isEmpty()) {
                            System.out.println("Please input valid engine ID");
                        } else if (isEngineNumberFound(engineNumber, inventory)) {
                            System.out.println("Engine Number already exists, please try again");
                        } else {
                            newRow[3] = engineNumber; // engine id
                            break;
                        }
                    }   
                         
                    // INSERT BRAND
                    while (true) {
                        System.out.print("Insert Brand: ");
                        String brand = sc.nextLine();
                        if (brand.trim().isEmpty()) {
                            System.out.println("Please input valid Brand");
                        } else {
                            newRow[2] = brand;
                            break;
                        }
                    }    
                    
                    inventory.add(newRow);
                    printData(inventory);
                    updateCSV(inventory, file);
                    
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        for (String[] updatedInventory : inventory) {
                            writer.write(String.join(",", updatedInventory));
                            writer.newLine();
                        }
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "2": // search
                    System.out.print("Input a keyword in order to search for a specific entry ");
                    String toSearch = sc.nextLine().trim(); // Trim whitespace
                    boolean isFound = false;

                    for (String[] row : inventory) {
                        for (String e : row) {
                            if (e.equalsIgnoreCase(toSearch)) {
                                if (!isFound) {
                                    System.out.println("These are the following Stocks that contain '" + toSearch + "':");
                                    isFound = true;
                                }
                                System.out.println(String.join(",", row));
                                break; // Exit the inner loop once a match is found
                            }
                        }
                    }
                    if (!isFound) {
                        System.out.println("Not Found");
                    }
                    break;

                case "3": // sort
                    mergeSort(inventory);
                    printData(inventory);
                    break;

                case "4": // delete
                    System.out.print("Insert unique engine ID: ");
                    String engineNumber = sc.nextLine().trim(); // Trim whitespace
                    boolean found = false;

                    for (int i = 0; i < inventory.size(); i++) {
                        String[] row = inventory.get(i);
                        if (row.length > 3 && row[3].equalsIgnoreCase(engineNumber)) { // Check length and compare
                            found = true; // Mark as found

                            if (row[1].equalsIgnoreCase("old") && row[4].equalsIgnoreCase("sold")) {
                                boolean isValidChoice = false; // Initialize to false
                                while (!isValidChoice) { // Change to while (!isValidChoice)
                                    System.out.print("Do you wish to delete '" + String.join(",", row) + "'? (Yes or No) ");
                                    String decision = sc.nextLine();

                                    if (decision.equalsIgnoreCase("yes")) {
                                        inventory.remove(i);
                                        printData(inventory);
                                        updateCSV(inventory, file); // Update CSV after deletion
                                        System.out.println("Successfully Deleted");
                                        isValidChoice = true; // Set to true to exit the loop
                                    } else if (decision.equalsIgnoreCase("no")) {
                                        System.out.println("Deletion Cancelled...");
                                        isValidChoice = true; // Set to true to exit the loop
                                    } else {
                                        System.out.println("Please choose either yes or no");
                                    }
                                }
                            } else if (row[1].equalsIgnoreCase("new") || row[4].equalsIgnoreCase("on-hand")) {
                                System.out.println("You can only delete stocks that are Old and Sold");
                            }
                            break; // Exit the loop after processing the found item
                        }
                    }
                    if (!found) {
                        System.out.println("Not Found");
                    }
                    break;

                case "5": // edit
                    System.out.println("Input engine number of record you want to edit: ");
                    String idToEdit = sc.nextLine().trim(); // Trim whitespace

                    // Search it, and then decision if null or not
                    String[] record = searchRecord(idToEdit, inventory);
                    if (idToEdit.trim().isEmpty()) {
                        System.out.println("Record cannot have an empty ID");
                    } else if (record != null) {
                        boolean editing = true; // Flag to control the editing loop

                        while (editing) {
                            // Ask user which one they'd like to edit
                            System.out.println("Type which element you wish to edit: (Date, Label, Engine Number, Brand or Status)");
                            String elementToEdit = sc.nextLine().trim(); // Trim whitespace

                            switch (elementToEdit.toLowerCase()) {
                                case "engine number":
                                    while (true) {
                                        System.out.print("Insert new Engine Number: ");
                                        String newEngineNumber = sc.nextLine().trim(); // Get new input and trim

                                        // Check if the engine number is valid
                                        if (isEngineNumberFound(newEngineNumber, inventory)) {
                                            System.out.println("Engine Number already exists, please try again");
                                        } else if (newEngineNumber.trim().isEmpty()) {
                                            System.out.println("Engine number cannot be empty");
                                        } else {
                                            edit(idToEdit, 3, newEngineNumber, inventory);
                                            printData(inventory);
                                            System.out.println("Updated Successfully");
                                            break; // Exit the loop if the engine number is valid
                                        }
                                    }
                                    break;

                                case "date":
                                    // Implement date editing logic here
                                    while (true) {
                                        System.out.print("Insert new date (Please strictly input as mm/dd/yyyy): ");
                                        String newDate = sc.nextLine().trim(); // Trim whitespace
                                        if (isValidDate(newDate)) {
                                            edit(idToEdit, 0, newDate, inventory);
                                            printData(inventory);
                                            System.out.println("\nEdit successful");
                                            break;
                                        } else {
                                            System.out.println("Please strictly input as mm/dd/yyyy");
                                        }
                                    }
                                    break;

                                case "label":
                                    while (true) {                                
                                        if (record[1].equals("Old")) {
                                            edit(idToEdit, 1, "New", inventory);
                                            printData(inventory);
                                            System.out.println("\nLabel automatically swapped from Old to New");
                                            break;
                                        } else {
                                            edit(idToEdit, 1, "Old", inventory);
                                            printData(inventory);
                                            System.out.println("\nLabel automatically swapped from New to Old");
                                            break;
                                        }
                                    }
                                    break;

                                case "brand":
                                    while (true) {
                                        System.out.print("Insert new brand name: ");
                                        String newBrand = sc.nextLine().trim(); // Trim whitespace
                                        if (newBrand.trim().isEmpty()) {
                                            System.out.println("Please input valid brand name");
                                        } else {
                                            edit(idToEdit, 2, newBrand, inventory);
                                            printData(inventory);
                                            System.out.println("Update successful.");
                                            break;
                                        }
                                    }
                                    break;

                                case "status":
                                    while (true) {
                                        if (record[4].equals("On-hand")) {
                                            edit(idToEdit, 4, "Sold", inventory);
                                            printData(inventory);
                                            System.out.println("\nStatus automatically changed from On-hand to Sold ");
                                            break;
                                        } else {
                                            edit(idToEdit, 4, "On-hand", inventory);
                                            printData(inventory);
                                            System.out.println("\nStatus automatically changed from Sold to On-hand ");
                                            break;
                                        }
                                    }
                                    break;

                                default:
                                    System.out.println("Please input either Date, Label, Engine Number, Brand or Status");
                                    continue; // Continue the loop to ask for the element again
                            }

                            // If we reach here, it means a valid case was handled
                            editing = false; // Exit the editing loop
                        }
                    } else {
                        System.out.println("Record with " + idToEdit + " not found");
                    }
                    break;

                case "6":
                    isDone = true;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid command");
            }
        }
        sc.close();
    }

    // ----------------------------M E T H O D S -------------------------------------------------------------
    public static void printData(List<String[]> inventory) {
        for (String[] row : inventory) {
            System.out.println(String.join(",", row));
        }
    }

    public static boolean isEngineNumberFound(String engineNumber, List<String[]> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            String[] row = inventory.get(i);
            if (row[3].equals(engineNumber)) {            
                return true;
            }                
        }                
        return false;  
    }

    public static void updateCSV(List<String[]> inventory, String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String[] updatedInventory : inventory) {
                writer.write(String.join(",", updatedInventory));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mergeSort(List<String[]> list) {
        if (list.size() > 1) {
            // Find the middle of the list
            int mid = list.size() / 2;

            // Split the list into two halves
            List<String[]> leftHalf = new ArrayList<>(list.subList(0, mid));
            List<String[]> rightHalf = new ArrayList<>(list.subList(mid, list.size()));

            // Recursively sort both halves
            mergeSort(leftHalf);
            mergeSort(rightHalf);

            // Merge the sorted halves
            merge(list, leftHalf, rightHalf);
        }
    }

    // Method to merge two sorted Lists
    private static void merge(List<String[]> list, List<String[]> leftHalf, List<String[]> rightHalf) {
        int i = 0, j = 0, k = 0;

        // Merge the two halves into the original list
        while (i < leftHalf.size() && j < rightHalf.size()) {
            // Compare the third elements (index 2) of the arrays
            if (leftHalf.get(i)[2].compareTo(rightHalf.get(j)[2]) <= 0) {
                list.set(k++, leftHalf.get(i++));
            } else {
                list.set(k++, rightHalf.get(j++));
            }
        }

        // Copy any remaining elements from leftHalf
        while (i < leftHalf.size()) {
            list.set(k++, leftHalf.get(i++));
        }

        // Copy any remaining elements from rightHalf
        while (j < rightHalf.size()) {
            list.set(k++, rightHalf.get(j++));
        }
    }

    public static String[] searchRecord(String engineID, List<String[]> inventory) {
        for (String[] row : inventory) {
            // Check if the row has at least 4 elements to avoid ArrayIndexOutOfBoundsException
            if (row.length > 3 && row[3].equalsIgnoreCase(engineID)) {
                return row; // Return the row if the engineID matches the value at index 3
            }
        }
        return null; // Return null if no match is found
    }

    public static void edit(String engineID, int recordIndexToEdit, String replacedInfo, List<String[]> inventory) {
        for (String[] records : inventory) {
            if (engineID.equals(records[3])) {
                records[recordIndexToEdit] = replacedInfo;
                updateCSV(inventory, "inventory.csv");
                break;
            }
        }
    } 

    public static boolean isValidDate(String date) {
        // Regular expression to match mm/dd/yyyy format
        String regex = "^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);

        if (!matcher.matches()) {
            return false; // Format is incorrect
        }

        // Now check if the date is valid
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false); // Set lenient to false to strictly parse the date

        try {
            sdf.parse(date); // This will throw ParseException if the date is invalid
            return true; // Date is valid
        } catch (ParseException e) {
            return false; // Date is invalid
        }
    }
}