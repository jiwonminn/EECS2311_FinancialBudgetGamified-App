package unitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Transaction;
import controller.TransactionController;

class TransactionControllerTest extends TransactionController {
		public TransactionControllerTest() throws SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

		private Transaction transaction;
	    private LocalDate testDate;
	    private Timestamp controllerTestDate;
	    
	    @BeforeEach
	    public void setTime() {
	    	controllerTestDate = Timestamp.valueOf(LocalDateTime.now());
	    }
	    
	    private Timestamp getCurrentTimestamp() {
	        return new Timestamp(System.currentTimeMillis());
	    }
	    

	    @Test
	    public void NegativeTransaction() {
	    	assertThrows(IllegalArgumentException.class,()-> new Transaction("Test",-100.0,LocalDate.now(),true,"Test"));
	    }
	    
	    @Test
	    public void addNegativeTransaction() {
	    	assertThrows(IllegalArgumentException.class,()-> addTransaction("Test", -10.0,LocalDate.now(),false,"Test"));
	    }

	    @Test
	    public void noDescription () {
	    	assertThrows(IllegalArgumentException.class, ()-> addTransaction("",10.00,LocalDate.now(),false,"Test"));
	    	
	    }
	    
	    @Test
	    public void nullCases() {
	    	assertThrows(NullPointerException.class,()-> addTransaction(null,100,null,false,"Test"));
	    	
	    }
	    
	    public void deleteTransaction() throws SQLException {
	        // First add a transaction to delete
	        String description = "Test Transaction";
	        double amount = 100.0;
	        LocalDate date = LocalDate.now();
	        boolean isIncome = false;
	        String category = "Food";
	        
	        addTransaction(description, amount, date, isIncome, category);
	        
	 
	        List<Transaction> transactions = getTransactions();
	        Transaction testTransaction = transactions.get(0);
	        boolean result = TransactionController.deleteTransaction(testTransaction.getId());
	        
	        // Assert deletion was successful
	        assertTrue(result);
	        
	    }
	    
	    
	    
}

