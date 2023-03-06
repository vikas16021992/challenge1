package com.dws.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.web.AccountsController;

import net.bytebuddy.agent.VirtualMachine.ForHotSpot.Connection.Response;

@SpringBootTest
class ChallengeApplicationTests {
	@Autowired
	AccountsController accountsController;
	
	@MockBean
	AccountsRepository accountsRepository; 
	
	@Autowired
	AccountsRepository accountsRepository1; 

	@Test
	void getAccountTest() {
		String account ="123";
		BigDecimal balance = new BigDecimal(2002);
		String emailId = "vikasv16.mishra@gmail.com";
		when(accountsRepository.getAccount(account)).thenReturn(new Account(account, balance, emailId));
		assertEquals(balance, accountsController.getAccount(account).getBalance());
		
	}
	
	@Test
	void getAccountServiceExceptionTest() {
		String str = "Account id 123 already exists!";
		Account account = new Account("123", new BigDecimal(2002), "abc@gmail.com");
		
		when(accountsController.createAccount(account)).thenThrow(new DuplicateAccountIdException(str));
		assertEquals(str, accountsController.createAccount(account).getBody().toString());
		
		
	}
	
	@Test
	void getAccountServiceTest() {
		Account account = new Account("123", new BigDecimal(2002), "abc@gmail.com");
		ResponseEntity<Object> response= accountsController.createAccount(account);
		
		assertNotEquals(response.getStatusCode().is4xxClientError(), true);
		
		
		
	}
	
	@Test
	void fundTransferTest() {
		String account ="123";
		BigDecimal balance = new BigDecimal(2002);
		String emailId = "vikasv16.mishra@gmail.com";
		
		String account1 ="456";
		BigDecimal balance1 = new BigDecimal(2102);
		String emailId1 = "vikasv16.mishra@gmail.com";
		
		Account fromAct=   (Account) when(accountsRepository1.getAccount(account)).thenReturn(new Account(account, balance, emailId));
		Account toAct=   (Account) when(accountsRepository1.getAccount(account)).thenReturn(new Account(account, balance, emailId));
		
		ResponseEntity<Object> response= accountsController.fundTransfer(fromAct.getAccountId(), toAct.getAccountId(), balance1);
		
		assertEquals(response.getStatusCode().is2xxSuccessful(), true);
		
		
	}

}
