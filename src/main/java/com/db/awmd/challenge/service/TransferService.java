package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.utils.StringConstants;

import lombok.Getter;

@Service
public class TransferService {

	@Autowired
	NotificationService notificationService;

	public Lock accLock;

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public TransferService(AccountsRepository accountsRepository,NotificationService notificationService) {
		this.accountsRepository = accountsRepository;
		this.notificationService = notificationService;
		accLock = new ReentrantLock();
	}

	// TODO:Replace Hard coded string with custom exception for Insufficient fund
	// scenario and use @Autowired for NotificationService object instead of new.
	/**
	 * This method is used to transfer money between the accounts and notify both
	 * the account holders in case of successful transfer.
	 * 
	 * @param accountTo
	 * @param accountFrom
	 * @param amount
	 * @return the appropriate transfer message.
	 */
	public String transferMoney(String accountTo, String accountFrom, BigDecimal amount) {
		accLock.lock();
		try {
			Account accountToTransfer = accountsRepository.getAccount(accountTo);
			Account accountFromTransfer = accountsRepository.getAccount(accountFrom);
			if (accountToTransfer != null && accountFromTransfer != null) {
				addMoney(accountToTransfer, amount);
				String withdrawalMsg = withdrawMoney(accountFromTransfer, amount);
				if (withdrawalMsg.equalsIgnoreCase(StringConstants.INSUFFICIENT_FUND_ERROR_MSG)) {
					notificationService.notifyAboutTransfer(accountToTransfer, StringConstants.INSUFFICIENT_FUND_ERROR_MSG);
					notificationService.notifyAboutTransfer(accountFromTransfer, StringConstants.INSUFFICIENT_FUND_ERROR_MSG);
					return StringConstants.TRANSACTION_ERROR_MSG;
				}

				else {
					notificationService.notifyAboutTransfer(accountToTransfer, "{amount} money credited");
					notificationService.notifyAboutTransfer(accountFromTransfer, "{amount} money withdrawal");
					return StringConstants.TRANSACTION_SUCCESS_MSG;
				}
			} else {
				return StringConstants.NON_EXISTING_FUND_MSG;
			}
		} finally {
			accLock.unlock();
		}
	}

	// TODO: Replace Hard coded string message with Custom Exception in case if
	// account with given id is not present.
	/**
	 * Method to add money to the existing balance.
	 * 
	 * @param amount
	 * @return
	 */
	private String addMoney(Account accountTo, BigDecimal amount) {
		if (accountTo != null) {
			accountTo.getBalance().add(amount);
			return StringConstants.SUCCESSFUL_MONEY_ADDITION_MSG;

		} else
			return StringConstants.NON_EXISTING_FUND_MSG;

	}

	// TODO: Replace Hard coded string message with Custom Exception in case if
	// insufficient funds.
	/**
	 * Method to subtract money from existing balance.
	 * 
	 * @param amount
	 * @return
	 */
	private String withdrawMoney(Account accountFrom, BigDecimal amount) {
		if (accountFrom != null) {
			BigDecimal balance = accountFrom.getBalance();
			if (balance.compareTo(amount) >= 0) {
				balance.subtract(amount);
				return StringConstants.SUCCESSFUL_MONEY_WITHDRAWAL_MSG;
			} else
				return StringConstants.INSUFFICIENT_FUND_ERROR_MSG;

		} else
			return StringConstants.NON_EXISTING_FUND_MSG;
	}
}