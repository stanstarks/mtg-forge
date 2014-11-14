/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.toolbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// based on code from http://tips4java.wordpress.com/2009/11/08/tooltips-and-scrollpanes/

/**
 *  This class is used to help generate tooltips on components added to a
 *  scrollpane. Generally tooltips are generated as the mouse if moved over
 *  components that display tooltips. On complex component, like a JTable
 *  the component can generate multiple tooltips depending on which cell the
 *  mouse is positioned over.
 *
 *  However, when the viewport of the scrollpane is moved and the mouse is
 *  not moved the tooltip is not updated even though the mouse is positioned
 *  over a different cell. This might happen for example when the mouse wheel
 *  is used to scroll the viewport.
 *
 *  To force updating of the tooltip, this class will generate a phantom
 *  mouseMoved event which is passed to the ToolTipManager.
 *
 *  This class is actually a 3 in 1 listener and will work slightly different
 *  depending on how it is being used. When used as a:
 *
 *  a) MouseWheelListener - it is added to the scrollpane. In this case the
 *     mouseMoved events are only generated by scrolling of the mouse wheel
 *     and therefore only supports vertical movement of the viewport
 *  b) AdjustmentListener - is added to the vertical and/or horizontal scrollbar.
 *     In this case the viewport can be scrolled by using the mouse wheel or
 *     the keyboard and mouseMoved events will be generated.
 *  c) ComponentListener - it is added to the component. In this case all forms
 *     of viewport movement as well as changes in the component size will cause
 *     the mouseMoved event to be generated.
 *
 */
public class ToolTipListener
    implements ComponentListener, MouseWheelListener, AdjustmentListener {
    /**
     *  Create a mouseMoved event to pass to the ToolTipManager.
     */
    private void phantomMouseMoved(Component component)
    {
        if (null == component) {
            return;
        }

        // mouse is in the bounds of the component, generate phantom
        // mouseMoved event for the ToolTipManager
        Point mouseLocation = component.getMousePosition();
        if (mouseLocation != null)
        {
            MouseEvent phantom = new MouseEvent(
                component,
                MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(),
                0,
                mouseLocation.x,
                mouseLocation.y,
                0,
                false);

            ToolTipManager.sharedInstance().mouseMoved(phantom);
        }
    }

    // implement ComponentListener
    public void componentMoved(ComponentEvent e)
    {
        phantomMouseMoved(e.getComponent());
    }

    public void componentResized(ComponentEvent e)
    {
        phantomMouseMoved(e.getComponent());
    }

    public void componentHidden(ComponentEvent e) { }
    public void componentShown(ComponentEvent e)  { }
    
    // implement MouseWheelListener
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        JScrollPane scrollPane = (JScrollPane)e.getSource();
        phantomMouseMoved(scrollPane.getViewport().getView());
    }

    // implement AdjustmentListener
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        JScrollBar scrollBar = (JScrollBar)e.getSource();
        JScrollPane scrollPane = (JScrollPane)scrollBar.getParent();
        phantomMouseMoved(scrollPane.getViewport().getView());
    }
}
