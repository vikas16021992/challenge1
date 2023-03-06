package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AmountException;
import com.dws.challenge.exception.InvalidAccountIdException;
import com.dws.challenge.repository.AccountsRepository;

import jakarta.validation.Valid;
import lombok.Getter;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;
	
	@Autowired
	private EmailNotificationService emailNotificationService;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	@Async
	public ResponseEntity<String> fundTransfer(@Valid String fromAccountId, @Valid String toAccountId, @Valid BigDecimal amount) {
		// TODO Auto-generated method stub
		Account fromAct = accountsRepository.getAccount(fromAccountId);
		Account toAct = accountsRepository.getAccount(toAccountId);
		
		//To check whether fromAccountId and toAccountId are not same
		if(fromAccountId.equals(toAccountId)) {
			throw new InvalidAccountIdException("From accont and to account are same");
			
		}
		// To check whether both the account exist in DB and also to check tr money should be greater than 0
		if (fromAct != null && toAct != null && amount.compareTo(BigDecimal.ZERO)>0) {
			BigDecimal from = fromAct.getBalance();
			BigDecimal to = toAct.getBalance();
			if (from.compareTo(amount) >= 0) {
				to = to.add(amount);
				toAct.setBalance(to);
				
			} else {
				throw new AmountException("Invalid Amount");
			}
		} else {
			throw new InvalidAccountIdException("Invalid account");
		}
		
		BigDecimal fromBal = fromAct.getBalance();
		fromBal = fromBal.subtract(amount);
		fromAct.setBalance(fromBal);
		//After successful transfer an email will be shoot out to the receiver  account
		emailNotificationService.notifyAboutTransfer(toAct,"Fund transfer done successfully");
		return new ResponseEntity<String>("Fund Transfer done successfully",HttpStatus.CREATED);
		
	}
}
