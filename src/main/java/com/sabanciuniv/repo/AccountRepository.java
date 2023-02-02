package com.sabanciuniv.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sabanciuniv.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {

	@Query("{ 'id' : ?0 }")
	List<Account> findAccountById(String id);
	
}
