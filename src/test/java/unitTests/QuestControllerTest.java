package unitTests;

import controller.QuestController;
import database.dao.QuestDao;
import database.dao.TransactionDao;
import model.Quest;
import model.Transaction;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// Fake implementation of the production QuestDao interface.
class FakeQuestDao implements QuestDao {
    private Map<Integer, Quest> quests = new HashMap<>();
    private int nextId = 1;

    @Override
    public int createQuest(Quest quest) throws SQLException {
        int id = nextId++;
        quest.setId(id);
        quests.put(id, quest);
        return id;
    }

    @Override
    public boolean updateQuest(Quest quest) throws SQLException {
        if (quests.containsKey(quest.getId())) {
            quests.put(quest.getId(), quest);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteQuest(int questId, int userId) throws SQLException {
        Quest removed = quests.remove(questId);
        return removed != null && removed.getUserId() == userId;
    }

    @Override
    public List<Quest> getQuestsByUserId(int userId) throws SQLException {
        List<Quest> list = new ArrayList<>();
        for (Quest q : quests.values()) {
            if (q.getUserId() == userId) {
                list.add(q);
            }
        }
        return list;
    }

    @Override
    public List<Quest> getActiveQuestsByUserId(int userId) throws SQLException {
        List<Quest> list = new ArrayList<>();
        for (Quest q : quests.values()) {
            if (q.getUserId() == userId && !q.isCompleted()) {
                list.add(q);
            }
        }
        return list;
    }

    @Override
    public List<Quest> getDailyQuestsByUserId(int userId) throws SQLException {
        List<Quest> list = new ArrayList<>();
        for (Quest q : quests.values()) {
            if (q.getUserId() == userId && "DAILY".equalsIgnoreCase(q.getQuestType())) {
                list.add(q);
            }
        }
        return list;
    }

    @Override
    public List<Quest> getWeeklyQuestsByUserId(int userId) throws SQLException {
        List<Quest> list = new ArrayList<>();
        for (Quest q : quests.values()) {
            if (q.getUserId() == userId && "WEEKLY".equalsIgnoreCase(q.getQuestType())) {
                list.add(q);
            }
        }
        return list;
    }

    @Override
    public List<Quest> getMonthlyQuestsByUserId(int userId) throws SQLException {
        List<Quest> list = new ArrayList<>();
        for (Quest q : quests.values()) {
            if (q.getUserId() == userId && "MONTHLY".equalsIgnoreCase(q.getQuestType())) {
                list.add(q);
            }
        }
        return list;
    }

    @Override
    public boolean completeQuest(int questId, int userId) throws SQLException {
        // For testing, simulate that the DAO method does not complete the quest (return false)
        return false;
    }

    @Override
    public Quest mapResultSetToQuest(java.sql.ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("Not used in fake");
    }

    @Override
    public Quest getQuestById(int questId) throws SQLException {
        return quests.get(questId);
    }
}

// Fake implementation of the production database.dao.ExperienceDao interface.
class FakeExperienceDao implements database.dao.ExperienceDao {
    private Map<Integer, Integer> userXP = new HashMap<>();

    @Override
    public boolean addUserXP(int userId, int xpReward) throws SQLException {
        int current = userXP.getOrDefault(userId, 0);
        userXP.put(userId, current + xpReward);
        return true;
    }

    @Override
    public int[] getUserExperience(int userId) throws SQLException {
        int xp = userXP.getOrDefault(userId, 0);
        // For simplicity, level = xp/100
        int level = xp / 100;
        return new int[]{xp, level};
    }

    @Override
    public int getXpForNextLevel(int currentLevel) {
        return 0;
    }
}

// Fake implementation of the production database.dao.TransactionDao interface.
class FakeTransactionDaoTest implements database.dao.TransactionDao {
    private int transactionCount;

    public FakeTransactionDaoTest(int count) {
        this.transactionCount = count;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) throws SQLException {
        return null;
    }

    @Override
    public void addTransaction(int userId, LocalDate date, double amount, String description, String type, String category) throws SQLException {

    }

    @Override
    public boolean updateTransaction(Transaction transaction) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteTransaction(int transactionId) throws SQLException {
        return false;
    }

    @Override
    public int getTransactionCountForUser(int userId) throws SQLException {
        return transactionCount;
    }

    @Override
    public int getTransactionCountForDay(int userId, LocalDate date) throws SQLException {
        return transactionCount;
    }
}

// Fake implementation of the production database.dao.Budget interface.
class FakeBudgetDao implements database.dao.Budget {
    private double income;
    private double expenses;

    public FakeBudgetDao(double income, double expenses) {
        this.income = income;
        this.expenses = expenses;
    }

    @Override
    public double getTotalIncomeForMonth(int userId) throws SQLException {
        return income;
    }

    @Override
    public double getTotalExpensesForMonth(int userId) throws SQLException {
        return expenses;
    }
}

public class QuestControllerTest {

    private static final int TEST_USER_ID = 1;
    private QuestController controller;

    // Helper method to set private fields via reflection.
    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
        controller = new QuestController();
        // Inject fake implementations that implement the exact production interfaces.
        setPrivateField(controller, "QuestDao", new FakeQuestDao());
        setPrivateField(controller, "experienceDao", new FakeExperienceDao());
        setPrivateField(controller, "transactionDao", new FakeTransactionDaoTest(1)); // Simulate one transaction.
        setPrivateField(controller, "budgetDao", new FakeBudgetDao(1000.0, 500.0));
    }

    @Test
    public void testCreateQuest() throws SQLException {
        Quest quest = new Quest(0, "Test Quest", "Testing creation", "DAILY", 50, 0, false, LocalDate.now().plusDays(1), TEST_USER_ID);
        int id = controller.createQuest(quest);
        assertTrue(id > 0, "Quest ID should be positive");
        List<Quest> quests = controller.getQuestsByUserId(TEST_USER_ID);
        boolean found = quests.stream().anyMatch(q -> q.getId() == id);
        assertTrue(found, "Created quest should be retrievable");
    }

    @Test
    public void testUpdateQuest() throws SQLException {
        Quest quest = new Quest(0, "Quest Update", "Initial description", "DAILY", 30, 0, false, LocalDate.now().plusDays(1), TEST_USER_ID);
        int id = controller.createQuest(quest);
        quest.setId(id);
        quest.setDescription("Updated description");
        boolean updated = controller.updateQuest(quest);
        assertTrue(updated, "Quest should be updated");
        List<Quest> quests = controller.getQuestsByUserId(TEST_USER_ID);
        Quest updatedQuest = quests.stream().filter(q -> q.getId() == id).findFirst().orElse(null);
        assertNotNull(updatedQuest, "Updated quest should be found");
        assertEquals("Updated description", updatedQuest.getDescription(), "Description should be updated");
    }

    @Test
    public void testDeleteQuest() throws SQLException {
        Quest quest = new Quest(0, "Quest Delete", "To be deleted", "DAILY", 20, 0, false, LocalDate.now().plusDays(1), TEST_USER_ID);
        int id = controller.createQuest(quest);
        boolean deleted = controller.deleteQuest(id, TEST_USER_ID);
        assertTrue(deleted, "Quest should be deleted");
        List<Quest> quests = controller.getQuestsByUserId(TEST_USER_ID);
        boolean exists = quests.stream().anyMatch(q -> q.getId() == id);
        assertFalse(exists, "Quest should not exist after deletion");
    }

    @Test
    public void testCompleteQuestAddsXP() throws Exception {
        // Create a quest with an XP reward of 40.
        Quest quest = new Quest(0, "Complete Quest", "For XP testing", "DAILY", 40, 0, false, LocalDate.now().plusDays(1), TEST_USER_ID);
        int id = controller.createQuest(quest);
        // Our fake QuestDao.completeQuest always returns false so that the controller’s manual XP addition logic is used.
        boolean completed = controller.completeQuest(id, TEST_USER_ID);
        assertTrue(completed, "completeQuest should return true");
        // Retrieve the fake experience DAO and check XP.
        FakeExperienceDao expDao = (FakeExperienceDao) getPrivateField(controller, "experienceDao");
        int[] exp = expDao.getUserExperience(TEST_USER_ID);
        assertEquals(40, exp[0], "XP added should equal quest's XP reward");
    }

    @Test
    public void testCalculateQuestProgressDailyLog() throws SQLException {
        // For a daily log quest, if at least one transaction exists then progress should be 100.
        Quest quest = new Quest(0, "Daily Log Quest", "Log your expense", "DAILY", 20, 1, false, LocalDate.now().plusDays(1), TEST_USER_ID);
        int progress = controller.calculateQuestProgress(quest, TEST_USER_ID);
        assertEquals(100, progress, "Progress should be 100 when a transaction exists");
    }

    // Helper method to get a private field's value.
    private static Object getPrivateField(Object target, String fieldName) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }
}
