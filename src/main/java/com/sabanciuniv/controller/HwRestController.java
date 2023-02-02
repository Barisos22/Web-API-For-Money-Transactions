package com.sabanciuniv.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sabanciuniv.model.Account;
import com.sabanciuniv.model.Transaction;
import com.sabanciuniv.repo.AccountRepository;
import com.sabanciuniv.repo.TransactionRepository;

@RestController
@RequestMapping()
public class HwRestController {

	@Autowired private TransactionRepository transactionRepo;
	@Autowired private AccountRepository accountRepo;

	private static final Logger log = LoggerFactory.getLogger(HwRestController.class);

	@PostConstruct
	public void init() {
		
		if(transactionRepo.count() == 0) {
			Account account1 = new Account("1111", "Jack Johns", LocalDateTime.now());
			Account account2 = new Account("2222", "Henry Williams", LocalDateTime.now());
			Transaction transaction1 = new Transaction("1111","2222", 1500);
			Transaction transaction2 = new Transaction("2222","1111", 2500);
			accountRepo.save(account1);
			accountRepo.save(account2);
			transactionRepo.save(transaction1);
			transactionRepo.save(transaction2);	
		}
	}
	
	//API 1: CREATE ACCOUNT
	@PostMapping("/account/save")
	public AccountMessage createAccount(@RequestBody Account account) {
		AccountMessage accmessage = new AccountMessage();
		if(account.getId() == null || account.getOwner() == null){
			accmessage.setMessage("ERROR:missing fields");
			accmessage.setData(null);
		}
		else {
			Account createAcc = new Account();
			createAcc.setId(account.getId());
			createAcc.setOwner(account.getOwner());
			createAcc.setCreateDate(LocalDateTime.now());
			accountRepo.save(createAcc);
			accmessage.setMessage("SUCCESS");
			accmessage.setData(createAcc);	
		}
		return accmessage;	
	}
	
	//API 2: CREATE TRANSACTION
	@PostMapping("/transaction/save")
	public TransactionMessage createTransaction(@RequestBody Transaction transaction) {
		TransactionMessage trmessage = new TransactionMessage();
		if(transaction.getAmount() == 0.0f || transaction.getFromAccountId() == null || transaction.getToAccountId() == null){
			trmessage.setMessage("ERROR:missing fields");
			trmessage.setData(null);
		}
		else if(accountRepo.findAccountById(transaction.getFromAccountId()).isEmpty()|| accountRepo.findAccountById(transaction.getToAccountId()).isEmpty() ) {
			trmessage.setMessage("ERROR: account id");
			trmessage.setData(null);	
		}	
		else {
			Transaction createTr = new Transaction();
			createTr.setAmountTransferred(transaction.getAmount());
			createTr.setFromAccountId(transaction.getFromAccountId());
			createTr.setToAccId(transaction.getToAccountId());
			transactionRepo.save(createTr);
			TransactionData Tdata = Transform(createTr);
			trmessage.setMessage("SUCCESS");
			trmessage.setData(Tdata);	
		}
		return trmessage;	
	}

	//API 3: SUMMARY MESSAGE
	@GetMapping("account/{accountId}")
	public SummaryMessage createSummary(@PathVariable String accountId) {
		
		int bal = 0;
		SummaryMessage sumMessage = new SummaryMessage();
		SummaryData sumData = new SummaryData();
		List<Account> accList = accountRepo.findAccountById(accountId);
		if(accList.size() > 0)
		{
			Account acc = accList.get(0);
			sumData.setId(acc.getId());
			sumData.setOwner(acc.getOwner());
			sumData.setCreateDate(LocalDateTime.now());
			List<Transaction> tListOut = transactionRepo.findTransactionByFromAccountId(accountId);
			List<TransactionData> out = new ArrayList<>();
			for(int i=0;i<tListOut.size();i++) {
				
				TransactionData t = Transform(tListOut.get(i));
				out.add(t);
				bal -= t.getAmount();
				
			}
			List<Transaction> tListIn =transactionRepo.findTransactionByToAccountId(acc.getId());
			List<TransactionData> in = new ArrayList<>();
			for(int i=0;i<tListIn.size();i++) {
				TransactionData t = Transform(tListIn.get(i));
				in.add(t);
				bal += t.getAmount();
			}
			sumData.setTransactionsOut(out);
			sumData.setTransactionsIn(in);
			sumData.setBalance(bal);
			sumMessage.setMessage("SUCCESS");
			sumMessage.setData(sumData);
		}
		else
		{
			sumMessage.setMessage("ERROR:account doesnt exist!");
			sumMessage.setData(null);	
		}
		return sumMessage;	
	}
	
