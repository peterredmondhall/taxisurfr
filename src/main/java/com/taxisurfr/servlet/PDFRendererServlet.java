package com.taxisurfr.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.taxisurfr.server.BookingServiceManager;
import com.taxisurfr.server.util.PdfUtil;
import com.taxisurfr.shared.model.AgentInfo;
import com.taxisurfr.shared.model.BookingInfo;
import com.taxisurfr.shared.model.ContractorInfo;

public class PDFRendererServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    BookingServiceManager bookingService = new BookingServiceManager();
    PdfUtil pdfUtil = new PdfUtil();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String bookingId = req.getParameter("order");
        if (bookingId != null && bookingId.length() > 0)
        {
            byte[] bytes = null;
            try
            {
                BookingInfo bookingInfo = bookingService.getBooking(Long.parseLong(bookingId));
                ContractorInfo contractorInfo = bookingService.getContractor(bookingInfo);
                AgentInfo agentInfo = bookingService.getAgent(contractorInfo);
                if (bookingInfo != null)
                {
                    bytes = pdfUtil.generateTaxiOrder("template/order.pdf", bookingInfo, agentInfo, contractorInfo);

                    resp.setContentType("application/pdf");
                    String filename = "filename=\"order_" + bookingInfo.getOrderRef() + ".pdf\"";
                    String header = "inline; " + filename;
                    resp.addHeader("Content-Disposition", header);
                    resp.setContentLength(bytes.length);

                    ServletOutputStream sos = resp.getOutputStream();
                    sos.write(bytes);
                    sos.flush();
                    sos.close();
                }
                else
                {
                    resp.getWriter().write("Booking with id: " + bookingId + " not fetched!");
                }

            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
