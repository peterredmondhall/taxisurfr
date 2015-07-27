package com.taxisurfr.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ContractorInfo;
import com.taxisurfr.shared.model.FinanceInfo;
import com.taxisurfr.shared.model.ProfilInfo;
import com.taxisurfr.shared.model.RatingInfo;
import com.taxisurfr.shared.model.RouteInfo;
import com.taxisurfr.shared.model.StatInfo;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface BookingService extends RemoteService
{
    BookingInfo getCurrencyRate(BookingInfo model) throws IllegalArgumentException;

    BookingInfo addBooking(BookingInfo model) throws IllegalArgumentException;

    List<BookingInfo> cancelBooking(BookingInfo model, AgentInfo agentInfo) throws IllegalArgumentException;

    BookingInfo sendShareRequest(BookingInfo bookingInfo);

    // BookingInfo sendShareAccepted(List<BookingInfo> l);

    AgentInfo getUser() throws IllegalArgumentException;

    List<AgentInfo> getAgents() throws IllegalArgumentException;

    RouteInfo getRoute(Long routeId) throws IllegalArgumentException;

    List<RouteInfo> getRoutes(AgentInfo userInfo) throws IllegalArgumentException;

    List<ContractorInfo> getContractors(AgentInfo userInfo) throws IllegalArgumentException;

    List<RouteInfo> getRoutes() throws IllegalArgumentException;

    List<RouteInfo> deleteRoute(AgentInfo userInfo, RouteInfo placeInfo) throws IllegalArgumentException;

    List<ContractorInfo> deleteContractor(AgentInfo userInfo, ContractorInfo placeInfo) throws IllegalArgumentException;

    List<RouteInfo> saveRoute(AgentInfo userInfo, RouteInfo placeInfo, RouteInfo.SaveMode mode) throws IllegalArgumentException;

    List<ContractorInfo> saveContractor(AgentInfo userInfo, ContractorInfo placeInfo, ContractorInfo.SaveMode mode) throws IllegalArgumentException;

    List<BookingInfo> getBookings(AgentInfo userInfo) throws IllegalArgumentException;

    List<BookingInfo> getBookingsForRoute(RouteInfo routeInfo) throws IllegalArgumentException;

    List<RatingInfo> getRatings(RouteInfo routeInfo) throws IllegalArgumentException;

    void addRating(RatingInfo routeInfo) throws IllegalArgumentException;

    BookingInfo getBookingForTransaction(String ref) throws IllegalArgumentException;

    List<BookingInfo> handleShareAccepted(Long id) throws IllegalArgumentException;

    ProfilInfo getPaypalProfil() throws IllegalArgumentException;

    BookingInfo payWithStripe(String token, BookingInfo bookingInfo);

    void sendStat(StatInfo statInfo);

    AgentInfo createDefaultUser();

    List<FinanceInfo> getFinances(AgentInfo agentInfo);

    List<FinanceInfo> savePayment(FinanceInfo financeInfo);

}
