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
 * Specifies a read-only view of an instrument.
 */
public interface RentalDTO {
    /**
     * @return The rental ID.
     */
    Integer getRentalID(); 

    /**
     * @return The rental student.
     */
    Integer getRentalStudentID();

    /**
     * @return The rental instrument.
     */
    Integer getRentalInstrumentID();

    /**
     * @return The rental start date.
     */
    Timestamp getRentalStartDate();

    /**
     * @return The rental end date.
     */
    Timestamp getRentalEndDate();

    /**
     * @return The rental duration.
     */
    Integer getRentalDuration();

    /**
     * @return The rental status.
     **/
    boolean getRentalTerminated();
}
