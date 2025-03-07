package unitTests;

import static org.junit.Assert.assertEquals;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.time.LocalDate;

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
	    	controllerTestDate.toLocalDateTime().toLocalDate();
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
	    public void deleteTransaction() {
	    	Transaction transaction1 = new Transaction(1,1,controllerTestDate,"Test","Food","Expense",100);
	    	
	    }
	    
	    
	    
}

