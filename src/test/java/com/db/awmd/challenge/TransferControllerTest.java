package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransferService;
import com.db.awmd.challenge.utils.StringConstants;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {
	
	private MockMvc mockMvc;
	@Autowired
	  private TransferService transferService;
	
	@Autowired
	  private AccountsService accountsService;

	  @Autowired
	  private WebApplicationContext webApplicationContext;

	  @Before
	  public void prepareMockMvc() {
	    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
	    // Reset the existing accounts before each test.
	    transferService.getAccountsRepository().clearAccounts();
	  }
	  /**
	   * Method to test money transfer for correct scenario.
	   * @throws Exception
	   */
	  @Test
	  public void testTransferMoney() throws Exception {
		  Account accountTo = new Account("Acc1", new BigDecimal("100"));
		  Account accountFrom = new Account("Acc2", new BigDecimal("500"));
		  this.accountsService.createAccount(accountTo);
		  this.accountsService.createAccount(accountFrom);
		  this.mockMvc.perform(put("/v1/moneyTransfer/Acc1/Acc2/150"))
	      .andExpect(status().isOk())
	      .andExpect(
	        content().string(StringConstants.TRANSACTION_SUCCESS_MSG));
	  }
	  /**
	   * Method to test money transfer for insufficient funds.
	   * @throws Exception
	   */
	  @Test
	  public void testTransferMoneyForInsufficientFunds() throws Exception {
		  Account accountTo = new Account("Acc1", new BigDecimal("500"));
		  Account accountFrom = new Account("Acc2", new BigDecimal("100"));
		  this.accountsService.createAccount(accountTo);
		  this.accountsService.createAccount(accountFrom);
		  this.mockMvc.perform(put("/v1/moneyTransfer/Acc1/Acc2/150"))
	      .andExpect(status().isOk())
	      .andExpect(
	        content().string(StringConstants.TRANSACTION_ERROR_MSG));
	  }  
	  
	  /**
	   * Method to test money transfer for incorrect id.
	   * @throws Exception
	   */
	  @Test
	  public void testTransferMoneyForIncorrectId() throws Exception {
		  Account accountFrom = new Account("Acc2", new BigDecimal("500"));
		  this.accountsService.createAccount(accountFrom);
		  this.mockMvc.perform(put("/v1/moneyTransfer/Acc1/Acc2/150"))
	      .andExpect(status().isOk())
	      .andExpect(
	        content().string(StringConstants.NON_EXISTING_FUND_MSG));
	  }  
	  /**
	   * Method to test money transfer for incorrect id.
	   * @throws Exception
	   */
	  @Test
	  public void testTransferMoneyForIncorrectId2() throws Exception {
		  Account accountTo = new Account("Acc1", new BigDecimal("100"));
		  this.accountsService.createAccount(accountTo);
		  this.mockMvc.perform(put("/v1/moneyTransfer/Acc1/Acc2/150"))
	      .andExpect(status().isOk())
	      .andExpect(
	        content().string(StringConstants.NON_EXISTING_FUND_MSG));
	  }  
}
