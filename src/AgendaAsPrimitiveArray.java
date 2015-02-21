import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import sun.awt.datatransfer.DataTransferer;


/**
 * Created by condor on 10/02/15.
 * FastTrackIT, 2015
 *
 * DEMO ONLY PURPOSES, IT MIGHT CONTAINS INTENTIONALLY ERRORS OR ESPECIALLY BAD PRACTICES
 */
public class AgendaAsPrimitiveArray {

    private final static int MAX_AGENDA_ITEMS=100;
    private Item[] agenda = new Item[MAX_AGENDA_ITEMS];
    private int currentAgendaIndex;

    public static void main(String[] args) {
        AgendaAsPrimitiveArray m = new AgendaAsPrimitiveArray();
        m.loadFromFile() ;
        System.out.println("AgendaTa versiunea 1.0");

        do {
            m.printMenu();
            int option = m.readMenuOption();
            switch (option) {
                case 1:
                    m.listAgenda();
                    break;
                case 2:
                    m.searchAgendaAndDisplay1();
                    break;
                case 3:
                    m.createItem();
                    break;
                case 4:
                    m.updateItem();
                    break;
                case 5:
                    m.deleteItem();
                    break;
                case 6:
                    m.readFromFile();
                    break;
                case 7:
                    m.writeToFile();
                    break;
                case 8:
                    m.sortListAgenda() ;
                    break;
                case 9:
                    m.longestItem();
                    break;
                case 10:
                    m.reportAgenda() ;
                    break;
                case 11:
                    m.deleteAllItem() ;
                    break;
                case 20:
                    m.exitOption();
                    break;
                default:
                    m.defaultOption();
                    break;
            }
        } while (true);

    }

    private void createItem() {
        boolean wasInserted = false;
        HandleKeyboard handleKeyboard = new HandleKeyboard().invokeItem();
        String firstName = handleKeyboard.getFirstName();
        String lastName = handleKeyboard.getLastName();
        String phone = handleKeyboard.getPhone();
        currentAgendaIndex = agenda.length;
        Item item = new Item();
        item.setFirstName(firstName);
        item.setLastName(lastName);
        item.setPhoneNumber(phone);

        if(currentAgendaIndex<MAX_AGENDA_ITEMS) {
            agenda[currentAgendaIndex] = item;
            currentAgendaIndex++;
             wasInserted = true;
        }
        else {
            //try to find null slots and add th item in the first null slot
            System.out.println("debug: try to find slots");
            for (int i = 0; i < agenda.length; i++) {
                if (agenda[i] == null) { // found one
                    agenda[i]=item;
                    wasInserted=true;
                    System.out.println("debug: slot found, inserted ok");
                    break;
                }
            }
        }
        if(wasInserted) {
            saveToFile();
            System.out.println("Item was added");
        }
        else
            System.out.println("Memory full! The item cannot be added");
    }


    private void updateItem() {
        //search and if found do an update
        int indexItem = searchAgenda();
        if (indexItem != -1) { //found
            HandleKeyboard handleKeyboard = new HandleKeyboard().invokeItem();
            String firstName = handleKeyboard.getFirstName(); // so we can change the name as well
            String lastName = handleKeyboard.getLastName();
            String phone = handleKeyboard.getPhone();

            Item i = new Item();
            i.setFirstName(firstName);
            i.setLastName(lastName);
            i.setPhoneNumber(phone);
            agenda[indexItem] = i;
            saveToFile() ;
            System.out.println("Item was updated!");
        } else {
            System.out.println("You cannot update an item that does not exists in agenda!");
        }

    }


    private void deleteItem() {
        //search and if found delete it and null the position
        int indexItem = searchAgenda();
        if (indexItem != -1) { //found
            agenda[indexItem] = null;
            saveToFile() ;
            System.out.println("Item was deleted!");
        } else {
            System.out.println("Item not found, so you cannot delete it!");
        }

    }


