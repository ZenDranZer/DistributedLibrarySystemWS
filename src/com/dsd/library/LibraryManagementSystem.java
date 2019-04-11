package com.dsd.library;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://com.dsd/library/")
@SOAPBinding(style=SOAPBinding.Style.RPC)
public interface LibraryManagementSystem {
    @WebMethod
    String findItem (String userId, String itemName);
    @WebMethod
    String returnItem (String userId, String itemID);
    @WebMethod
    String borrowItem (String userId, String itemID, int numberOfDays);
    @WebMethod
    String addItem (String userId, String itemID, String itemName, int quantity);
    @WebMethod
    String removeItem (String managerId, String itemId, int quantity);
    @WebMethod
    String listItem (String managerId);
    @WebMethod
    String addUserInWaitingList (String userId, String ItemId, int numberOfDays);
    @WebMethod
    String exchangeItem (String userId, String oldItemId, String newItemID);
    @WebMethod
    String validateUser (String userId);
}
