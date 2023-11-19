package com.haddioui.comptecqrses.query.service;

import java.util.Date;
import java.util.List;

import com.haddioui.comptecqrses.query.entities.Account;
import com.haddioui.comptecqrses.query.entities.Operation;
import com.haddioui.comptecqrses.query.repository.AccountRepository;
import com.haddioui.comptecqrses.query.repository.OperationRepository;
import com.haddioui.comptecqrses.commonapi.enums.OperationType;
import com.haddioui.comptecqrses.commonapi.events.AccountActivatedEvent;
import com.haddioui.comptecqrses.commonapi.events.AccountCreatedEvent;
import com.haddioui.comptecqrses.commonapi.events.AccountCreditedEvent;
import com.haddioui.comptecqrses.commonapi.events.AccountDebitedEvent;
import com.haddioui.comptecqrses.commonapi.queries.GetAccountByIdQuery;
import com.haddioui.comptecqrses.commonapi.queries.GetAllAccountQuery;
import com.haddioui.comptecqrses.commonapi.queries.GetAllOperationQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceHandler {
	
	//private static final Logger l = Logger.getLogger(AccountServiceHandler.class);
	
	private AccountRepository accountRepository;
	private OperationRepository operationRepository;
	
	
	
	
	public AccountServiceHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
		super();
		this.accountRepository = accountRepository;
		this.operationRepository = operationRepository;
	}




	@EventHandler
	public void  on(AccountCreatedEvent event) {
		
		//l.info("**********************************");
		//l.info("AccountCreatedEvent event");
		
		Account account = new Account();
		account.setId(event.getId());
		account.setCurrency(event.getCurrency());
		account.setStatus(event.getStatus());
		account.setOperations(null);
		account.setBalance(event.getInitialBalance());	
		accountRepository.save(account);
	}
	
	@EventHandler
	public void  on(AccountActivatedEvent event) {
		
		Account account = accountRepository.findById(event.getId()).get();
		account.setStatus(event.getStatus());
		accountRepository.save(account);
	}
	
	@EventHandler
	public void  on(AccountDebitedEvent event) {
		
		Account account = accountRepository.findById(event.getId()).get();
		
		Operation operation = new Operation();
		operation.setAmount(event.getAmount());
		operation.setDate(new Date()); //à ne pas faire
		operation.setType(OperationType.DEBIT);
		operation.setAccount(account);
		operationRepository.save(operation);
		
		account.setBalance(account.getBalance()-event.getAmount());
		accountRepository.save(account);
	}
	
	@EventHandler
	public void  on(AccountCreditedEvent event) {
		
		Account account = accountRepository.findById(event.getId()).get();
		
		Operation operation = new Operation();
		operation.setAmount(event.getAmount());
		operation.setDate(new Date()); //à ne pas faire , car il faut prendre la date de event
		operation.setType(OperationType.CREDIT);
		operation.setAccount(account);
		operationRepository.save(operation);
		
		account.setBalance(account.getBalance()+event.getAmount());
		accountRepository.save(account);
	}
	
	@QueryHandler
	//@Transactional(readOnly=true)
	public List<Account> on(GetAllAccountQuery query) {
		return accountRepository.findAll();
	}
	
	@QueryHandler
	public Account on(GetAccountByIdQuery query) {
		return accountRepository.findById(query.getId()).get();
	}
	
	@QueryHandler
	public List<Operation> on(GetAllOperationQuery query) {
		return operationRepository.findAll();
	}
	
	

}
