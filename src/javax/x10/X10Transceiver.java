/*
 * X10Transceiver.java
 *
 * Created on July 15, 2002, 11:27 PM
 */

package javax.x10;

/** This interface simply joins the two interfaces it extends,
 * {@link javax.x10.X10Transmitter} and
 * {@link javax.x10.X10Receiver}, and defines no other new
 * methods.  <code>X10Transceiver</code>s are expected to receive events they
 * transmit, regardless of what underlying hardware does.
 *
 * @see javax.x10.X10Transmitter
 * @see javax.x10.X10Receiver
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public interface X10Transceiver extends X10Transmitter, X10Receiver {
}