    private void deleteAllItem() {
        //search and if found delete it and null the position
        HandleKeyboard handleKeyboard = new HandleKeyboard().invokeDeleteYesNo();
        String yesNo = handleKeyboard.getYesNo();
        if(yesNo.equalsIgnoreCase("Y")) {
            Arrays.fill(agenda,null);
            try{
                File file = new File("agenda.txt");
                if(file.delete()){
                    System.out.println(file.getName() + " is deleted!");
                }else{
                    System.out.println("Delete operation is failed.");
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }


    /* returns the index where the name was found or -1 if the name is not in the agenda*/
    private int searchAgenda() {
        HandleKeyboard handleKeyboard = new HandleKeyboard().invokeItemFirstName();
        String firstName = handleKeyboard.getFirstName();
        int indexWhereItWasFound = -1;

        // for (Item anAgenda : agenda) might not work here , we need the index so I keep the original form of for
        for (int i = 0; i < agenda.length; i++) {
            if (agenda[i] != null) {
                Item item = agenda[i];
                String nameInAgenda = item.getFirstName();
                if (firstName.equalsIgnoreCase(nameInAgenda)) {
                    indexWhereItWasFound = i;
                    break;
                }
            }
        }
        return indexWhereItWasFound;
    }

    /* returns the index where the name was found or -1 if the name is not in the agenda */
    private void searchAgendaAndDisplay() {
        int index = searchAgenda();
        if (index != -1) { //found
            Item item = agenda[index];
            String firstName = item.getFirstName();
            String lastName = item.getLastName();
            String phoneNumber = item.getPhoneNumber();
            System.out.println("FirstName:" + firstName);
            System.out.println("LastName:" + lastName);
            System.out.println("Phone Number:" + phoneNumber);
        } else {
            System.out.println("This name does not exists in agenda!");
        }
    }

    private void searchAgendaAndDisplay1() {
        HandleKeyboard handleKeyboard = new HandleKeyboard().invokeItemStrSearch();
        String strSearch = handleKeyboard.getStrSearch() ;
        ArrayList<String>  listFind = new ArrayList<String>() ;

        // for (Item anAgenda : agenda) might not work here , we need the index so I keep the original form of for
        for (Item s:  agenda) {
            if (s != null) {
                if (getItem(s).toLowerCase().contains(strSearch.toLowerCase())) {
                    listFind.add(getItem(s)) ;
                }
            }
        }
        for(int i = 0; i < listFind.size(); i++) {
           System.out.print((i+1)+". " + listFind.get(i)+"\n");
        }
        System.out.print("-------------------------\n");



    }


    private void listAgenda() {

        int emptySpaces = 0;
        int i=0;
        //System.out.println("agenda.length = " + agenda.length); //sout tab, or soutv tab, or soutm tab
        System.out.println("Your Agenda:");
        for (Item anAgenda : agenda) {
            i++;
            if (anAgenda != null) {
                String firstName = anAgenda.getFirstName();
                String lastName = anAgenda.getLastName();
                String telephone = anAgenda.getPhoneNumber();
                System.out.println(i + ". " + firstName + " " + lastName  + '\t'+ telephone);
            } else {
                emptySpaces++;
            }
        }
       // System.out.println("empty spaces:" + emptySpaces);
        System.out.println("---------------");
    }

    private void sortListAgenda() {
        int emptySpaces = 0;
        int i=0;
        //System.out.println("agenda.length = " + agenda.length); //sout tab, or soutv tab, or soutm tab
        System.out.println("Your Sort Agenda:");
        ArrayList<Item> sortAgenda = new ArrayList<Item>() ;
        for (Item anAgenda : agenda)  sortAgenda.add(anAgenda);


        Collections.sort(sortAgenda, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1!=null & o2!=null) {
                    System.out.println(o1.getFirstName() + "\n" + o2.getFirstName());
                    return o1.getFirstName().compareTo(o2.getFirstName()) ;
                }
                else {
                    return 0;
                }
            }
        });
        for (Item anAgenda : sortAgenda ) {
            i++;
            if (anAgenda != null) {
                String firstName = anAgenda.getFirstName();
                String lastName = anAgenda.getLastName();
                String telephone = anAgenda.getPhoneNumber();
                System.out.println(i + ". " + firstName + " " + lastName  + '\t'+ telephone);
            } else {
                emptySpaces++;
            }
        }
        // System.out.println("empty spaces:" + emptySpaces);
        System.out.println("---------------");

    }


    private void printMenu() {
        System.out.println("1. List");
        System.out.println("2. Search");
        System.out.println("3. Create");
        System.out.println("4. Update");
        System.out.println("5. Delete");
        System.out.println("6. Read From File");
        System.out.println("7. Write to File");
        System.out.println("8. Sort List");
        System.out.println("9. Longest");
        System.out.println("10. Report");
        System.out.println("11. Delete All");
        System.out.println("20. Exit");
    }

    private void exitOption() {
        System.out.println("Bye, bye...the content not saved will now be erased");
        System.exit(0);
    }

    private void defaultOption() {
        System.out.println("This option does not exist. Pls take another option");
    }

    private int readMenuOption() {
        HandleKeyboard handleKeyboard = new HandleKeyboard().invokeOption();
        return handleKeyboard.getOption();
    }


