/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package philips.appframeworklibrary.flowmanager.exceptions;

public class NullEventException extends RuntimeException {

    public NullEventException() {
        super("Null Event Found");
    }
}
