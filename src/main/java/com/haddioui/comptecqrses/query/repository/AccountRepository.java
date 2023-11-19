package com.haddioui.comptecqrses.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.haddioui.comptecqrses.query.entities.Account;

public interface AccountRepository extends JpaRepository<Account, String>{
	

}
