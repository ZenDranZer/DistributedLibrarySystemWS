package com.dsd;

import javax.xml.ws.Endpoint;

public class ServerDriver {
    public static void main(String[] args) {
        String concordiaURL = "http://localhost:8080/comp/dsd/concordia";
        String mcGillURL = "http://localhost:8080/comp/dsd/mcgill";
        String montrealURL = "http://localhost:8080/comp/dsd/montreal";
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
    }
}
