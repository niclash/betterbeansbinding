/***********************************************************************************************************************
 * 
 * BetterBeansBinding - keeping JavaBeans in sync
 * ==============================================
 * 
 * Copyright (C) 2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://betterbeansbinding.kenai.com
 * 
 * This is derived work from BeansBinding: http://beansbinding.dev.java.net
 * BeansBinding is copyrighted (C) by Sun Microsystems, Inc.
 * 
 ***********************************************************************************************************************
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 ***********************************************************************************************************************
 * 
 * $Id: PropertyStateListener.java 33 2009-04-25 16:43:54Z fabriziogiudici $
 * 
 **********************************************************************************************************************/
package org.jdesktop.beansbinding;

import java.util.EventListener;

/**
 * {@code PropertyStateListeners} are registerd on {@link org.jdesktop.beansbinding.Property}
 * instances, to be notified when the state of the property changes.
 *
 * @author Shannon Hickey
 */
public interface PropertyStateListener extends EventListener {

    /**
     * Called to notify the listener that a change of state has occurred to
     * one of the {@code Property} instances upon which the listener is registered.
     *
     * @param pse an event describing the state change, {@code non-null}
     */
    public void propertyStateChanged(PropertyStateEvent pse);

}
