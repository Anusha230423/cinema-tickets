package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {
        if (accountId > 0) {
            // check if "ADULT" is present or not
            boolean valid = validateRequests(true, ticketTypeRequests);
            if (!valid) {
                throw new InvalidPurchaseException(
                        "Child and Infant tickets cannot be purchased without purchasing an Adult ticket.");
            }
            // check for maximum number of tickets
            boolean checkMax = validateRequests(false, ticketTypeRequests);
            if (!checkMax) {
                throw new InvalidPurchaseException(
                        "Only a maximum of 20 tickets that can be purchased at a time.");
            }
            // Printing total amount to pay
            System.out.printf("Total amount to pay: £%.2f\n", this.totalAmountToPay(ticketTypeRequests));

            System.out.print("Total seats to allocate: " + this.totalSeatsToAllocate(ticketTypeRequests));
        } else {
            throw new InvalidPurchaseException("Invalid account id: " + accountId);
        }

    }

    private boolean validateRequests(boolean isAdultPresent, TicketTypeRequest... ticketTypeRequests) {
        for (TicketTypeRequest ticket : ticketTypeRequests) {
            if (isAdultPresent) {
                if (ticket.getTicketType().toString().equals("ADULT")) {
                    return true;
                }
            } else {
                if (ticket.getNoOfTickets() <= 20) {
                    return true;
                }
            }
        }
        return false;
    }

    private double totalAmountToPay(TicketTypeRequest... ticketTypeRequests) {
        double totalCost = 0.00;
        for (TicketTypeRequest ticket : ticketTypeRequests) {
            double ticketCost = switch (ticket.getTicketType().toString()) {
                case "ADULT" -> ticket.getNoOfTickets() * 20;
                case "CHILD" -> ticket.getNoOfTickets() * 10;
                case "INFANT" -> 0.00;
                default -> 0.00;
            };
            totalCost += ticketCost;
        }
        return totalCost;
    }

    private int totalSeatsToAllocate(TicketTypeRequest... ticketTypeRequests) {
        int totalTickets = 0;
        for (TicketTypeRequest ticket : ticketTypeRequests) {
            if (ticket.getTicketType().toString() != "INFANT") {
                totalTickets += ticket.getNoOfTickets();
            }
        }
        return totalTickets;
    }
}
