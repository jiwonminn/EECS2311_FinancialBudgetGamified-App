package unitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import model.Transaction;
import controller.TransactionController;

class TransactionControllerTest extends TransactionController {
		private Transaction transaction;
	    private LocalDate testDate;
	    

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
	    
	    
	    
}

