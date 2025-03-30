package database;

import java.sql.*;
import java.util.Map;
import java.util.Calendar;

public class StubResultSet implements ResultSet {
    private final Map<Integer, Map<String, Object>> users;
    private final Integer userId;
    private boolean isBeforeFirst = true;
    private boolean isAfterLast = false;
    private boolean isClosed = false;

    public StubResultSet(Map<Integer, Map<String, Object>> users) {
        this.users = users;
        this.userId = null;
    }

    public StubResultSet(Map<Integer, Map<String, Object>> users, Integer userId) {
        this.users = users;
        this.userId = userId;
    }

    @Override
    public boolean next() throws SQLException {
        if (isClosed) {
            throw new SQLException("ResultSet is closed");
        }
        if (userId != null) {
            if (isBeforeFirst) {
                isBeforeFirst = false;
                if (users.containsKey(userId)) {
                    return true;
                }
            }
            isAfterLast = true;
            return false;
        }
        return false;
    }

    @Override
    public void close() throws SQLException {
        isClosed = true;
    }

    @Override
    public boolean wasNull() throws SQLException {
        return false;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnIndex) {
            case 1: // id
                return String.valueOf(userId);
            case 2: // email
                return (String) userData.get("email");
            case 3: // password
                return (String) userData.get("password");
            case 4: // points
                return String.valueOf(userData.get("points"));
            case 5: // balance
                return String.valueOf(userData.get("balance"));
            default:
                throw new SQLException("Invalid column index: " + columnIndex);
        }
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnIndex) {
            case 1: // id
                return userId;
            case 4: // points
                return (Integer) userData.get("points");
            default:
                throw new SQLException("Invalid column index: " + columnIndex);
        }
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnIndex) {
            case 5: // balance
                return (Double) userData.get("balance");
            default:
                throw new SQLException("Invalid column index: " + columnIndex);
        }
    }

    @Override
    public java.math.BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnLabel.toLowerCase()) {
            case "id":
                return String.valueOf(userId);
            case "email":
                return (String) userData.get("email");
            case "password":
                return (String) userData.get("password");
            default:
                throw new SQLException("Invalid column label: " + columnLabel);
        }
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnLabel.toLowerCase()) {
            case "id":
                return userId;
            case "points":
                return (Integer) userData.get("points");
            default:
                throw new SQLException("Invalid column label: " + columnLabel);
        }
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnLabel.toLowerCase()) {
            case "balance":
                return (Double) userData.get("balance");
            default:
                throw new SQLException("Invalid column label: " + columnLabel);
        }
    }

    @Override
    public java.math.BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.sql.SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        // No-op for stub
    }

    @Override
    public String getCursorName() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        switch (columnLabel.toLowerCase()) {
            case "id":
                return 1;
            case "email":
                return 2;
            case "password":
                return 3;
            case "points":
                return 4;
            case "balance":
                return 5;
            default:
                throw new SQLException("Invalid column label: " + columnLabel);
        }
    }

    @Override
    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.math.BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.math.BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return isBeforeFirst;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return isAfterLast;
    }

    @Override
    public boolean isFirst() throws SQLException {
        return !isBeforeFirst && !isAfterLast;
    }

    @Override
    public boolean isLast() throws SQLException {
        return !isBeforeFirst && isAfterLast;
    }

    @Override
    public void beforeFirst() throws SQLException {
        isBeforeFirst = true;
        isAfterLast = false;
    }

    @Override
    public void afterLast() throws SQLException {
        isBeforeFirst = false;
        isAfterLast = true;
    }

    @Override
    public boolean first() throws SQLException {
        isBeforeFirst = false;
        isAfterLast = false;
        return true;
    }

    @Override
    public boolean last() throws SQLException {
        isBeforeFirst = false;
        isAfterLast = true;
        return true;
    }

    @Override
    public int getRow() throws SQLException {
        if (isBeforeFirst || isAfterLast) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        if (row == 0) {
            beforeFirst();
            return false;
        } else if (row == 1) {
            first();
            return true;
        } else {
            afterLast();
            return false;
        }
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        if (isBeforeFirst || isAfterLast) {
            return false;
        }
        if (rows == 0) {
            return true;
        }
        afterLast();
        return false;
    }

    @Override
    public boolean previous() throws SQLException {
        if (isBeforeFirst) {
            return false;
        }
        if (isAfterLast) {
            isAfterLast = false;
            return true;
        }
        isBeforeFirst = true;
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        // No-op for stub
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        // No-op for stub
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBigDecimal(int columnIndex, java.math.BigDecimal x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBigDecimal(String columnLabel, java.math.BigDecimal x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void refreshRow() throws SQLException {
        // No-op for stub
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        // No-op for stub
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        // No-op for stub
    }

    @Override
    public Statement getStatement() throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.net.URL getURL(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.net.URL getURL(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBlob(int columnIndex, java.io.InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBlob(String columnLabel, java.io.InputStream inputStream, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateClob(int columnIndex, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateClob(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNClob(int columnIndex, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNClob(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBlob(int columnIndex, java.io.InputStream inputStream) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateBlob(String columnLabel, java.io.InputStream inputStream) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateClob(int columnIndex, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateClob(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNClob(int columnIndex, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public void updateNClob(String columnLabel, java.io.Reader reader) throws SQLException {
        throw new SQLException("Not supported in stub implementation");
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnIndex) {
            case 1: // id
                return type.cast(userId);
            case 2: // email
                return type.cast(userData.get("email"));
            case 3: // password
                return type.cast(userData.get("password"));
            case 4: // points
                return type.cast(userData.get("points"));
            case 5: // balance
                return type.cast(userData.get("balance"));
            default:
                throw new SQLException("Invalid column index: " + columnIndex);
        }
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        if (userId == null) {
            throw new SQLException("No current row");
        }
        Map<String, Object> userData = users.get(userId);
        if (userData == null) {
            throw new SQLException("User not found");
        }
        switch (columnLabel.toLowerCase()) {
            case "id":
                return type.cast(userId);
            case "email":
                return type.cast(userData.get("email"));
            case "password":
                return type.cast(userData.get("password"));
            case "points":
                return type.cast(userData.get("points"));
            case "balance":
                return type.cast(userData.get("balance"));
            default:
                throw new SQLException("Invalid column label: " + columnLabel);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }
} 