package com.taxisurfr.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.FinanceInfo;
import com.taxisurfr.shared.model.ProfilInfo;
import com.taxisurfr.shared.model.RatingInfo;
import com.taxisurfr.shared.model.RouteInfo;
import com.taxisurfr.shared.model.StatInfo;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface BookingServiceAsync
{
    void getCurrencyRate(BookingInfo bookingInfo, AsyncCallback<BookingInfo> callback)
            throws IllegalArgumentException;

    void addBooking(BookingInfo bookingInfo, AsyncCallback<BookingInfo> callback)
            throws IllegalArgumentException;

    void cancelBooking(BookingInfo bookingInfo, AgentInfo agentInfo, AsyncCallback<List<BookingInfo>> callback)
            throws IllegalArgumentException;

    void sendShareRequest(BookingInfo bookingInfo, AsyncCallback<BookingInfo> callback);

    // void sendShareAccepted(List<BookingInfo> bookingInfo, AsyncCallback<BookingInfo> callback);

    void getRoutes(AgentInfo userInfo, AsyncCallback<List<RouteInfo>> callback);

    void getContractors(AgentInfo userInfo, AsyncCallback<List<ContractorInfo>> callback);

    void getRoute(Long routeId, AsyncCallback<RouteInfo> callback);

    void getRoutes(AsyncCallback<List<RouteInfo>> callback);

    void deleteRoute(AgentInfo userInfo, RouteInfo placeInfo, AsyncCallback<List<RouteInfo>> callback)
            throws IllegalArgumentException;

    void deleteContractor(AgentInfo userInfo, ContractorInfo placeInfo, AsyncCallback<List<ContractorInfo>> callback)
            throws IllegalArgumentException;

    void saveRoute(AgentInfo userInfo, RouteInfo placeInfo, RouteInfo.SaveMode mode, AsyncCallback<List<RouteInfo>> callback)
            throws IllegalArgumentException;

    void saveContractor(AgentInfo userInfo, ContractorInfo placeInfo, ContractorInfo.SaveMode mode, AsyncCallback<List<ContractorInfo>> callback)
            throws IllegalArgumentException;

    void getBookings(AgentInfo userInfo, AsyncCallback<List<BookingInfo>> callback);

    void getBookingsForRoute(RouteInfo id, AsyncCallback<List<BookingInfo>> callback);

    void getRatings(RouteInfo routeInfo, AsyncCallback<List<RatingInfo>> callback);

    void addRating(RatingInfo statInfo, AsyncCallback<Void> asyncCallback);


    void handleShareAccepted(Long id, AsyncCallback<List<BookingInfo>> callback);

    void getUser(AsyncCallback<AgentInfo> callback);

    void getAgents(AsyncCallback<List<AgentInfo>> callback) throws IllegalArgumentException;

    void getPaypalProfil(AsyncCallback<ProfilInfo> callback);

    void payWithStripe(String token, BookingInfo bookingInfo, AsyncCallback<BookingInfo> callback);

    void sendStat(StatInfo statInfo, AsyncCallback<Void> asyncCallback);

    void getFinances(AgentInfo agentInfo, AsyncCallback<List<FinanceInfo>> asyncCallback);

    void savePayment(FinanceInfo financeInfo, AsyncCallback<List<FinanceInfo>> asyncCallback);

}
