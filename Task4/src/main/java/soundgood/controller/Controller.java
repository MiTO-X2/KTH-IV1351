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

package soundgood.controller;

import java.util.List;

// import javax.security.auth.login.AccountException;

import soundgood.integration.SoundgoodDAO;
import soundgood.integration.SoundgoodDBException;
import soundgood.model.InstrumentDTO;
import soundgood.model.InstrumentException;
import soundgood.model.RentalDTO;
import soundgood.model.RentalException;
import soundgood.model.Rental;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final SoundgoodDAO soundgoodDB;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws SoundgoodDBException If unable to connect to the database.
     */
    public Controller() throws SoundgoodDBException {
        soundgoodDB = new SoundgoodDAO();
    }

    /**
     * Creates a new rental for the specified instrument and student ID.
     * 
     * @param student_id    The student ID.
     * @param instrument_id The instrument ID.
     * @throws InstrumentException  If unable to rent instrument.
     * @throws SoundgoodDBException
     */
    public void createRental(Integer student_id, Integer instrument_id, String end_date)
            throws InstrumentException, SoundgoodDBException {
        String failureMsg = "Could not create rental for student_id: " + student_id + " and instrument: "
                + instrument_id + ".";

        if (student_id == null || instrument_id == null) {
            throw new InstrumentException(failureMsg);
        }
        // checks if student is already renting 2 instruments
        List<Rental> rentals = soundgoodDB.findCurrentRentalsByStudent(student_id, true);
        if (rentals.size() >= 2) {
            throw new InstrumentException("Rent quota reached");
        }
        // checks if instrument is already rented
        rentals = soundgoodDB.findRentalsByInstrument(instrument_id, true);
        for (Rental r : rentals) {
            if (!r.getRentalTerminated()) {
                throw new InstrumentException("Instrument already rented");
            }
        }

        try {
            soundgoodDB.createRental(student_id, instrument_id, end_date);
        } catch (SoundgoodDBException e) {
            throw new InstrumentException(failureMsg, e);
        }  catch (Exception d) {
            commitOngoingTransaction(failureMsg);
            throw d;
        }
    }

    /**
     * Terminates rental with the specified rental ID.
     * 
     * @param rental_id The ID of the rental that shall be terminated.
     * @throws InstrumentException If failed to terminate the specified rental.
     */
    public void markRentalAsTerminated(Integer rental_id) throws InstrumentException {
        String failureMsg = "Could not terminate rental: " + rental_id;

        if (rental_id == null) {
            throw new InstrumentException(failureMsg);
        }

        try {
            soundgoodDB.markRentalAsTerminated(rental_id);
            } catch (Exception e) {
                throw new InstrumentException(failureMsg, e);
                }
        }

    /**
     * Lists all students who are not allowed to rent more instruments.
     * 
     * @return A list containing all students who rent two instruments. The list
     *         is empty if there are no such students.
     * @throws InstrumentException If unable to retrieve accounts.
     */
    public List<? extends InstrumentDTO> findInstrumentsByAvailability() throws InstrumentException {
        try {
            return soundgoodDB.findInstrumentsByAvailability();
        } catch (Exception e) {
            throw new InstrumentException("Unable to retrieve instruments.", e);
        }
    }

    // Returns whole rental history
    public List<? extends RentalDTO> findRentals() throws RentalException {
        try {
            return soundgoodDB.findRentals();
        } catch (Exception e) {
            throw new RentalException("Unable to show rentals", e);
        }
    }

    public List<? extends InstrumentDTO> findInstrumentsByAvailabilityAndType(String type) throws InstrumentException {
        try {
            return soundgoodDB.findInstrumentsByAvailabilityAndType(type);
        } catch (Exception e) {
            throw new InstrumentException("Unable to retrieve instruments.", e);
        }
    }


    /*private void commitOngoingTransaction(String failureMsg) throws AccountException {
        try {
            bankDb.commit();
        } catch (BankDBException bdbe) {
            throw new AccountException(failureMsg, bdbe);
        }
    }*/

    private void commitOngoingTransaction(String failureMsg) throws InstrumentException {
        try {
            soundgoodDB.commit();
        } catch (SoundgoodDBException bdbe) {
            throw new InstrumentException(failureMsg, bdbe);
        }
    }
}
