package unitTests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
	    
	    @Test
	    public void transactionAdded() throws SQLException {
	    	boolean result = addTransaction(1,"2025-02-03","Test","Food","Expense",10);
	        assertTrue(result);
	        
	    }
	    
}

