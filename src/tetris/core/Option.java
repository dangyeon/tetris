/*  Copyright (c) 2012 All Right Reserved
 *
 *  This source is subject to the GNU general public License.  Please see the
 *  gpl.txt file for more information.  All other rights reserved.
 *
 *  @file:   $File$
 *  @brief:  the core class of whole project, connects EntryPoint to the UI and logic part
 *  @author: $Author$
 *  @date:   $Date$
 */

package tetris.core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Option {
    private SimpleIntegerProperty columnNumberProperty = new SimpleIntegerProperty();
    private SimpleIntegerProperty rowNumberProperty    = new SimpleIntegerProperty();
    private SimpleIntegerProperty frameRateProperty    = new SimpleIntegerProperty();

    public IntegerProperty columnNumberProperty() {
        return columnNumberProperty;
    }


    public IntegerProperty rowNumberProperty() {
        return rowNumberProperty;
    }

    public IntegerProperty frameRateProperty() {
        return frameRateProperty;
    }
}