    private void readFromFile() {

        //warning, it is going to overwrite
        HandleKeyboard handleKeyboard = new HandleKeyboard().invokeYesNo();
        String yesNo = handleKeyboard.getYesNo();
        if(yesNo.equalsIgnoreCase("Y")) {
            FileInputStream fis = null;
            ByteArrayOutputStream out = null;
            try {
                File f = new File("agenda.txt");
                fis = new FileInputStream(f);
                out = new ByteArrayOutputStream();
                IOUtils.copy(fis, out);
                byte[] data = out.toByteArray();
                agenda = SerializationUtils.deserialize(data);
                System.out.println("Read from file done!");
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("The file can't find!");
            } finally {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(fis);
            }

        }
    }

    private void loadFromFile() {
        //warning, it is going to overwrite
        FileInputStream fis = null;
        ByteArrayOutputStream out = null;
        try {
            File f = new File("agenda.txt");
            fis = new FileInputStream(f);
            out = new ByteArrayOutputStream();
            IOUtils.copy(fis, out);
            byte[] data = out.toByteArray();
            agenda = SerializationUtils.deserialize(data);
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("ERR:  =========== The file can't be find!");
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fis);
        }
    }

    private void writeToFile() {
        FileOutputStream fwr = null;
        try {
            byte[] data = SerializationUtils.serialize(agenda);
            File f = new File("agenda.txt");
            fwr = new FileOutputStream(f);
            fwr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(fwr);
        }
        System.out.println("Write to file done!");
    }

    private void saveToFile() {
        FileOutputStream fwr = null;
        try {
            byte[] data = SerializationUtils.serialize(agenda);
            File f = new File("agenda.txt");
            fwr = new FileOutputStream(f);
            fwr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(fwr);
        }
    }


    private class HandleKeyboard {
        private String firstName;
        private String lastName;
        private String phone;
        private String strSearch;

        private int option;

        private String yesNo;

        public String getFirstName() {
            return firstName;
        }
        public String getLastName() {
            return lastName;
        }

        public String getPhone() {
            return phone;
        }

        public int getOption() {
            return option;
        }

        public String getYesNo() {
            return yesNo;
        }

        public String getStrSearch() {
            return strSearch ;
        }

        public HandleKeyboard invokeItem() {
            Scanner s = new Scanner(System.in);
            System.out.print("FirstName: ");
            firstName = s.nextLine();
            System.out.print("LastName: ");
            lastName = s.nextLine();
            System.out.print("Phone Number: ");
            phone = s.nextLine();
            return this;
        }

        public HandleKeyboard invokeItemFirstName() {
            Scanner s = new Scanner(System.in);
            System.out.print("FirstName: ");
            firstName = s.nextLine();
            return this;
        }

        public HandleKeyboard invokeItemStrSearch() {
            Scanner s = new Scanner(System.in);
            System.out.print("Write text to search: ");
            strSearch = s.nextLine();
            return this;
        }

        public HandleKeyboard invokeOption() {
            Scanner s = new Scanner(System.in);
            System.out.print("Option: ");
            option = s.nextInt();
            return this;
        }

        public HandleKeyboard invokeYesNo() {
            Scanner s = new Scanner(System.in);
            System.out.print("Are you sure you want to overwrite your current content in memory ? (Y,N): ");
            yesNo = s.nextLine();
            return this;
        }
        public HandleKeyboard invokeDeleteYesNo() {
            Scanner s = new Scanner(System.in);
            System.out.print("Do you want to delete All ? (Y,N): ");
            yesNo = s.nextLine();
            if (yesNo.equalsIgnoreCase("Y")) {
                System.out.print("Are you sure you want to delete All ? (Y,N): ");
                yesNo = s.nextLine();
            }
            return this;        }
    }

    public void longestItem(){
        int maxLength = 0;
        String longestString = null;
        for (Item s : agenda  )
        {
            if (s != null) {
                if (getLenghtNameItem(s) > maxLength) {
                    maxLength = getLenghtNameItem(s);
                    longestString = getNameItem(s);
                }
            }
        }
        System.out.println("The longest Item is: "+  longestString);
    }
    public String getNameItem(Item s){
        return s.getFirstName()  +" "+s.getLastName()   ;
    }
    public String getItem(Item s){
        return s.getFirstName()  +" "+s.getLastName() +" "+s.getPhoneNumber()  ;
    }
    public int getLenghtNameItem(Item s){
        return getNameItem(s).length() ;
    }

    public void reportAgenda(){
        int nrItem =0;
        int nrItemA =0;

        for(Item s:agenda){
            if (s!=null){
                nrItem++;
                if (s.getFirstName().startsWith("A") || s.getFirstName().startsWith("a") ){
                nrItemA++;
                }
            }
        }
        System.out.println("Count Item: \t\t" + nrItem );
        System.out.println("Count Item begin with 'A' or 'a': \t\t" + nrItemA );
                System.out.println("You can also add: " + (agenda.length -  nrItem) + " items!" );
    }

}
