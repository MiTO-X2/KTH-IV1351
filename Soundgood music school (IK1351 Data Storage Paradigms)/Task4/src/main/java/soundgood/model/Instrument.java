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

 /**
  * An account in the bank.
  */
    public class Instrument implements InstrumentDTO {
        private int instrumentID;
        private String type;
        private String brand;
        private String location;
        private int price;

   
     /**
      * Creates an account for the specified holder with the specified balance and account
      * number.
      *
      * @param instrumentID     The instrument ID.
      * @param type             The instrument type.
      * @param brand            The instrument brand.
      * @param price            The rental fee.
      * @param location            The location of the instrument
      */

     public Instrument(int instrumentID, String type, String brand, String location, int price) {
         this.instrumentID = instrumentID;
         this.type = type;
         this.brand = brand;
         this.location = location;
         this.price = price;
     }
 
     /**
      * @return The ID.
      */
     public Integer getInstrumentID() {
         return instrumentID; 
         }
 
     /**
      * @return The type.
      */
     public String getInstrumentType() {
         return type;
         }
 
     /**
      * @return The brand.
      */
     public String getInstrumentBrand() {
         return brand;
         }

     /**
      * @return The brand.
      */
     public String getInstrumentLocation() {
         return location;
         }

     /**
      * @return The price.
      */
     public Integer getInstrumentPrice() {
        return price;
        }

     /**
      * @return A string representation of all fields in this object.
      */
     @Override
    public String toString() {
        return "Instrument: [ID=" + instrumentID + ", Type=" + type + ", Brand=" + brand +
                ", Location=" + location + ", Price=" + price + "]";
        }
    }

    

 