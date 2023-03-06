package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AmountException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InvalidAccountIdException;
import com.dws.challenge.service.AccountsService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

	private final AccountsService accountsService;

	
	@Autowired
	public AccountsController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	//For adding accounts details with acount id, email id and amount
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
		// log.info("Creating account {}", account);

		System.out.println("Creating account {}" + account);

		try {
			this.accountsService.createAccount(account);
		} catch (DuplicateAccountIdException daie) {
			return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	//to fetch  the accounts details based on account id 
	@GetMapping(path = "/{accountId}")
	public Account getAccount(@PathVariable String accountId) {

		System.out.println("Retrieving account for id {}" + accountId);
		return this.accountsService.getAccount(accountId);
	}
	
	/*For transferring fund .. fromAccountId means the accountid which is tranfering money to "toAccountId"
	 * Amount means the amount to transfer
	*/

	@PostMapping(path = "/{fromAccountId}/{toAccountId}/{amount}")
	public ResponseEntity<Object> fundTransfer(@PathVariable @Valid String fromAccountId,
			@PathVariable @Valid String toAccountId, @PathVariable @Valid BigDecimal amount) {
		// log.info("Creating account {}", account);

		try {
			accountsService.fundTransfer(fromAccountId, toAccountId, amount);

		} catch (AmountException ae) {
			return new ResponseEntity<Object>(ae.getMessage(), HttpStatus.BAD_REQUEST);

		} catch (InvalidAccountIdException ie) {
			return new ResponseEntity<Object>(ie.getMessage(), HttpStatus.BAD_REQUEST);

		}

		return new ResponseEntity<>("Fund transfer done successfully", HttpStatus.CREATED);
	}

}
