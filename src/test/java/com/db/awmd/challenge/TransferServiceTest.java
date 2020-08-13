package com.db.awmd.challenge;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.service.TransferService;
import com.db.awmd.challenge.utils.StringConstants;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransferServiceTest {


	@Mock
	private  AccountsRepository accountsRepository;

	@Mock
	private NotificationService emailNotificationService;
	
    @InjectMocks
	private TransferService transferService;

	

	/**
	 * Method to test fund transfer for correct scenario.
	 */
	@Test
	public void testTransferMoney() {
		Account accountTo = new Account("Acc1", new BigDecimal("100"));
		Account accountFrom = new Account("Acc2", new BigDecimal("500"));
		Mockito.when(accountsRepository.getAccount("Acc1")).thenReturn(accountTo);
		Mockito.when(accountsRepository.getAccount("Acc2")).thenReturn(accountFrom);
		Mockito.doNothing().when(emailNotificationService).notifyAboutTransfer(Mockito.any(Account.class),
				Mockito.anyString());
		String msg = transferService.transferMoney("Acc1", "Acc2", new BigDecimal(150));
		assertEquals(StringConstants.TRANSACTION_SUCCESS_MSG,msg);
	}

	/**
	 * Method to test fund transfer for incorrect scenario.
	 */
	@Test
	public void testTransferMoneyForInsufficientFunds() {
		Account accountTo = new Account("Acc3", new BigDecimal("500"));
		Account accountFrom = new Account("Acc4", new BigDecimal("100"));
		Mockito.when(accountsRepository.getAccount("Acc3")).thenReturn(accountTo);
		Mockito.when(accountsRepository.getAccount("Acc4")).thenReturn(accountFrom);
		Mockito.doNothing().when(emailNotificationService).notifyAboutTransfer(Mockito.any(Account.class),
				Mockito.anyString());
		String msg = transferService.transferMoney("Acc3", "Acc4", new BigDecimal(150));
		assertEquals(StringConstants.TRANSACTION_ERROR_MSG,msg);
	}
	
	/**
	 * Method to test fund transfer for incorrect scenario.
	 */
	@Test
	public void testTransferMoneyForIncorrectId() {
		Account accountTo = new Account("Acc3", new BigDecimal("500"));
		Account accountFrom = new Account("Acc4", new BigDecimal("100"));
		Mockito.when(accountsRepository.getAccount("Acc3")).thenReturn(null);
		Mockito.when(accountsRepository.getAccount("Acc4")).thenReturn(accountFrom);
		Mockito.doNothing().when(emailNotificationService).notifyAboutTransfer(Mockito.any(Account.class),
				Mockito.anyString());
		String msg = transferService.transferMoney("Acc3", "Acc4", new BigDecimal(150));
		assertEquals(StringConstants.NON_EXISTING_FUND_MSG,msg);
	}
	/**
	 * Method to test fund transfer for incorrect scenario.
	 */
	@Test
	public void testTransferMoneyForIncorrectId2() {
		Account accountTo = new Account("Acc3", new BigDecimal("500"));
		Mockito.when(accountsRepository.getAccount("Acc3")).thenReturn(accountTo);
		Mockito.when(accountsRepository.getAccount("Acc4")).thenReturn(null);
		Mockito.doNothing().when(emailNotificationService).notifyAboutTransfer(Mockito.any(Account.class),
				Mockito.anyString());
		String msg = transferService.transferMoney("Acc3", "Acc4", new BigDecimal(150));
		assertEquals(StringConstants.NON_EXISTING_FUND_MSG,msg);
	}
}
