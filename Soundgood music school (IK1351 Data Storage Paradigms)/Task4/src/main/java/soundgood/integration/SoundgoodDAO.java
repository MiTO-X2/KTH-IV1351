/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package soundgood.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import soundgood.model.Instrument;
import soundgood.model.Rental;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class SoundgoodDAO {
    /* INSTRUMENT */
    private static final String INSTRUMENT_TABLE_NAME = "instrument";
    private static final String INSTRUMENT_PK_COLUMN_NAME = "instrument_id";
    private static final String INSTRUMENT_TYPE_COLUMN_NAME = "type";
    private static final String INSTRUMENT_BRAND_COLUMN_NAME = "brand";
    private static final String INSTRUMENT_LOCATION_COLUMN_NAME = "location";
    private static final String INSTRUMENT_PRICE_COLUMN_NAME = "price";

    /* RENTAL */
    private static final String RENTAL_TABLE_NAME = "rental";
    private static final String RENTAL_PK_COLUMN_NAME = "rental_id";
    private static final String RENTAL_START_DATE_COLUMN_NAME = "rental_start_date";
    private static final String RENTAL_END_DATE_COLUMN_NAME = "rental_end_date";
    private static final String RENTAL_DURATION_COLUMN_NAME = "duration";
    private static final String RENTAL_TERMINATED_COLUMN_NAME = "terminated";
    private static final String RENTAL_FK_STUDENT_COLUMN_NAME = "student_id";
    private static final String RENTAL_FK_INSTRUMENT_COLUMN_NAME = "instrument_id";

    public Connection connection;
    private PreparedStatement createRental;
    private PreparedStatement markRentalAsTerminated;
    private PreparedStatement findInstrumentsByAvailability;
    private PreparedStatement findInstrumentsByAvailabilityAndType;
    private PreparedStatement findRentalsByInstrument;
    private PreparedStatement findRentalsByInstrumentForUpdate;
    private PreparedStatement findRentals;
    private PreparedStatement findRentalsByStudentAndStatus;
    private PreparedStatement findRentalsByStudentAndStatusForUpdate;
    /**
     * Constructs a new DAO object connected to the bank database.
     */
    public SoundgoodDAO() throws SoundgoodDBException {
        try {
            connectToSoundgoodDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBException("Could not connect to datasource.", exception);
        }
    }

    /**
     * Rents an instrument to a student and updates the information on the instruments availibility.
     *
     * @param student_id    ID of the student renting the instrument
     * @param instrument_id ID of the instrument
     * @param rentDateDue   The date which the student has to return the instrument at
     * @throws SoundgoodDBException If failed to create rental.
     */
    public void createRental(Integer student_id, Integer instrument_id, String rentDateDue) throws SoundgoodDBException {
        String failureMsg = "Unable to rent for student_id: " + student_id + " and instrument: " + instrument_id + " at the due date of: " + rentDateDue + ".";
        try {
            // Convert the rentDateDue string to Timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(rentDateDue);
            Timestamp rentDateDueTimestamp = new Timestamp(parsedDate.getTime());

            // Set values in the prepared statement
            createRental.setInt(1, student_id);
            createRental.setTimestamp(2, rentDateDueTimestamp); // Set the rental end date as a Timestamp
            createRental.setInt(3, instrument_id);

            int updatedRows = createRental.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException | java.text.ParseException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    public void markRentalAsTerminated(int rentalID) throws SoundgoodDBException {
        String failureMsg = "Could not terminate rental: " + rentalID;
        try {
            markRentalAsTerminated.setInt(1, rentalID);  // Set the rental ID parameter
            int updatedRows = markRentalAsTerminated.executeUpdate();  // Execute the update statement
        
            if (updatedRows != 1) {
                handleException(failureMsg, null);  // Handle case if rental is not found or not updated
            }
        
            connection.commit();  // Commit the transaction if the update was successful
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);  // Handle any SQL exceptions
        }
    }
    
    /**
     * Retrieves all available instruments.
     *
     * @return A list with all available instruments. The list is empty if there are
     *         no
     *         instruments available.
     * @throws SoundgoodDBException If failed to search for available instruments.
     * @throws SQLException
     */
    public List<Instrument> findInstrumentsByAvailability() throws SoundgoodDBException {
        List<Instrument> availableInstruments = new ArrayList<>();
        ResultSet result = null;
        
        try {
            // Execute the query using the prepared statement
            result = findInstrumentsByAvailability.executeQuery();
            
            // Loop through the result set to populate the list of available instruments
            while (result.next()) {
                availableInstruments.add(new Instrument(result.getInt(INSTRUMENT_PK_COLUMN_NAME),
                    result.getString(INSTRUMENT_TYPE_COLUMN_NAME),
                    result.getString(INSTRUMENT_BRAND_COLUMN_NAME),
                    result.getString(INSTRUMENT_LOCATION_COLUMN_NAME),
                    result.getInt(INSTRUMENT_PRICE_COLUMN_NAME)));
            }
        } catch (SQLException e) {
            throw new SoundgoodDBException("Error listing available instruments.", e);
        } finally {
            closeResultSet("Could not list instruments.", result);
        }
        
        return availableInstruments;
    }

    /**
     * Retrieves all available instruments of given type.
     *
     * @return A list with all available instruments. The list is empty if there are
     *         no
     *         instruments available.
     * @throws SoundgoodDBException If failed to search for available instruments.
     */
    public List<Instrument> findInstrumentsByAvailabilityAndType(String type) throws SoundgoodDBException, SQLException {
        ResultSet result = null;
        String failureMsg = "Could not list instruments.";
        List<Instrument> instruments = new ArrayList<>();
        try {
            findInstrumentsByAvailabilityAndType.setInt(1, 0); // 0 means "not terminated"
            findInstrumentsByAvailabilityAndType.setString(2, type); // Type of the instrument, e.g., 'Guitar'
            result = findInstrumentsByAvailabilityAndType.executeQuery();
            while (result.next()) {
                instruments.add(new Instrument(result.getInt(INSTRUMENT_PK_COLUMN_NAME),
                result.getString(INSTRUMENT_TYPE_COLUMN_NAME),
                result.getString(INSTRUMENT_BRAND_COLUMN_NAME),
                result.getString(INSTRUMENT_LOCATION_COLUMN_NAME),
                result.getInt(INSTRUMENT_PRICE_COLUMN_NAME)));
    }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return instruments;
    }
    

    // reads all rentals (history)
    public List<Rental> findRentals() throws SoundgoodDBException {
        String failureMsg = "Could not show rentals";
        List<Rental> rentals = new ArrayList<>();
        ResultSet result = null;
        try {
            result = findRentals.executeQuery();
            while (result.next()) {
                rentals.add(new Rental(result.getInt(RENTAL_PK_COLUMN_NAME),
                        result.getInt(RENTAL_FK_STUDENT_COLUMN_NAME),
                        result.getInt(RENTAL_FK_INSTRUMENT_COLUMN_NAME),
                        result.getTimestamp(RENTAL_START_DATE_COLUMN_NAME),
                        result.getTimestamp(RENTAL_END_DATE_COLUMN_NAME),                        
                        result.getInt(RENTAL_DURATION_COLUMN_NAME),
                        result.getInt(RENTAL_TERMINATED_COLUMN_NAME) == 1)); //convert to boolean
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return rentals;
    }

    // finds current and past rentals for a specific instrument (used in history)
    public List<Rental> findRentalsByInstrument(Integer instrument_id, boolean lockExclusive) throws SoundgoodDBException {
        PreparedStatement stmtToExecute;
        if (lockExclusive) {
            stmtToExecute = findRentalsByInstrumentForUpdate;
        } else {
            stmtToExecute = findRentalsByInstrument;
        }

        String failureMsg = "Could not find rentals";
        List<Rental> rentals = new ArrayList<>();
        ResultSet result = null;

        try {
            stmtToExecute.setInt(1, instrument_id);
            result = stmtToExecute.executeQuery();
            while (result.next()) {
                rentals.add(new Rental(result.getInt(RENTAL_PK_COLUMN_NAME),
                        result.getInt(RENTAL_FK_STUDENT_COLUMN_NAME),
                        result.getInt(RENTAL_FK_INSTRUMENT_COLUMN_NAME),
                        result.getTimestamp(RENTAL_START_DATE_COLUMN_NAME),
                        result.getTimestamp(RENTAL_END_DATE_COLUMN_NAME),
                        result.getInt(RENTAL_DURATION_COLUMN_NAME),
                        result.getInt(RENTAL_TERMINATED_COLUMN_NAME) == 1)); // convert to boolean
            }
            if (!lockExclusive) {
                connection.commit();
            }
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return rentals;
    }

    // finds all of student's rentals, both past and current (used in history)
    public List<Rental> findCurrentRentalsByStudent(Integer student_id, boolean lockExclusive) throws SoundgoodDBException {
        PreparedStatement stmtToExecute;
        if (lockExclusive) {
            stmtToExecute = findRentalsByStudentAndStatusForUpdate;
        } else {
            stmtToExecute = findRentalsByStudentAndStatus;
        }

        String failureMsg = "Could not find rentals";
        List<Rental> rentals = new ArrayList<>();
        ResultSet result = null;

        try {
            stmtToExecute.setInt(1, student_id);
            stmtToExecute.setInt(2, 0);
            result = stmtToExecute.executeQuery();
            while (result.next()) {
                rentals.add(new Rental(result.getInt(RENTAL_PK_COLUMN_NAME),
                        result.getInt(RENTAL_FK_STUDENT_COLUMN_NAME),
                        result.getInt(RENTAL_FK_INSTRUMENT_COLUMN_NAME),
                        result.getTimestamp(RENTAL_START_DATE_COLUMN_NAME),
                        result.getTimestamp(RENTAL_END_DATE_COLUMN_NAME),
                        result.getInt(RENTAL_DURATION_COLUMN_NAME),
                        result.getInt(RENTAL_TERMINATED_COLUMN_NAME) == 1));  // Convert to boolean
            }
            if (!lockExclusive) {
                connection.commit();
            }
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return rentals;
    }

    /**
     * Commits the current transaction.
     * 
     * @throws SoundgoodDBException If unable to commit the current transaction.
     */
    public void commit() throws SoundgoodDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }

    private void connectToSoundgoodDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Leif1", "postgres", "postgres");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {

        createRental = connection.prepareStatement("INSERT INTO " + RENTAL_TABLE_NAME
            + "(" + RENTAL_FK_STUDENT_COLUMN_NAME + ", " + RENTAL_START_DATE_COLUMN_NAME +
            ", " + RENTAL_END_DATE_COLUMN_NAME + ", " + RENTAL_DURATION_COLUMN_NAME
            + ", " + RENTAL_TERMINATED_COLUMN_NAME + ", " + RENTAL_FK_INSTRUMENT_COLUMN_NAME
            + ") VALUES (?, NOW(), ?, 0, 0, ?)");
        

        markRentalAsTerminated = connection.prepareStatement("UPDATE " + RENTAL_TABLE_NAME
            + " SET " + RENTAL_TERMINATED_COLUMN_NAME + " = 1"
            + " WHERE " + RENTAL_PK_COLUMN_NAME + " = ?");

        findInstrumentsByAvailability = connection.prepareStatement("SELECT i.* " +
            "FROM " + INSTRUMENT_TABLE_NAME + " AS i " + "LEFT JOIN " + RENTAL_TABLE_NAME + " AS r ON i." 
            + INSTRUMENT_PK_COLUMN_NAME + " = r." + RENTAL_FK_INSTRUMENT_COLUMN_NAME + " AND r." 
            + RENTAL_TERMINATED_COLUMN_NAME + " = 0 " + "WHERE r." + RENTAL_FK_INSTRUMENT_COLUMN_NAME + " IS NULL " 
            + "ORDER BY i." + INSTRUMENT_PK_COLUMN_NAME + ";");

        findInstrumentsByAvailabilityAndType = connection.prepareStatement("SELECT i." + INSTRUMENT_PK_COLUMN_NAME
            + ", i." + INSTRUMENT_TYPE_COLUMN_NAME + ", i." + INSTRUMENT_BRAND_COLUMN_NAME
            + ", i." + INSTRUMENT_LOCATION_COLUMN_NAME + ", i." + INSTRUMENT_PRICE_COLUMN_NAME
            + " FROM " + INSTRUMENT_TABLE_NAME + " AS i" + " WHERE NOT EXISTS (SELECT r." + RENTAL_FK_INSTRUMENT_COLUMN_NAME
            + " FROM " + RENTAL_TABLE_NAME + " AS r" + " WHERE i." + INSTRUMENT_PK_COLUMN_NAME + " = r." + RENTAL_FK_INSTRUMENT_COLUMN_NAME
            + " AND r." + RENTAL_TERMINATED_COLUMN_NAME + " = ?)"
            + " AND LOWER(i." + INSTRUMENT_TYPE_COLUMN_NAME + ") = LOWER(?)"); // Make it case-insensitive

        findRentalsByStudentAndStatus = connection.prepareStatement("SELECT * FROM rental WHERE " 
            + RENTAL_FK_STUDENT_COLUMN_NAME + " = ? AND " + RENTAL_TERMINATED_COLUMN_NAME 
            + " = ?");

        findRentalsByStudentAndStatusForUpdate = connection.prepareStatement("SELECT * FROM rental WHERE " 
            + RENTAL_FK_STUDENT_COLUMN_NAME + " = ? AND " + RENTAL_TERMINATED_COLUMN_NAME 
            + " = ? FOR UPDATE");


        findRentals = connection.prepareStatement("SELECT * FROM " + RENTAL_TABLE_NAME);

        findRentalsByInstrument = connection.prepareStatement(
                "SELECT * FROM " + RENTAL_TABLE_NAME + " WHERE " + RENTAL_FK_INSTRUMENT_COLUMN_NAME
                        + " = ?");

        findRentalsByInstrumentForUpdate = connection.prepareStatement(
                "SELECT * FROM " + RENTAL_TABLE_NAME + " WHERE " + RENTAL_FK_INSTRUMENT_COLUMN_NAME
                        + " = ? FOR UPDATE");

    }

    private void handleException(String failureMsg, Exception cause) throws SoundgoodDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg + ". Also failed to rollback transaction because of: "
                    + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new SoundgoodDBException(failureMsg, cause);
        } else {
            throw new SoundgoodDBException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws SoundgoodDBException {
        if (result != null) {
                try {
                result.close();
            } catch (Exception e) {
                throw new SoundgoodDBException(failureMsg + " Could not close result set.", e);
            }
        }
    }
}