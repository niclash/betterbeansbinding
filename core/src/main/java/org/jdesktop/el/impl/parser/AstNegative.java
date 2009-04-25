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
 * $Id: $
 * 
 **********************************************************************************************************************/
/*
 * Copyright (C) 2007 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 *
 *//* Generated By:JJTree: Do not edit this line. AstNegative.java */

package org.jdesktop.el.impl.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.jdesktop.el.ELContext;

import org.jdesktop.el.ELException;

import org.jdesktop.el.impl.lang.EvaluationContext;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: kchung $
 */
public final class AstNegative extends SimpleNode {
    public AstNegative(int id) {
        super(id);
    }

    public Class getType(EvaluationContext ctx)
            throws ELException {
        return Number.class;
    }

    public Object getValue(EvaluationContext ctx)
            throws ELException {
        Object obj = this.children[0].getValue(ctx);

        if (obj == ELContext.UNRESOLVABLE_RESULT) {
            return ELContext.UNRESOLVABLE_RESULT;
        }
        if (obj == null) {
            return new Long(0);
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).negate();
        }
        if (obj instanceof BigInteger) {
            return ((BigInteger) obj).negate();
        }
        if (obj instanceof String) {
            if (isStringFloat((String) obj)) {
                return new Double(-Double.parseDouble((String) obj));
            }
            return new Long(-Long.parseLong((String) obj));
        }
        Class type = obj.getClass();
        if (obj instanceof Long || Long.TYPE == type) {
            return new Long(-((Long) obj).longValue());
        }
        if (obj instanceof Double || Double.TYPE == type) {
            return new Double(-((Double) obj).doubleValue());
        }
        if (obj instanceof Integer || Integer.TYPE == type) {
            return new Integer(-((Integer) obj).intValue());
        }
        if (obj instanceof Float || Float.TYPE == type) {
            return new Float(-((Float) obj).floatValue());
        }
        if (obj instanceof Short || Short.TYPE == type) {
            return new Short((short) -((Short) obj).shortValue());
        }
        if (obj instanceof Byte || Byte.TYPE == type) {
            return new Byte((byte) -((Byte) obj).byteValue());
        }
        Long num = (Long) coerceToNumber(obj, Long.class);
        return new Long(-num.longValue());
    }
}