	//API 4: LIST INCOMING TRANSACTIONS
	@GetMapping("transaction/to/{accountId}")
	public TransactionIn TransactionInInfo(@PathVariable String accountId) {
		
		TransactionIn t = new TransactionIn();
		TransactionInData tData = new TransactionInData();
		List<Account> accList = accountRepo.findAccountById(accountId);
		if(accList.size() > 0)
		{
			Account acc = accList.get(0);
			List<Transaction> tListIn =transactionRepo.findTransactionByToAccountId(acc.getId());
			List<TransactionData> in = new ArrayList<>();
			for(int i=0;i<tListIn.size();i++) {
				TransactionData tD = Transform(tListIn.get(i));
				in.add(tD);	
			}
			tData.setTransactionsIn(in);
			t.setMessage("SUCCESS");
			t.setData(tData);	
		}
		else
		{
			t.setMessage("ERROR:account doesn’t exist");
			t.setData(null);	
		}	
		return t;
	}
	
	//API5: LIST OUTGOING TRANSACTIONS
	@GetMapping("transaction/from/{accountId}")
	public TransactionOut TransactionOutInfo(@PathVariable String accountId) {
		
		TransactionOut t = new TransactionOut();
		TransactionOutData tData = new TransactionOutData();
		List<Account> accList = accountRepo.findAccountById(accountId);
		if(accList.size() > 0)
		{
			Account acc = accList.get(0);
			List<Transaction> tListOut =transactionRepo.findTransactionByFromAccountId(acc.getId());
			List<TransactionData> in = new ArrayList<>();
			for(int i=0;i<tListOut.size();i++) {
				TransactionData tD = Transform(tListOut.get(i));
				in.add(tD);	
			}
			tData.setTransactionsOut(in);
			t.setMessage("SUCCESS");
			t.setData(tData);	
		}
		else
		{
			t.setMessage("ERROR:account doesn’t exist");
			t.setData(null);	
		}
		
		return t;
	}
	
	public TransactionData Transform(Transaction tr) {
		
		TransactionData Tdata = new TransactionData(tr.getId(), accountRepo.findAccountById(tr.getFromAccountId()).get(0), accountRepo.findAccountById(tr.getToAccountId()).get(0), LocalDateTime.now(), tr.getAmount()  );
		return Tdata;
	}
	
	class Message{
		
		protected String message;
		
		public Message() {
			// TODO Auto-generated constructor stub
		}
		public Message(String message) {
			super();
			this.message = message;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}	
	}
	class AccountMessage extends Message{
		
		Account data;
		
		public AccountMessage() {
			// TODO Auto-generated constructor stub
		}
		public AccountMessage(String message, Account data) {
			super(message);
			this.data = data;
		}
		public Account getData() {
			return data;
		}
		public void setData(Account data) {
			this.data = data;
		}		
	}
	class TransactionMessage extends Message{
		
		TransactionData data;
		
		public TransactionMessage() {
			// TODO Auto-generated constructor stub
		}
		public TransactionMessage(String message, TransactionData data) {
			super(message);
			this.data = data;
		}
		public TransactionData getData() {
			return data;
		}
		public void setData(TransactionData data) {
			this.data = data;
		}	
	}
	
	class SummaryData{
		
		String id;
		String owner;
		LocalDateTime createDate;
		List<TransactionData> transactionsOut;
		List<TransactionData> transactionsIn;
		double balance;
		
