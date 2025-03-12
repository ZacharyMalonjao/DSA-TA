
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
//comment
public class DsaMs1 {
    public static void main(String[] args) {
        // Inventory list
        
        
        List<String[]> inventory = new ArrayList<>();
    
            String file = "inventory.csv";
            try{
                BufferedReader reader = new BufferedReader(new FileReader(file));
                
                String line;
                while((line = reader.readLine())!=null){
                    
                  String[] columns = line.split(",");
                  inventory.add(columns);
                      
                }
                reader.close();
            }catch(IOException e){e.printStackTrace();}
          printData(inventory);  

     
        
        Scanner sc = new Scanner(System.in);
        boolean isDone = false;

        while (!isDone) {
            System.out.println("----------------------------");
            System.out.print("Add, Delete, Sort, Search, Edit or Exit? ");
            String command = sc.nextLine();

            switch (command.toLowerCase()) {
                case "add":
                    
                    LocalDate date = LocalDate.now();
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    
                    String[] newRow = new String[5];
                    //INSERT DATE
                        newRow[0] = date.format(f);
                    //INSERT STOCK LABEL
                        newRow[1] = "New";
                    //INSERT PURCHASE STATUS
                         newRow[4]="On-hand";
                     //INSERT ENGINE NUMBER    
                    while(true){    
                    System.out.print("Insert Engine Number: ");
                        String engineNumber = sc.nextLine();
                        if(isEngineNumberFound(engineNumber, inventory)){
                           System.out.println("Engine Number already exists, please try again");
                        }else{
                           newRow[3]=engineNumber;
                           break;
                        }
                    }   
                        
                    //INSERT BRAND
                    System.out.print("Insert Brand: ");
                        newRow[2] = sc.nextLine();
                   
                    
                  
                    inventory.add(newRow);
                    printData(inventory);
                    updateCSV(inventory, file);
                    
                    try{
                      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                      for(String[] updatedInventory:inventory){
                      writer.write(String.join(",",updatedInventory));
                      writer.newLine();
                    
                        }
                        writer.close();
                    }catch(IOException e){
                     e.printStackTrace();}
                           break;

                case "search":
                    System.out.print("Insert criteria you want to search: (it can be a date, stock label, brand, engine number or status)");
                    String toSearch = sc.nextLine();
                    boolean isFound = false;

                    for (String[] row : inventory) {
                        for (String e : row) {
                            if (e.equalsIgnoreCase(toSearch)) {
                                if (!isFound) {
                                    System.out.println("These are the following Stocks that contain '" + toSearch + "':");
                                    isFound = true;
                                }
                                System.out.println(String.join(",", row));
                                break;
                            }
                        }
                    }
                    if (!isFound) {
                        System.out.println("Not Found");
                    }
                    break;

                case "sort":             
                    mergeSort(inventory);
                    printData(inventory);
                    break;

             case "delete":
                System.out.print("Insert unique engine ID: ");
                String engineNumber = sc.nextLine();
                boolean found = false;

                for (int i = 0; i < inventory.size(); i++) {
                    String[] row = inventory.get(i);
                    if (row[3].equals(engineNumber)) {
                        found = true; // Mark as found

                        switch (row[1].toLowerCase()) {
                            case "old":
                                boolean isValidChoice = false; // Initialize to false
                                while (!isValidChoice) { // Change to while (!isValidChoice)
                                    System.out.print("Do you wish to delete '" + String.join(",", row) + "'? (Yes or No) ");
                                    String decision = sc.nextLine();

                                    switch (decision.toLowerCase()) {
                                        case "yes":
                                            inventory.remove(i);
                                            System.out.println("Successfully Deleted");
                                            printData(inventory);
                                            updateCSV(inventory, file); // Update CSV after deletion
                                            isValidChoice = true; // Set to true to exit the loop
                                            break; // Exit the inner switch
                                        case "no":
                                            System.out.println("Deletion Cancelled...");
                                            isValidChoice = true; // Set to true to exit the loop
                                            break; // Exit the inner switch
                                        default:
                                            System.out.println("Please choose either yes or no");
                                    }
                                }
                                break; // Add this break to prevent fall-through
                            case "new":
                                System.out.println("You cannot delete new Stocks.");
                                break; // Exit the delete case
                        }
                    }
                }
                if (!found) {
                    System.out.println("Not Found");
                }
                break; // This break is for the outer switch statement
                
                
           case "edit":
                // Ask input for ID to search
                System.out.println("Input engine number of record you want to edit: ");
                String idToEdit = sc.nextLine();

                // Search it, and then decision if null or not
                String[] record = searchRecord(idToEdit, inventory);
                if (record != null) {
                    boolean editing = true; // Flag to control the editing loop

                    while (editing) {
                        // Ask user which one they'd like to edit
                        System.out.println("Type which element you wish to edit: (Date, Label, Engine Number, Brand or Status)");
                        String elementToEdit = sc.nextLine();

                        switch (elementToEdit.toLowerCase()) {
                            case "engine number":
                                while (true) {
                                    System.out.print("Insert new Engine Number: ");
                                    String newEngineNumber = sc.nextLine(); // Get new input

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
                                while(true){
                                    System.out.print("Insert new date (Please strictly input as mm/dd/yyyy)");
                                    String newDate = sc.nextLine();
                                    if(isValidDate(newDate)){
                                        edit(idToEdit, 0, newDate, inventory);
                                        printData(inventory);
                                        System.out.println("\nEdit successful");
                                        break;
                                    }else{
                                         System.out.println("Please strictly input as mm/dd/yyyy");
                                    }
                                
                                }
                                
                                break;
                            case "label":
                               while(true){                                
                                   if(record[1].equals("Old")){
                                       edit(idToEdit, 1, "New", inventory);
                                        printData(inventory);
                                        System.out.println("\nLabel automatically swapped from Old to New");
                                      
                                       break;
                                   }else /*(record[1].equals("New"))*/{
                                       edit(idToEdit, 1, "Old", inventory);
                                        printData(inventory);
                                        System.out.println("\nLabel automatically swapped from New to Old");
                                       break;
                                   }
                               }
                                break;
                            case "brand":
                               
                                while(true){
                                    System.out.print("Insert new brand name: ");
                                    String newBrand = sc.nextLine();
                                
                                    if(newBrand.trim().isEmpty()){
                                        System.out.println("Please input valid brand name");
                                    }else{
                                        edit(idToEdit, 2, newBrand, inventory);
                                        printData(inventory);
                                        System.out.println("Update successful.");
                                        break;
                                    }
                                }
                                break;
                            case "status":
                                while(true){
                                    if(record[4].equals("On-hand")){
                                        edit(idToEdit, 4, "Sold", inventory);
                                        printData(inventory);
                                        System.out.println("\nStatus automaticall changed from On-hand to Sold ");
                                        break;
                                    }else{
                                        edit(idToEdit, 4, "On-hand", inventory);
                                        printData(inventory);
                                        System.out.println("\nStatus automaticall changed from Sold to On-hand ");
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
                } else if (idToEdit.trim().isEmpty()) {
                    System.out.println("Record cannot have an empty ID");
                } else {
                    System.out.println("Record with " + idToEdit + " not found");
                }
                break;
             case "exit":
                    isDone = true;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid command");
            }
        }
        sc.close();
        
       
           
    }
//----------------------------M E T H O D S -------------------------------------------------------------
    public static void printData(List<String[]> inventory) {
        for (String[] row : inventory) {
            System.out.println(String.join(",", row));
        }
    }
    
    public static boolean isEngineNumberFound(String engineNumber, List<String[]> inventory ){
         for (int i = 0; i < inventory.size(); i++) {
            String[] row = inventory.get(i);
            if (row[3].equals(engineNumber)) {            
                return true;
            }                
        }                
         return false;
                
    }
    
    public static void updateCSV(List<String[]> inventory, String file){
                     try{
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        for(String[] updatedInventory:inventory){
                             writer.write(String.join(",",updatedInventory));
                             writer.newLine();

                        }
                        writer.close();
                    }catch(IOException e){
                       e.printStackTrace();}
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
  public static void edit(String engineID, int recordIndexToEdit, String replacedInfo, List<String[]> inventory){
      for(String[] records: inventory){
          if(engineID.equals(records[3])){
              records[recordIndexToEdit]= replacedInfo;
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


