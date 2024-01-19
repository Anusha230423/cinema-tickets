import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void ticketsWithInvalidAccountIdTest() {
        exceptionRule.expect(InvalidPurchaseException.class);
        Long accountId = 0L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 4);

        ticketServiceImpl.purchaseTickets(accountId, adultTicket);
        exceptionRule.expectMessage("Invalid account id: " + accountId);
    }

    @Test
    public void ticketPurchaseWithAdultTest() throws InvalidPurchaseException {
        Long accountId = 7L;
        TicketTypeRequest adult = new TicketTypeRequest(Type.ADULT, 3);
        TicketTypeRequest child = new TicketTypeRequest(Type.CHILD, 4);
        ticketServiceImpl.purchaseTickets(accountId, adult, child);
        Assert.assertEquals("Total amount to pay: £100.00\nTotal seats to allocate: 7".toString(),
                outContent.toString());
    }

    @Test
    public void ticketPurchaseWithoutAdultTest() {
        exceptionRule.expect(InvalidPurchaseException.class);
        Long accountId = 14L;
        TicketTypeRequest child = new TicketTypeRequest(Type.CHILD, 2);
        ticketServiceImpl.purchaseTickets(accountId, child);
        exceptionRule.expectMessage("Child and Infant tickets cannot be purchased without purchasing an Adult ticket.");
    }

    @Test
    public void ticketPurchaseWithTooManyTicketsTest() {
        exceptionRule.expect(InvalidPurchaseException.class);
        Long accountId = 123L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 21);
        ticketServiceImpl.purchaseTickets(accountId, adultTicket);
        exceptionRule.expectMessage("Only a maximum of 20 tickets that can be purchased at a time.");
    }

    @Test
    public void ticketPurchaseTest1() {
        Long accountId = 25L;
        TicketTypeRequest adult = new TicketTypeRequest(Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(Type.CHILD, 3);
        TicketTypeRequest infant = new TicketTypeRequest(Type.INFANT, 2);
        ticketServiceImpl.purchaseTickets(accountId, adult, child, infant);
        Assert.assertEquals("Total amount to pay: £70.00\nTotal seats to allocate: 5".toString(),
                outContent.toString());
    }

    @Test
    public void ticketPurchaseTest2() {
        Long accountId = 65L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 7);
        TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 5);
        TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 2);

        ticketServiceImpl.purchaseTickets(accountId, adultTicket, childTicket, infantTicket);
        Assert.assertEquals("Total amount to pay: £190.00\nTotal seats to allocate: 12", outContent.toString());
    }

}
