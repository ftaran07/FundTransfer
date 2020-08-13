package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.service.TransferService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/moneyTransfer")
@Slf4j
public class TransferController {
	
	private final TransferService transferService;
	
	@Autowired
	  public TransferController(TransferService transferService) {
	    this.transferService = transferService;
	  }
	
	/**
	   * End point for money transfer.
	   * @param accountTo
	   * @param accountFrom
	   * @param amount
	   * @return
	   */
	 @PutMapping(path = "/{accountTo}/{accountFrom}/{amount}")
	  public String transferMoney(@PathVariable("accountTo") String accountTo,
			  @PathVariable("accountFrom") String accountFrom,@PathVariable("amount") String amount ) {
	    log.info("Transferring amount {} from account Id {} to accountID{}", 
	    		amount,accountFrom,accountTo);
	    BigDecimal amountActual = new BigDecimal(amount);
	    return this.transferService.transferMoney(accountTo,accountFrom,amountActual);
	 }
}
