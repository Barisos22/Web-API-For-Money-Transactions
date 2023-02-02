package com.sabanciuniv.model;

import org.springframework.data.annotation.Id;

public class Transaction {

	@Id
	private String id;
	private String fromAccountId;
	private String toAccountId;
	private double amount;
	
	public Transaction() {
		// TODO Auto-generated constructor stub
	}

	public Transaction(String fromAccountId, String toAccountId, double amount) {
		super();
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;
	}

	public String getFromAccountId() {
		return fromAccountId;
	}

	public void setFromAccountId(String fromAccountId) {
		this.fromAccountId = fromAccountId;
	}

	public String getToAccountId() {
		return toAccountId;
	}

	public void setToAccId(String toAccountId) {
		this.toAccountId = toAccountId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmountTransferred(double amount) {
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	
	
	
}
