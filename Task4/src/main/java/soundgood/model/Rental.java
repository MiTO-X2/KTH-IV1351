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

package soundgood.model;
import java.sql.Timestamp;

/**
 * Represents a rental record in the Soundgood music school system.
 */
public class Rental implements RentalDTO{
    private int rentalID;
    private int studentID;
    private int instrumentID;
    private Timestamp rentalStartDate;
    private Timestamp rentalEndDate;
    private int duration;
    private boolean terminated;

    /**
     * Creates a new Rental instance.
     *
     * @param rentalID        The unique ID of the rental.
     * @param studentID       The ID of the student renting the instrument.
     * @param instrumentID    The ID of the rented instrument.
     * @param rentalStartDate The start date of the rental. 
     * @param rentalEndDate   The end date of the rental.
     * @param duration        The duration of the rental in days.
     * @param terminated      The termination of the rental.
     */
    public Rental(int rentalID, int studentID, int instrumentID, Timestamp rentalStartDate, Timestamp rentalEndDate, int duration, boolean terminated) {
        this.rentalID = rentalID;
        this.studentID = studentID;
        this.instrumentID = instrumentID;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.duration = duration;
        this.terminated = terminated; // Converts 1 to `true` and 0 to `false`.
    }

    /**
     * @return The unique ID of the rental.
     */
    public Integer getRentalID() {
        return rentalID;
    }

    /**
     * @return The ID of the student renting the instrument.
     */
    public Integer getRentalStudentID() {
        return studentID;
    }

    /**
     * @return The ID of the rented instrument.
     */
    public Integer getRentalInstrumentID() {
        return instrumentID;
    }

    /**
     * @return The start date of the rental.
     */
    public Timestamp getRentalStartDate() {
        return rentalStartDate;
    }

    /**
     * @return The end date of the rental.
     */
    public Timestamp getRentalEndDate() {
        return rentalEndDate;
    }

    /**
     * @return The duration of the rental in days.
     */
    public Integer getRentalDuration() {
        return duration;
    }

    public boolean getRentalTerminated() {
        return terminated;
    }

    /**
     * @return A string representation of all fields in this rental record.
     */
    @Override
    public String toString() {
        return "Rental: [ID=" + rentalID + ", StudentID=" + studentID + ", InstrumentID=" + instrumentID +
               ", StartDate=" + rentalStartDate + ", EndDate=" + rentalEndDate +
               ", Duration=" + duration + ", Terminated=" + terminated + "]";
    }
}
