package com.sabanciuniv.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


import com.sabanciuniv.model.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String>{

	@Query("{ 'fromAccountId' : ?0 }")
	List<Transaction> findTransactionByFromAccountId(String fromAccId);
	
	@Query("{ 'toAccountId' : ?0 }")
	List<Transaction> findTransactionByToAccountId(String toAccId);
}