		public SummaryData() {
			// TODO Auto-generated constructor stub
		}
		public SummaryData(String id, String owner, LocalDateTime createDate, List<TransactionData> transactionsOut,
				List<TransactionData> transactionsIn, double balance) {
			super();
			this.id = id;
			this.owner = owner;
			this.createDate = createDate;
			this.transactionsOut = transactionsOut;
			this.transactionsIn = transactionsIn;
			this.balance = balance;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getOwner() {
			return owner;
		}
		public void setOwner(String owner) {
			this.owner = owner;
		}
		public LocalDateTime getCreateDate() {
			return createDate;
		}
		public void setCreateDate(LocalDateTime createDate) {
			this.createDate = createDate;
		}
		public List<TransactionData> getTransactionsOut() {
			return transactionsOut;
		}
		public void setTransactionsOut(List<TransactionData> transactionsOut) {
			this.transactionsOut = transactionsOut;
		}
		public List<TransactionData> getTransactionsIn() {
			return transactionsIn;
		}
		public void setTransactionsIn(List<TransactionData> transactionsIn) {
			this.transactionsIn = transactionsIn;
		}
		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
	}

	class SummaryMessage extends Message{
		
		
		SummaryData data;
		
		public SummaryMessage() {
			// TODO Auto-generated constructor stub
		}
		public SummaryMessage(String message, SummaryData data) {
			super(message);
			this.data = data;
		}
		public SummaryData getData() {
			return data;
		}
		public void setData(SummaryData data) {
			this.data = data;
		}		
	}
	
	class TransactionData {
		
		String id;
		Account from;
		Account to;
		LocalDateTime createDate;
		double amount;
		
		public TransactionData() {
			// TODO Auto-generated constructor stub
		}
	
		public TransactionData(String id, Account from, Account to, LocalDateTime createDate, double amount) {
			super();
			this.id = id;
			this.from = from;
			this.to = to;
			this.createDate = createDate;
			this.amount = amount;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Account getFrom() {
			return from;
		}
		public void setFrom(Account from) {
			this.from = from;
		}
		public Account getTo() {
			return to;
		}
		public void setTo(Account to) {
			this.to = to;
		}
		public LocalDateTime getCreateDate() {
			return createDate;
		}
		public void setCreateDate(LocalDateTime createDate) {
			this.createDate = createDate;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}	
	}
	
	class TransactionIn extends Message{
		
		TransactionInData Data;
		
		public TransactionIn() {
			// TODO Auto-generated constructor stub
		}
		public TransactionIn(String message, TransactionInData data) {
			super(message);
			Data = data;
		}
		public TransactionInData getData() {
			return Data;
		}
		public void setData(TransactionInData data) {
			Data = data;
		}		
	}
	
	class TransactionInData{
		
		List<TransactionData> transactionsIn;
		
		public TransactionInData() {
			// TODO Auto-generated constructor stub
		}
		public TransactionInData(List<TransactionData> transactionsIn) {
			super();
			this.transactionsIn = transactionsIn;
		}
		public List<TransactionData> getTransactionsIn() {
			return transactionsIn;
		}
		public void setTransactionsIn(List<TransactionData> transactionsIn) {
			this.transactionsIn = transactionsIn;
		}		
	}
	
	class TransactionOut extends Message{
		
		TransactionOutData data;
		
		public TransactionOut() {
			// TODO Auto-generated constructor stub
		}
		public TransactionOut(TransactionOutData data) {
			super();
			this.data = data;
		}
		public TransactionOutData getData() {
			return data;
		}
		public void setData(TransactionOutData data) {
			this.data = data;
		}
	}
	
	class TransactionOutData{
		
		List<TransactionData> transactionsOut;
		
		public TransactionOutData() {
			// TODO Auto-generated constructor stub
		}
		public TransactionOutData(List<TransactionData> transactionsOut) {
			super();
			this.transactionsOut = transactionsOut;
		}
		public List<TransactionData> getTransactionsOut() {
			return transactionsOut;
		}
		public void setTransactionsOut(List<TransactionData> transactionsOut) {
			this.transactionsOut = transactionsOut;
		}	
	}
	
}
