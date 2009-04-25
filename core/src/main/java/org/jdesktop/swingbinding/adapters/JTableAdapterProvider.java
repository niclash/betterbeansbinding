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
 * $Id: JTableAdapterProvider.java 34 2009-04-25 17:27:10Z fabriziogiudici $
 * 
 **********************************************************************************************************************/
package org.jdesktop.swingbinding.adapters;

import java.beans.*;
import javax.swing.*;
import org.jdesktop.beansbinding.ext.BeanAdapterProvider;
import org.jdesktop.swingbinding.impl.ListBindingManager;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Shannon Hickey
 */
public final class JTableAdapterProvider implements BeanAdapterProvider {

    private static final String SELECTED_ELEMENT_P = "selectedElement";
    private static final String SELECTED_ELEMENTS_P = "selectedElements";
    private static final String SELECTED_ELEMENT_IA_P = SELECTED_ELEMENT_P + "_IGNORE_ADJUSTING";
    private static final String SELECTED_ELEMENTS_IA_P = SELECTED_ELEMENTS_P + "_IGNORE_ADJUSTING";

    private static boolean IS_JAVA_15 =
        System.getProperty("java.version").startsWith("1.5");

    public final class Adapter extends BeanAdapterBase {
        private JTable table;
        private Handler handler;
        private Object cachedElementOrElements;

        private Adapter(JTable table, String property) {
            super(property);
            this.table = table;
        }

        private boolean isPlural() {
            return property == SELECTED_ELEMENTS_P || property == SELECTED_ELEMENTS_IA_P;
        }
        
        public Object getSelectedElement() {
            return JTableAdapterProvider.getSelectedElement(table);
        }
        
        public Object getSelectedElement_IGNORE_ADJUSTING() {
            return getSelectedElement();
        }

        public List<Object> getSelectedElements() {
            return JTableAdapterProvider.getSelectedElements(table);
        }

        public List<Object> getSelectedElements_IGNORE_ADJUSTING() {
            return getSelectedElements();
        }

        protected void listeningStarted() {
            handler = new Handler();
            cachedElementOrElements = isPlural() ?
                getSelectedElements() : JTableAdapterProvider.getSelectedElement(table);
            table.addPropertyChangeListener("selectionModel", handler);
            table.getSelectionModel().addListSelectionListener(handler);
        }
        
        protected void listeningStopped() {
            table.getSelectionModel().removeListSelectionListener(handler);
            table.removePropertyChangeListener("selectionModel", handler);
            cachedElementOrElements = null;
            handler = null;
        }

        private class Handler implements ListSelectionListener, PropertyChangeListener {
            private void tableSelectionChanged() {
                Object oldElementOrElements = cachedElementOrElements;
                cachedElementOrElements = getSelectedElements();
                firePropertyChange(oldElementOrElements, cachedElementOrElements);
            }

            public void valueChanged(ListSelectionEvent e) {
                if ((property == SELECTED_ELEMENT_IA_P || property == SELECTED_ELEMENTS_IA_P)
                        && e.getValueIsAdjusting()) {

                    return;
                }

                tableSelectionChanged();
            }
            
            public void propertyChange(PropertyChangeEvent pce) {
                ((ListSelectionModel)pce.getOldValue()).removeListSelectionListener(handler);
                ((ListSelectionModel)pce.getNewValue()).addListSelectionListener(handler);
                tableSelectionChanged();
            }
        }
    }

    private static int viewToModel(JTable table, int index) {
        // deal with sorting & filtering in 6.0 and up
        if (!IS_JAVA_15) {
            try {
                java.lang.reflect.Method m = table.getClass().getMethod("convertRowIndexToModel", int.class);
                index = (Integer)m.invoke(table, index);
            } catch (NoSuchMethodException nsme) {
                throw new AssertionError(nsme);
            } catch (IllegalAccessException iae) {
                throw new AssertionError(iae);
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }

        return index;
    }

    private static int modelToView(JTable table, int index) {
        // deal with sorting & filtering in 6.0 and up
        if (!IS_JAVA_15) {
            try {
                java.lang.reflect.Method m = table.getClass().getMethod("convertRowIndexToView", int.class);
                index = (Integer)m.invoke(table, index);
            } catch (NoSuchMethodException nsme) {
                throw new AssertionError(nsme);
            } catch (IllegalAccessException iae) {
                throw new AssertionError(iae);
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }

        return index;
    }
    
    private static List<Object> getSelectedElements(JTable table) {
        assert table != null;

        ListSelectionModel selectionModel = table.getSelectionModel();
        int min = selectionModel.getMinSelectionIndex();
        int max = selectionModel.getMaxSelectionIndex();

        List<Object> newSelection;

        if (min < 0 || max < 0) {
            return new ArrayList<Object>(0);
        }
        
        ArrayList<Object> elements = new ArrayList<Object>(max - min + 1);

        for (int i = min; i <= max; i++) {
            if (selectionModel.isSelectedIndex(i)) {
                elements.add(getElement(table, i));
            }
        }
        
        return elements;
    }

    private static Object getSelectedElement(JTable table) {
        assert table != null;

        // PENDING(shannonh) - more cases to consider
        int index = table.getSelectionModel().getLeadSelectionIndex();
        index = table.getSelectionModel().isSelectedIndex(index) ?
            index : table.getSelectionModel().getMinSelectionIndex();
        
        if (index == -1) {
            return null;
        }

        return getElement(table, index);
    }

    private static Object getElement(JTable table, int index) {
        index = viewToModel(table, index);
        
        TableModel model = table.getModel();
        if (model instanceof ListBindingManager) {
            return ((ListBindingManager)model).getElement(index);
        } else {
            int columnCount = model.getColumnCount();
            // PENDING(shannonh) - need to support editing values in this map!
            HashMap map = new HashMap(columnCount);
            for (int i = 0; i < columnCount; i++) {
                map.put("column" + i, model.getValueAt(index, i));
            }
            return map;
        }
    }

    public boolean providesAdapter(Class<?> type, String property) {
        if (!JTable.class.isAssignableFrom(type)) {
            return false;
        }

        property = property.intern();

        return property == SELECTED_ELEMENT_P ||
               property == SELECTED_ELEMENT_IA_P ||
               property == SELECTED_ELEMENTS_P ||
               property == SELECTED_ELEMENTS_IA_P;
                 
    }

    public Object createAdapter(Object source, String property) {
        if (!providesAdapter(source.getClass(), property)) {
            throw new IllegalArgumentException();
        }
        
        return new Adapter((JTable)source, property);
    }
    
    public Class<?> getAdapterClass(Class<?> type) {
        return JTable.class.isAssignableFrom(type) ?
            JTableAdapterProvider.Adapter.class :
            null;
    }
    
}
