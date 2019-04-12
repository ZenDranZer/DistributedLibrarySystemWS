package com.dsd.library;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;

public class Client implements Runnable {

    private String clientID;
    private String library;
    private String type;
    private String index;
    private BufferedReader sc;
    private File logFile;
    private PrintWriter logger;
    private LibraryManagementSystem client;

    public Client(String clientID){
        this.clientID = clientID;
        this.library = clientID.substring(0,3);
        this.type = String.valueOf(clientID.charAt(3));
        this.index = clientID.substring(4);
        URL compURL = null;
        try{
        switch (library){
            case "CON":
                compURL = new URL("http://localhost:8081/library");
                break;
            case "MCG":
                compURL = new URL("http://localhost:8082/library");
                break;
            case "MON":
                compURL = new URL("http://localhost:8083/library");
                break;
        }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        QName comName = new QName("http://com.dsd/library/","LibraryManagementServerService");
        Service compService = Service.create(compURL, comName);
        this.client = compService.getPort(LibraryManagementSystem.class);
        sc = new BufferedReader(new InputStreamReader(System.in));
        logFile = new File("/home/sarvesh/IdeaProjects/JavaRIMExample/src/DistributedLibrarySystemWS/src/Logs/log_" + library + "_"+ clientID+ ".log");
        try{
            if(!logFile.exists())
                logFile.createNewFile();
            logger = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
        }catch (IOException io){
            System.out.println("Error in creating log file.");
            io.printStackTrace();
        }
        writeToLogFile("User " + clientID + " Started.");
    }

    private void printUserOptions(){
        System.out.println("Features :");
        System.out.println("1) Borrow an item.");
        System.out.println("2) Find an Item.");
        System.out.println("3) Return an Item.");
        System.out.println("4) Exchange Items.");
        System.out.println("Press 'N' or 'n' to exit.");
    }

    private void printManagerOptions(){
        System.out.println("Features :");
        System.out.println("1) Add an item.");
        System.out.println("2) Remove an Item.");
        System.out.println("3) List Item Availability.");
        System.out.println("4) MultiThreading (Operations : Add Item & List Item)");
        System.out.println("Press 'N' or 'n' to exit.");
    }

    @Override
    public void run() {
        if(type.equals("U"))
            userInterface();
        else
            managerInterface();
    }

    public void userInterface(){
        char op = 'Y';
        try {
            String response = client.validateUser(clientID);
            System.out.println(response);
            if(response.contains("false")) {
                System.out.println("Provided ID is wrong!! please invoke the client again.");
                writeToLogFile("User id: " + clientID + " Provided ID is wrong!! please invoke the client again.");
                System.exit(0);
            }
            System.out.println("Hello " + clientID);
            while (op == 'Y' || op == 'y'){
                printUserOptions();
                op = sc.readLine().charAt(0);
                switch (op){
                    case '1':
                        System.out.println("Borrow Item Section :");
                        System.out.println("Enter Item ID: ");
                        String itemID = sc.readLine();
                        System.out.println("Enter for how many days you want to borrow ?");
                        Integer numberOfDays = new Integer(sc.readLine());
                        String reply = client.borrowItem(clientID,itemID,numberOfDays);
                        System.out.println("Reply from server : " + reply);
                        writeToLogFile(reply);
                        if(reply.contains("waitList")){
                            System.out.println("Item is not available at all library, do wanna put yourself in a queue (Y/N) ? ");
                            String ch = sc.readLine();
                            if (ch.charAt(0) == 'Y' || ch.charAt(0) == 'y'){
                                reply = client.addUserInWaitingList(clientID,itemID,numberOfDays);
                                writeToLogFile(reply);
                                System.out.println(reply);
                            }
                        }
                        op = 'Y';
                        break;
                    case '2':
                        System.out.println("Find Item Section :");
                        System.out.println("Enter Item Name :");
                        itemID = sc.readLine();
                        reply = client.findItem(clientID,itemID);
                        writeToLogFile(reply);
                        System.out.println("Reply from server : " + reply);
                        op = 'Y';
                        break;
                    case '3':
                        System.out.println("Return Item Section :");
                        System.out.println("Enter Item ID :");
                        itemID = sc.readLine();
                        reply = client.returnItem(clientID,itemID);
                        writeToLogFile(reply);
                        System.out.println("Reply from server : " + reply);
                        op = 'Y';
                        break;
                    case '4':
                        System.out.println("Exchange Item Section :");
                        System.out.println("Enter new Item ID :");
                        String newItemID = sc.readLine();
                        System.out.println("Enter old Item ID :");
                        String oldItemID = sc.readLine();
                        reply = client.exchangeItem(clientID,oldItemID,newItemID);
                        writeToLogFile(reply);
                        System.out.println("Reply from server : " + reply);
                        op = 'Y';
                        break;
                    case 'N':
                        writeToLogFile("User Quit : UserID : " + clientID);
                        break;
                    case 'n':
                        writeToLogFile("User Quit : UserID : " + clientID);
                        break;
                    default:
                        System.out.println("Wrong Selection!");
                        op = 'Y';
                        break;
                }
            }
            System.out.println("Bye " + clientID);
            System.exit(0);
        } catch(RemoteException e){
            writeToLogFile("Remote Exception");
            System.out.println("Remote Exception.");
            e.printStackTrace();
        } catch (IOException e){
            writeToLogFile("IO Exception");
            System.out.println("IO Exception.");
            e.printStackTrace();
        }
    }

    public void managerInterface(){
        char op = 'Y';
        try {
            if(client.validateUser(clientID).contains("false")) {
                System.out.println("Provided ID is wrong!! please invoke the client again.");
                writeToLogFile("UserID : " + clientID +" Provided ID is wrong!! please invoke the client again. ");
                System.exit(0);
            }
            System.out.println("Hello " + clientID);
            while (op == 'Y' || op == 'y'){
                printManagerOptions();
                op = sc.readLine().charAt(0);
                switch (op){
                    case '1':
                        System.out.println("Enter Item ID: ");
                        String itemID = sc.readLine();
                        System.out.println("Enter Item Name: ");
                        String itemName = sc.readLine();
                        System.out.println("Enter Item quantity: ");
                        Integer quantity = new Integer(sc.readLine());
                        String reply = client.addItem(clientID,itemID,itemName,quantity);
                        writeToLogFile(reply);
                        System.out.println("Reply from Server : " + reply);
                        op = 'Y';
                        break;
                    case '2':
                        System.out.println("Enter Item ID: ");
                        itemID = sc.readLine();
                        System.out.println("Enter Item quantity: ");
                        quantity = new Integer(sc.readLine());
                        reply = client.removeItem(clientID,itemID,quantity);
                        writeToLogFile(reply);
                        System.out.println("Reply from Server : " + reply);
                        op = 'Y';
                        break;
                    case '3':
                        reply = client.listItem(clientID);
                        writeToLogFile(reply);
                        System.out.println("Reply from Server : \n" + reply);
                        op = 'Y';
                        break;
                    case '4':
                        performMultiThreading();
                        op = 'Y';
                        break;
                    case 'N':
                        writeToLogFile("Manager Quit : UserID : " + clientID);
                        break;
                    case 'n':
                        writeToLogFile("Manager Quit : UserID : " + clientID);
                        break;
                    default:
                        System.out.println("Wrong Selection!");
                        op = 'Y';
                        break;
                }
            }
            System.out.println("Bye " + clientID);
            System.exit(0);
        } catch(IOException e){
            System.out.println("IO Exception.");
            writeToLogFile("IO Exception");
            e.printStackTrace();
        }
    }

    private void performMultiThreading(){
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                String reply = client.addItem(clientID,"CON0004","ABC",3);
                writeToLogFile(reply);
                System.out.println("Multithread : 1 :Reply from Server : " + reply);
                reply = client.listItem(clientID);
                writeToLogFile(reply);
                System.out.println("Multithread : 1 :Reply from Server : " + reply);
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                String reply = client.addItem(clientID,"CON0005","ABCD",3);
                writeToLogFile(reply);
                System.out.println("Multithread : 2 :Reply from Server : " + reply);
                reply = client.listItem(clientID);
                writeToLogFile(reply);
                System.out.println("Multithread : 2 :Reply from Server : " + reply);
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                String reply = client.addItem(clientID,"CON0006","ABCDE",3);
                writeToLogFile(reply);
                System.out.println("Multithread : 3 :Reply from Server : " + reply);
                reply = client.listItem(clientID);
                writeToLogFile(reply);
                System.out.println("Multithread : 3 :Reply from Server : " + reply);
            }
        };
        Runnable runnable4 = new Runnable() {
            @Override
            public void run() {
                String reply = client.addItem(clientID,"CON0007","ABCDEF",3);
                writeToLogFile(reply);
                System.out.println("Multithread : 4 :Reply from Server : " + reply);
                reply = client.listItem(clientID);
                writeToLogFile(reply);
                System.out.println("Multithread : 4 :Reply from Server : " + reply);
            }
        };
        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        Thread thread3 = new Thread(runnable3);
        Thread thread4 = new Thread(runnable4);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }


    synchronized private void writeToLogFile(String message) {
        try {
            if (logger == null)
                return;
            logger.println(Calendar.getInstance().getTime().toString() + " - " + message);
            logger.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
