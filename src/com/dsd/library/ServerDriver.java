package com.dsd.library;

import javax.xml.ws.Endpoint;

public class ServerDriver {
    public static void main(String[] args) {
        String concordiaURL = "http://localhost:8081/library";
        String mcGillURL = "http://localhost:8082/library";
        String montrealURL = "http://localhost:8083/library";

        LibraryManagementServer concordia = new LibraryManagementServer();
        concordia.setLibrary("CON");
        concordia.setLogger();
        Endpoint concordiaEndPoint = Endpoint.publish(concordiaURL,concordia);

        LibraryManagementServer mcgill = new LibraryManagementServer();
        mcgill.setLibrary("MCG");
        mcgill.setLogger();
        Endpoint mcgillEndPoint = Endpoint.publish(mcGillURL,mcgill);

        LibraryManagementServer montreal = new LibraryManagementServer();
        montreal.setLibrary("MON");
        montreal.setLogger();
        Endpoint montrealEndPoint = Endpoint.publish(montrealURL,montreal);

        Thread concordiaDelegate = new Thread(new Delegate(1301,concordia));
        concordiaDelegate.start();
        Thread mcgillDelegate = new Thread(new Delegate(1302,mcgill));
        mcgillDelegate.start();
        Thread montrealDelegate = new Thread(new Delegate(1303,montreal));
        montrealDelegate.start();
    }
}
