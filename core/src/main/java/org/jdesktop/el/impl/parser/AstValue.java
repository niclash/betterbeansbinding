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
 *//* Generated By:JJTree: Do not edit this line. AstValue.java */

package org.jdesktop.el.impl.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdesktop.el.ELContext;
import org.jdesktop.el.ELException;
import org.jdesktop.el.ELResolver;
import org.jdesktop.el.MethodInfo;
import org.jdesktop.el.PropertyNotFoundException;

import org.jdesktop.el.impl.lang.EvaluationContext;
import org.jdesktop.el.impl.util.MessageFactory;
import org.jdesktop.el.impl.util.ReflectionUtil;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: kchung $
 */
public final class AstValue extends SimpleNode {

    protected static class Target {
        protected Object base;

        protected Object property;
    }

    public AstValue(int id) {
        super(id);
    }

    public Class getType(EvaluationContext ctx) throws ELException {
        Target t = getTarget(ctx);
        ctx.setPropertyResolved(false);
        return ctx.getELResolver().getType(ctx, t.base, t.property);
    }

    private final Target getTarget(EvaluationContext ctx) throws ELException {
        // evaluate expr-a to value-a
        Object base = this.children[0].getValue(ctx);

        // if our base is null (we know there are more properites to evaluate)
        if (base == null || base == ELContext.UNRESOLVABLE_RESULT) {
            throw new PropertyNotFoundException(MessageFactory.get(
                    "error.unreachable.base", this.children[0].getImage()));
        }

        // set up our start/end
        Object property = null;
        int propCount = this.jjtGetNumChildren() - 1;
        int i = 1;

        // evaluate any properties before our target
        ELResolver resolver = ctx.getELResolver();
        if (propCount > 1) {
            while (base != null && base != ELContext.UNRESOLVABLE_RESULT && i < propCount) {
                property = this.children[i].getValue(ctx);
                ctx.setPropertyResolved(false);
                base = resolver.getValue(ctx, base, property);
                i++;
            }
            // if we are in this block, we have more properties to resolve,
            // but our base was null
            if (base == ELContext.UNRESOLVABLE_RESULT || base == null || property == null) {
                throw new PropertyNotFoundException(MessageFactory.get(
                        "error.unreachable.property", property));
            }
        }

        property = this.children[i].getValue(ctx);

        if (property == null) {
            throw new PropertyNotFoundException(MessageFactory.get(
                    "error.unreachable.property", this.children[i]));
        }

        Target t = new Target();
        t.base = base;
        t.property = property;
        return t;
    }

    public Object getValue(EvaluationContext ctx) throws ELException {
        Object base = this.children[0].getValue(ctx);
        int propCount = this.jjtGetNumChildren();
        if (base == ELContext.UNRESOLVABLE_RESULT || (base == null && propCount > 1)) {
            ctx.clearResolvedProperties();
            return ELContext.UNRESOLVABLE_RESULT;
        }
        int i = 1;
        Object property = null;
        ELResolver resolver = ctx.getELResolver();
        while (base != null && i < propCount) {
            property = this.children[i].getValue(ctx);
            if (property == null) {
                return null;
            } else {
                ctx.setPropertyResolved(false);
                Object origBase = base;
                base = resolver.getValue(ctx, base, property);
                if (base == ELContext.UNRESOLVABLE_RESULT) {
                    ctx.clearResolvedProperties();
                    return base;
                } else {
                    ctx.resolvedProperty(origBase, property);
                }
            }
            i++;
        }
        if (base == null && i < propCount) {
            ctx.clearResolvedProperties();
            return ELContext.UNRESOLVABLE_RESULT;
        }
        return base;
    }

    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        Target t = getTarget(ctx);
        ctx.setPropertyResolved(false);
        return ctx.getELResolver().isReadOnly(ctx, t.base, t.property);
    }

    public void setValue(EvaluationContext ctx, Object value)
            throws ELException {
        Target t = getTarget(ctx);
        ctx.setPropertyResolved(false);
        ctx.getELResolver().setValue(ctx, t.base, t.property, value);
    }

    public MethodInfo getMethodInfo(EvaluationContext ctx, Class[] paramTypes)
            throws ELException {
        Target t = getTarget(ctx);
        Method m = ReflectionUtil.getMethod(t.base, t.property, paramTypes);
        return new MethodInfo(m.getName(), m.getReturnType(), m
                .getParameterTypes());
    }

    public Object invoke(EvaluationContext ctx, Class[] paramTypes,
            Object[] paramValues) throws ELException {
        Target t = getTarget(ctx);
        Method m = ReflectionUtil.getMethod(t.base, t.property, paramTypes);
        Object result = null;
        try {
            result = m.invoke(t.base, (Object[]) paramValues);
        } catch (IllegalAccessException iae) {
            throw new ELException(iae);
        } catch (InvocationTargetException ite) {
            throw new ELException(ite.getCause());
        }
        return result;
    }
}
