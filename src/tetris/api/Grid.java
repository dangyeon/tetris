/*  Copyright (c) 2012 All Right Reserved
 *
 *  This source is subject to the GNU general public License.  Please see the
 *  gpl.txt file for more information.  All other rights reserved.
 *
 *  @file:   $File$
 *  @brief:  interface for the playfield of tetrominos
 *  @author: $Author$
 *  @date:   $Date$
 */

package tetris.api;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import tetris.tetrominos.Mino;

public interface Grid {
    public Mino get(int x, int y);

    public Mino[] allocateMinos(int number);

    public AnchorPane toJavaFXNode();

    public ReadOnlyDoubleProperty minoHeighthProperty();

    public ReadOnlyDoubleProperty minoWidthProperty();

    public boolean isAccessible(int x, int y);

    public int getColumnNo();

    public int getRowNo();

    public int squeeze();

    public void addMino(Mino c);

    public void recoverAllocatedMinos();

    public void removeMino(Mino c);

    public void set(int x, int y, Mino c);

    public void unset(int x, int y);

    public void unsetAllCooridinate();
}
